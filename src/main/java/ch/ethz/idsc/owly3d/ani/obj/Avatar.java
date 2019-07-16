// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owl.bot.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owl.bot.rice.Rice2StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.owly3d.ani.SE3Interface;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;

public class Avatar implements Animated, SE3Interface {
  private static final Tensor U_POS = Array.zeros(3).unmodifiable();
  private static final Tensor U_ROT = Array.zeros(3).unmodifiable();
  // ---
  private final Scalar maxVel = RealScalar.of(5);
  private final EpisodeIntegrator posVelIntegrator;
  private final EpisodeIntegrator rateIntegrator;
  private Tensor rotation = IdentityMatrix.of(3);
  private Tensor u_pos = U_POS;
  private Tensor u_rot = U_ROT;

  public Avatar(Tensor pos) {
    posVelIntegrator = new SimpleEpisodeIntegrator( //
        Rice2StateSpaceModel.of(RealScalar.of(1)), //
        EulerIntegrator.INSTANCE, //
        new StateTime(Join.of(pos, Array.zeros(3)), RealScalar.ZERO));
    rateIntegrator = new SimpleEpisodeIntegrator( //
        Rice1StateSpaceModel.of(RealScalar.of(2)), //
        EulerIntegrator.INSTANCE, //
        new StateTime(Array.zeros(3), RealScalar.ZERO));
  }

  @Override
  public void control() {
    // ---
  }

  @Override
  public void integrate(Scalar now) {
    {
      Tensor campush = Tensors.of(u_pos.Get(1).negate(), u_pos.Get(2), u_pos.Get(0).negate());
      posVelIntegrator.move(rotation.dot(campush), now);
    }
    // ---
    {
      rateIntegrator.move(u_rot, now);
      StateTime rate = rateIntegrator.tail();
      // ---
      Tensor drag = rate.state();
      rotation = rotation.dot(Rodrigues.exp(drag));
      boolean status = OrthogonalMatrixQ.of(rotation);
      if (!status)
        System.out.println("not orthogonal");
    }
  }

  @Override
  public void resetControl() {
    u_pos = U_POS;
    u_rot = U_ROT;
  }

  public void addPush(Tensor push) {
    u_pos = u_pos.add(push.multiply(maxVel));
  }

  public void addRotate(Tensor u) {
    u_rot = u_rot.add(u);
  }

  /** @return inverse of model view matrix */
  @Override
  public Tensor getSE3() {
    return MatrixFunctions.getSE3(rotation, posVelIntegrator.tail().state());
  }
}
