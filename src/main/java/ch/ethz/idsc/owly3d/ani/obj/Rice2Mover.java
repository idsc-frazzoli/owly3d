// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owly.demo.rice.Rice2StateSpaceModel;
import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public class Rice2Mover implements Animated {
  private static final Tensor U_NULL = Array.zeros(2).unmodifiable();
  // ---
  private final EpisodeIntegrator episodeIntegrator;
  private Tensor u = U_NULL;

  /** @param state {px, py, vx, vy} */
  public Rice2Mover(Tensor state) {
    episodeIntegrator = new SimpleEpisodeIntegrator( //
        Rice2StateSpaceModel.of(RealScalar.of(0.8)), EulerIntegrator.INSTANCE, new StateTime(state, RealScalar.ZERO));
  }

  @Override
  public void control() {
    // ---
  }

  @Override
  public void integrate(Scalar now) {
    episodeIntegrator.move(u, now);
  }

  @Override
  public void resetControl() {
    u = U_NULL;
  }

  public void addPush(Tensor push) {
    u = u.add(push.extract(0, 2));
  }

  public StateTime getStateTime() {
    return episodeIntegrator.tail();
  }
}
