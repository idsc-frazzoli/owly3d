// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import ch.ethz.idsc.owly.demo.car.CarControl;
import ch.ethz.idsc.owly.demo.car.CarState;
import ch.ethz.idsc.owly.demo.car.CarStateSpaceModel;
import ch.ethz.idsc.owly.demo.car.CarStatic;
import ch.ethz.idsc.owly.demo.car.CarSteering;
import ch.ethz.idsc.owly.demo.car.HomogenousTrack;
import ch.ethz.idsc.owly.demo.car.box.CHatchbackModel;
import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.math.SingleIntegrator;
import ch.ethz.idsc.owly.math.flow.EulerIntegrator;
import ch.ethz.idsc.owly.math.flow.Integrator;
import ch.ethz.idsc.owly.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owly.math.state.BoundedEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
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
  private static final Tensor U_NULL = Array.zeros(4).unmodifiable();
  private static final Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
  private static final Scalar MAX_TIME_STEP = RealScalar.of(.005);
  // ---
  private final EpisodeIntegrator pushIntegrator = new BoundedEpisodeIntegrator( //
      new Rice1StateSpaceModel(RealScalar.of(1.0)), //
      EulerIntegrator.INSTANCE, //
      new StateTime(U_NULL, RealScalar.ZERO), //
      MAX_TIME_STEP);
  private final EpisodeIntegrator tireIntegrator = new SimpleEpisodeIntegrator( //
      SingleIntegrator.INSTANCE, //
      EulerIntegrator.INSTANCE, //
      new StateTime(U_NULL, RealScalar.ZERO));
  final CHatchbackModel carModel = new CHatchbackModel(CarSteering.BOTH, RealScalar.of(.5));
  private EpisodeIntegrator carIntegrator;
  private Tensor u = U_NULL;

  public EjCar() {
    CarState carState = CarStatic.x0_demo1();
    carIntegrator = new BoundedEpisodeIntegrator( //
        new CarStateSpaceModel(carModel, HomogenousTrack.DRY_ROAD), //
        INTEGRATOR, //
        new StateTime(carState.asVector(), RealScalar.ZERO), //
        MAX_TIME_STEP);
  }

  public void reset() {
    System.out.println("reset");
    Scalar now = carIntegrator.tail().time();
    CarState carState = CarStatic.x0_demo1();
    carIntegrator = new BoundedEpisodeIntegrator( //
        new CarStateSpaceModel(carModel, HomogenousTrack.DRY_ROAD), //
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
    Tensor omega = carIntegrator.tail().x().extract(6, 10);
    carIntegrator.move(pushIntegrator.tail().x(), now);
    tireIntegrator.move(omega, now);
  }

  @Override
  public void resetControl() {
    u = U_NULL;
  }

  public void addControl(Scalar delta, Scalar brake, Scalar handbrake, Scalar throttle) {
    CarControl cc = carModel.createControl( //
        Tensors.of( //
            delta, //
            brake.multiply(RealScalar.of(.5)), //
            handbrake.multiply(RealScalar.of(.5)), //
            throttle));
    u = u.add(cc.asVector());
  }

  public CarControl getCarControl() {
    return new CarControl(pushIntegrator.tail().x());
  }

  public CarState getCarState() {
    return new CarState(carIntegrator.tail().x());
  }

  public Tensor getTireAngle() {
    return tireIntegrator.tail().x();
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
