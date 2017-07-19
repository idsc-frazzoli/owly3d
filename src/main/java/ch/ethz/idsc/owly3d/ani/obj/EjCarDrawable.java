// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly.demo.car.CarControl;
import ch.ethz.idsc.owly.demo.car.CarState;
import ch.ethz.idsc.owly.demo.car.TireForces;
import ch.ethz.idsc.owly3d.ani.Drawable;
import ch.ethz.idsc.owly3d.util.PlanarHelper;
import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.owly3d.util.draw.Cylinder;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.lie.Rodriguez;

public class EjCarDrawable extends EjCar implements Drawable {
  // TODO draw more info about the car dynamics: energy, momentum etc.
  @Override
  public void draw() {
    CarState carState = getCarState();
    CarControl carControl = getCarControl();
    Scalar mu = RealScalar.of(0.8); // friction coefficient on dry road
    TireForces tireForces = new TireForces(vehicleModel, carState, carControl, mu);
    GL11.glPushMatrix();
    {
      GL11.glMultMatrixd(Primitives2.matrix44(getSE3())); // JPH
      { // FOOT PRINT
        GL11.glBegin(GL11.GL_POLYGON);
        { // draw car foot print
          GL11.glColor4f(.7f, .7f, .5f, .2f);
          double h = -.02;
          for (Tensor vertex : vehicleModel.footprint())
            GL11.glVertex3d( //
                vertex.Get(0).number().doubleValue(), //
                vertex.Get(1).number().doubleValue(), //
                h);
        }
        GL11.glEnd();
        // ---
        GL11.glBegin(GL11.GL_QUADS);
        { // draw cogUnitV
          GL11.glColor4f(1, 1, 1, .4f);
          double xf = +.1;
          double xb = -.1;
          double yl = +.1;
          double yr = -.1;
          GL11.glVertex2d(xf, yl);
          GL11.glVertex2d(xb, yl);
          GL11.glVertex2d(xb, yr);
          GL11.glVertex2d(xf, yr);
        }
        GL11.glEnd();
      }
      { // GROUND SPEED
        GL11.glColor4f(0, 1, 1, .5f);
        PlanarHelper.drawArrow2D(Tensors.of(carState.Ux, carState.Uy));
      }
      { // ANGULAR RATE
        double rate = carState.r.number().doubleValue();
        Cylinder.drawZ(Math.min(rate, 0), Math.max(0, rate), .1, 20, 5, Tensors.vector(.6, 0, .6, 1));
      }
      { // BRAKE
        double rate = carControl.brake.number().doubleValue() * .5;
        GL11.glPushMatrix();
        GL11.glTranslatef(-1 / 2, 0, 0);
        Cylinder.drawZ(0, rate, .2, 20, 5, Tensors.vector(.2, .2, .8, 1));
        GL11.glPopMatrix();
      }
      { // HANDBRAKE
        double rate = carControl.handbrake.number().doubleValue() * .0025;
        GL11.glPushMatrix();
        GL11.glTranslatef(-1 * .7f, 0, 0);
        Cylinder.drawZ(0, rate, .2, 20, 5, Tensors.vector(.8, .2, .2, 1));
        GL11.glPopMatrix();
      }
      { // TIRES
        final Tensor angles = getTireAngle();
        GL11.glLineWidth(2);
        {
          // deltas
          Tensor deltas = vehicleModel.angles(carControl.delta);
          Tensor wrate = carState.asVector().extract(6, 10);
          for (int index = 0; index < vehicleModel.wheels(); ++index) { // front left
            GL11.glPushMatrix();
            {
              Tensor lever = vehicleModel.wheel(index).lever().copy();
              { // body frame at tire at COG-height level
                lever.set(scalar -> scalar.negate(), 2);
                Tensor mat = MatrixFunctions.getTranslation(lever);
                GL11.glMultMatrixd(Primitives2.matrix44(mat));
              }
              {
                Tensor force = tireForces.Forces.get(index).multiply(RealScalar.of(3e-4));
                GL11.glBegin(GL11.GL_LINES);
                GL11.glColor4d(.8, .4, 1, .7);
                GL11.glVertex2d(0, 0);
                GL11.glVertex3d( //
                    force.Get(0).number().doubleValue(), //
                    force.Get(1).number().doubleValue(), //
                    force.Get(2).number().doubleValue());
                GL11.glEnd();
              }
              { // draw lines of angular rate
                Tensor vec = Array.zeros(3);
                vec.set(vehicleModel.wheel(index).lever().Get(2), 2); //
                Tensor mat = MatrixFunctions.getSE3( //
                    Rodriguez.of(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, deltas.Get(index))), //
                    vec); // altitude == 0
                GL11.glMultMatrixd(Primitives2.matrix44(mat));
                GL11.glBegin(GL11.GL_LINES);
                GL11.glColor4d(1, 0, 0, .7);
                GL11.glVertex2d(0, 0);
                GL11.glVertex2d(vehicleModel.wheel(index).radius().multiply(wrate.Get(index)).number().doubleValue(), 0);
                GL11.glEnd();
              }
              { // draw wheel
                double w2 = vehicleModel.wheel(index).width().number().doubleValue() / 2;
                GL11.glPushMatrix();
                double radius = vehicleModel.wheel(index).radius().number().doubleValue();
                Tensor mat = MatrixFunctions.getSE3( //
                    Rodriguez.of(Tensors.of(RealScalar.ZERO, angles.Get(index), RealScalar.ZERO)), //
                    Tensors.vector(0, 0, radius));
                GL11.glMultMatrixd(Primitives2.matrix44(mat));
                Cylinder.drawY(-w2, w2, radius, 30, 15, Tensors.vector(0, 1, 0, .8));
                { // throttle, torque on tire
                  double rate = carControl.throttleV.Get(index).number().doubleValue() * .003;
                  Tensor vec = Tensors.vector(w2, w2 + rate);
                  boolean left = 0 < lever.Get(1).number().doubleValue();
                  Tensor res = Sort.of(left ? vec : vec.negate());
                  Cylinder.drawY( //
                      res.Get(0).number().doubleValue(), //
                      res.Get(1).number().doubleValue(), //
                      radius * .3, 30, 15, Tensors.vector(1, 1, 0, .8));
                }
                GL11.glPopMatrix();
              }
            }
            GL11.glPopMatrix();
          }
        }
      }
    }
    GL11.glPopMatrix();
  }
}
