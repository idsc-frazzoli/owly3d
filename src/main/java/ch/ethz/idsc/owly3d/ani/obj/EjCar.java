// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owly.math.flow.EulerIntegrator;
import ch.ethz.idsc.owly.math.flow.Integrator;
import ch.ethz.idsc.owly.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owly.math.state.BoundedEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.owly.model.car.CarControl;
import ch.ethz.idsc.owly.model.car.CarState;
import ch.ethz.idsc.owly.model.car.CarStateSpaceModel;
import ch.ethz.idsc.owly.model.car.CarStatic;
import ch.ethz.idsc.owly.model.car.HomogenousTrack;
import ch.ethz.idsc.owly.model.car.VehicleModel;
import ch.ethz.idsc.owly.model.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.owly3d.ani.SE3Interface;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.Rodriguez;

public class EjCar implements Animated, SE3Interface {
  private static final Tensor U_NULL = Array.zeros(5).unmodifiable();
  private static final Tensor TIRE_N = Array.zeros(4).unmodifiable();
  private static final Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
  private static final Scalar MAX_TIME_STEP = RealScalar.of(.005);
  // ---
  private final EpisodeIntegrator pushIntegrator = new BoundedEpisodeIntegrator( //
      new Rice1StateSpaceModel(RealScalar.of(15.5)), //
      EulerIntegrator.INSTANCE, //
      new StateTime(U_NULL, RealScalar.ZERO), //
      MAX_TIME_STEP);
  // absolute wheel angles, only used for display -> accuracy has low priority
  private final EpisodeIntegrator tireIntegrator = new SimpleEpisodeIntegrator( //
      SingleIntegratorStateSpaceModel.INSTANCE, //
      EulerIntegrator.INSTANCE, //
      new StateTime(TIRE_N, RealScalar.ZERO));
  final VehicleModel vehicleModel = //
      RimoSinusIonModel.standard();
  // new CHatchbackModel(CarSteering.BOTH, RealScalar.of(.5));
  private EpisodeIntegrator carIntegrator;
  private Tensor u = U_NULL;

  public EjCar() {
    CarState carState = CarStatic.x0_demo1(); // magic const
    // TODO redundant to reset() mod some const -> refactor
    carIntegrator = new BoundedEpisodeIntegrator( //
        new CarStateSpaceModel(vehicleModel, HomogenousTrack.DRY_ROAD), //
        INTEGRATOR, //
        new StateTime(carState.asVector(), RealScalar.ZERO), //
        MAX_TIME_STEP);
  }

  public void reset() {
    System.out.println("reset");
    Scalar now = carIntegrator.tail().time();
    CarState carState = CarStatic.x0_demo1();
    carIntegrator = new BoundedEpisodeIntegrator( //
        new CarStateSpaceModel(vehicleModel, HomogenousTrack.DRY_ROAD), //
        INTEGRATOR, //
        new StateTime(carState.asVector(), now), //
        MAX_TIME_STEP);
  }

  @Override
  public void control() {
    // ---
  }

  @Override
  public void integrate(Scalar now) {
    pushIntegrator.move(u, now);
    int tires = vehicleModel.wheels();
    Tensor omega = carIntegrator.tail().state().extract(6, 6 + tires); // TODO magic const
    carIntegrator.move(pushIntegrator.tail().state(), now);
    tireIntegrator.move(omega, now);
  }

  @Override
  public void resetControl() {
    u = U_NULL;
  }

  public void addControl(Scalar delta, Scalar brake, Scalar handbrake, Scalar throttleL, Scalar throttleR) {
    Tensor uv = Tensors.of( //
        delta, //
        brake.multiply(RealScalar.of(.5)), //
        handbrake.multiply(RealScalar.of(.5)), //
        throttleL, throttleR);
    u = u.add(uv);
  }

  public CarControl getCarControl() {
    return vehicleModel.createControl(pushIntegrator.tail().state());
  }

  public CarState getCarState() {
    return new CarState(carIntegrator.tail().state());
  }

  public Tensor getTireAngle() {
    return tireIntegrator.tail().state();
  }

  @Override
  public Tensor getSE3() {
    CarState carState = getCarState();
    Tensor rotation = Rodriguez.of( //
        Tensors.of(RealScalar.ZERO, RealScalar.ZERO, carState.Ksi));
    return MatrixFunctions.getSE3( //
        rotation, Tensors.of(carState.px, carState.py, RealScalar.ZERO));
  }
}
