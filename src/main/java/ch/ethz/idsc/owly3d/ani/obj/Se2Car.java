// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.demo.se2.Se2CarIntegrator;
import ch.ethz.idsc.owly.demo.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.owly3d.ani.SE3Interface;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.Rodriguez;

public class Se2Car implements Animated, SE3Interface {
  private static final Tensor U_NULL = Array.zeros(3).unmodifiable();
  // ---
  private final EpisodeIntegrator pushIntegrator = new SimpleEpisodeIntegrator( //
      Rice1StateSpaceModel.of(RealScalar.of(1.2)), //
      EulerIntegrator.INSTANCE, //
      new StateTime(U_NULL, RealScalar.ZERO));
  private Tensor u = U_NULL;
  // (x-y-theta,...)
  private final EpisodeIntegrator se2Integrator;
  private final Scalar maxSpeed;

  // ---
  /** @param state {x, y, theta} */
  public Se2Car(Tensor state, Scalar maxSpeed) {
    se2Integrator = new SimpleEpisodeIntegrator( //
        Se2StateSpaceModel.INSTANCE, //
        Se2CarIntegrator.INSTANCE, //
        new StateTime(state, RealScalar.ZERO));
    this.maxSpeed = maxSpeed;
  }

  @Override
  public void control() {
    // ---
  }

  @Override
  public void integrate(Scalar now) {
    pushIntegrator.move(u, now);
    se2Integrator.move(pushIntegrator.tail().state(), now);
  }

  @Override
  public void resetControl() {
    u = U_NULL;
  }

  /** @param angle
   * @param speed */
  public void addControl(Scalar angle, Scalar speed) {
    u = u.add(Tensors.of(speed.multiply(maxSpeed), RealScalar.ZERO, angle)); // angle and speed
  }

  /** @return delta, speed */
  public Tensor getU() {
    return pushIntegrator.tail().state();
  }

  public StateTime getStateTime() {
    return se2Integrator.tail();
  }

  @Override
  public Tensor getSE3() {
    Tensor state = getStateTime().state();
    Tensor rotation = Rodriguez.exp( //
        Tensors.of(RealScalar.ZERO, RealScalar.ZERO, state.Get(2)));
    return MatrixFunctions.getSE3( //
        rotation, //
        Tensors.of(state.Get(0), state.Get(1), RealScalar.ZERO));
  }
}
