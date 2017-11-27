// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owl.bot.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.state.BoundedEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owly.car.drift.DriftExtStateSpaceModel;
import ch.ethz.idsc.owly.car.drift.DriftParameters;
import ch.ethz.idsc.owly.car.drift.DriftStates;
import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.owly3d.ani.SE3Interface;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.Rodriguez;

public class DriftCar implements Animated, SE3Interface {
  private static final Tensor U_NULL = Array.zeros(2).unmodifiable();
  private static final Tensor TIRE_N = Array.zeros(2).unmodifiable();
  private static final Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
  private static final Scalar MAX_TIME_STEP = RealScalar.of(.005);
  // ---
  private final EpisodeIntegrator pushIntegrator = new BoundedEpisodeIntegrator( //
      Rice1StateSpaceModel.of(RealScalar.of(3.0)), //
      EulerIntegrator.INSTANCE, //
      new StateTime(U_NULL, RealScalar.ZERO), //
      MAX_TIME_STEP);
  // absolute wheel angles, only used for display -> accuracy has low priority
  @Deprecated
  private final EpisodeIntegrator tireIntegrator = new SimpleEpisodeIntegrator( //
      SingleIntegratorStateSpaceModel.INSTANCE, //
      EulerIntegrator.INSTANCE, //
      new StateTime(TIRE_N, RealScalar.ZERO));
  DriftParameters driftParameters = new DriftParameters();
  // final VehicleModel vehicleModel = //
  // RimoSinusIonModel.standard();
  // new CHatchbackModel(CarSteering.BOTH, RealScalar.of(.5));
  private EpisodeIntegrator carIntegrator;
  private Tensor u = U_NULL;

  public DriftCar() {
    // CarState carState = CarStatic.x0_demo1(); // magic const
    // TODO redundant to reset() mod some const -> refactor
    carIntegrator = new BoundedEpisodeIntegrator( //
        new DriftExtStateSpaceModel(driftParameters), //
        // new CarStateSpaceModel(vehicleModel, HomogenousTrack.DRY_ROAD), //
        INTEGRATOR, //
        new StateTime(DriftStates.x0_demo1(), RealScalar.ZERO), //
        MAX_TIME_STEP);
  }

  public void reset() {
    System.out.println("reset");
    Scalar now = carIntegrator.tail().time();
    carIntegrator = new BoundedEpisodeIntegrator( //
        // new CarStateSpaceModel(vehicleModel, HomogenousTrack.DRY_ROAD), //
        new DriftExtStateSpaceModel(driftParameters), //
        INTEGRATOR, //
        new StateTime(DriftStates.x0_demo1(), now), //
        MAX_TIME_STEP);
  }

  @Override
  public void control() {
    // ---
  }

  @Override
  public void integrate(Scalar now) {
    pushIntegrator.move(u, now);
    // int tires = 2;
    // Tensor omega = carIntegrator.tail().state().extract(6, 6 + tires);
    carIntegrator.move(pushIntegrator.tail().state(), now);
    // tireIntegrator.move(omega, now);
  }

  @Override
  public void resetControl() {
    u = U_NULL;
  }

  public void addControl(Scalar delta, Scalar throttleL) {
    Tensor uv = Tensors.of(delta, throttleL);
    u = u.add(uv);
  }

  public Tensor getCarControl() {
    return pushIntegrator.tail().state();
  }

  /** @return (x,y,theta,beta,r,Ux) */
  public Tensor getCarState() {
    return carIntegrator.tail().state();
  }

  // public Tensor getTireAngle() {
  // return tireIntegrator.tail().state();
  // }
  @Override
  public Tensor getSE3() {
    Tensor carState = getCarState();
    Tensor rotation = Rodriguez.exp( //
        Tensors.of(RealScalar.ZERO, RealScalar.ZERO, carState.Get(2)));
    return MatrixFunctions.getSE3( //
        rotation, Tensors.of(carState.Get(0), carState.Get(1), RealScalar.ZERO));
  }
}
