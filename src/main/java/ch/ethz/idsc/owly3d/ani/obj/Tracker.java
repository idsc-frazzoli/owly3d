// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owly.demo.rice.Rice2StateSpaceModel;
import ch.ethz.idsc.owly.math.flow.EulerIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.owly3d.ani.SE3Interface;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class Tracker implements Animated, SE3Interface {
  private static final Tensor U_POS = Array.zeros(3).unmodifiable();
  // ---
  // private final Scalar maxVel = RealScalar.of(2);
  private final EpisodeIntegrator posVelIntegrator;
  private Tensor u_pos = U_POS;

  public Tracker(Tensor posvel) {
    posVelIntegrator = new SimpleEpisodeIntegrator( //
        new Rice2StateSpaceModel(RealScalar.of(1)), //
        EulerIntegrator.INSTANCE, //
        new StateTime(posvel, RealScalar.ZERO));
  }

  @Override
  public void control() {
    // ---
  }

  @Override
  public void integrate(Scalar now) {
    // Tensor campush = Tensors.of(u_pos.Get(1).negate(), u_pos.Get(2), u_pos.Get(0).negate());
    posVelIntegrator.move(u_pos, now);
  }

  @Override
  public void resetControl() {
    u_pos = U_POS;
  }

  public void addPush(Tensor push) {
    u_pos = u_pos.add(push);
  }

  /** @return inverse of model view matrix */
  @Override
  public Tensor getSE3() {
    return MatrixFunctions.getSE3(IdentityMatrix.of(3), posVelIntegrator.tail().x());
  }

  public void setGoal(Tensor src) {
    Tensor pos = posVelIntegrator.tail().x().extract(0, 3);
    Tensor dir = src.extract(0, 3).subtract(pos);
    addPush(dir);
  }
}
