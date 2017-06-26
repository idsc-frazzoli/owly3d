// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly.demo.car.CarControl;
import ch.ethz.idsc.owly.demo.car.CarState;
import ch.ethz.idsc.owly3d.ani.Drawable;
import ch.ethz.idsc.owly3d.util.PlanarHelper;
import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.owly3d.util.draw.Cylinder;
import ch.ethz.idsc.owly3d.util.math.MatrixFunctions;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Rodriguez;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class EjCarDrawable extends EjCar implements Drawable {
  private final double DENT = 0.85;
  private int count = 0;

  @Override
  public void draw() {
    CarState carState = getCarState();
    CarControl carControl = getCarControl();
    if (++count % 10 == 0) {
      // System.out.println("VEL=" + carState.groundSpeed());
      // System.out.println(carControl.asVector());
    }
    GL11.glPushMatrix();
    {
      GL11.glMultMatrixd(Primitives2.matrix44(getSE3())); // JPH
      { // FOOT PRINT
        GL11.glBegin(GL11.GL_QUADS);
        { // draw car foot print
          GL11.glColor4f(.7f, .7f, .5f, .2f);
          double xf = +carModel.frontL().number().doubleValue(); // TODO replace by functions
          double xb = -carModel.rearL().number().doubleValue();
          double yl = +carModel.width().number().doubleValue() / 2;
          double yr = -yl;
          double h = -.02;
          GL11.glVertex3d(xf, yl, h);
          GL11.glVertex3d(xb, yl, h);
          GL11.glVertex3d(xb, yr, h);
          GL11.glVertex3d(xf, yr, h);
        }
        // { // draw axle front and rear
        // GL11.glColor4f(.7f, .7f, .5f, .4f);
        // double xf = +carModel.lF().number().doubleValue();
        // double xb = -carModel.lR().number().doubleValue();
        // double yl = +carModel.lw().number().doubleValue();
        // double yr = -yl;
        // double height = .2;
        // GL11.glVertex3d(xf, yl * DENT, height);
        // GL11.glVertex3d(xb, yl, height);
        // GL11.glVertex3d(xb, yr, height);
        // GL11.glVertex3d(xf, yr * DENT, height);
        // }
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
        GL11.glTranslatef(-carModel.lR().number().floatValue() / 2, 0, 0);
        Cylinder.drawZ(0, rate, .2, 20, 5, Tensors.vector(.2, .2, .8, 1));
        GL11.glPopMatrix();
      }
      { // HANDBRAKE
        double rate = carControl.handbrake.number().doubleValue() * .0025;
        GL11.glPushMatrix();
        GL11.glTranslatef(-carModel.lR().number().floatValue(), 0, 0);
        Cylinder.drawZ(0, rate, .2, 20, 5, Tensors.vector(.8, .2, .2, 1));
        GL11.glPopMatrix();
      }
      { // THROTTLE
        double rate = carControl.throttle.number().doubleValue() * .001;
        GL11.glPushMatrix();
        GL11.glTranslatef(carModel.lF().number().floatValue() / 2, 0, 0);
        Cylinder.drawZ(0, rate, .2, 20, 5, Tensors.vector(.8, .8, .2, 1));
        GL11.glPopMatrix();
      }
      { // TIRES
        final Tensor angles = getTireAngle();
        final double xf = +carModel.lF().number().doubleValue();
        final double xb = -carModel.lR().number().doubleValue();
        final double yl = +carModel.lw().number().doubleValue();
        GL11.glLineWidth(2);
        { // front left
          GL11.glPushMatrix();
          {
            Tensor mat1L = MatrixFunctions.getSE3( //
                Rodriguez.of(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, carControl.delta)), //
                Tensors.vector(xf, yl * DENT, 0));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4d(1, 0, 0, .7);
            GL11.glVertex2d(0, 0);
            GL11.glVertex2d(carModel.radiusTimes(carState.w1L).number().doubleValue(), 0);
            GL11.glEnd();
          }
          {
            double radius = carModel.radius().number().doubleValue();
            Tensor mat1L = MatrixFunctions.getSE3( //
                Rodriguez.of(Tensors.of(RealScalar.ZERO, angles.Get(0), RealScalar.ZERO)), //
                Tensors.vector(0, 0, radius));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            Cylinder.drawY(-.1, .1, radius, 30, 15, Tensors.vector(0, 1, 0, .8));
          }
          GL11.glPopMatrix();
        }
        // ---
        { // front left
          GL11.glPushMatrix();
          {
            Tensor mat1L = MatrixFunctions.getSE3( //
                Rodriguez.of(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, carControl.delta)), //
                Tensors.vector(xf, -yl * DENT, 0));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4d(1, 0, 0, .7);
            GL11.glVertex2d(0, 0);
            GL11.glVertex2d(carModel.radiusTimes(carState.w1R).number().doubleValue(), 0);
            GL11.glEnd();
          }
          {
            double radius = carModel.radius().number().doubleValue();
            Tensor mat1L = MatrixFunctions.getSE3( //
                Rodriguez.of(Tensors.of(RealScalar.ZERO, angles.Get(1), RealScalar.ZERO)), //
                Tensors.vector(0, 0, radius));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            Cylinder.drawY(-.1, .1, radius, 30, 15, Tensors.vector(0, 1, 0, .8));
          }
          GL11.glPopMatrix();
        }
        // ---
        { // rear left
          GL11.glPushMatrix();
          {
            Tensor mat1L = MatrixFunctions.getSE3( //
                IdentityMatrix.of(3),
                // Rodriguez.of(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, carControl.delta)), //
                Tensors.vector(xb, yl, 0));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4d(1, 0, 0, .7);
            GL11.glVertex2d(0, 0);
            GL11.glVertex2d(carModel.radiusTimes(carState.w2L).number().doubleValue(), 0);
            GL11.glEnd();
          }
          {
            double radius = carModel.radius().number().doubleValue();
            Tensor mat1L = MatrixFunctions.getSE3( //
                Rodriguez.of(Tensors.of(RealScalar.ZERO, angles.Get(2), RealScalar.ZERO)), //
                Tensors.vector(0, 0, radius));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            Cylinder.drawY(-.1, .1, radius, 30, 15, Tensors.vector(0, 1, 0, .8));
          }
          GL11.glPopMatrix();
        }
        // ---
        { // rear right
          GL11.glPushMatrix();
          {
            Tensor mat1L = MatrixFunctions.getSE3( //
                IdentityMatrix.of(3),
                // Rodriguez.of(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, carControl.delta)), //
                Tensors.vector(xb, -yl, 0));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor4d(1, 0, 0, .7);
            GL11.glVertex2d(0, 0);
            GL11.glVertex2d(carModel.radiusTimes(carState.w2R).number().doubleValue(), 0);
            GL11.glEnd();
          }
          {
            double radius = carModel.radius().number().doubleValue();
            Tensor mat1L = MatrixFunctions.getSE3( //
                Rodriguez.of(Tensors.of(RealScalar.ZERO, angles.Get(3), RealScalar.ZERO)), //
                Tensors.vector(0, 0, radius));
            GL11.glMultMatrixd(Primitives2.matrix44(mat1L));
            Cylinder.drawY(-.1, .1, radius, 30, 15, Tensors.vector(0, 1, 0, .8));
          }
          GL11.glPopMatrix();
        }
      }
    }
    GL11.glPopMatrix();
  }
}
