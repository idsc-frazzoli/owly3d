// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly3d.ani.Drawable;
import ch.ethz.idsc.owly3d.util.PlanarHelper;
import ch.ethz.idsc.owly3d.util.Primitives3d;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Se2CarDrawable extends Se2Car implements Drawable {
  private final Scalar x_front = RealScalar.of(.85);
  private final Scalar x_back = RealScalar.of(-.75);
  private final Scalar y_left = RealScalar.of(.45);
  private final Scalar y_right = RealScalar.of(-.45);

  public Se2CarDrawable(Tensor state, Scalar maxSpeed) {
    super(state, maxSpeed);
  }

  @Override
  public void draw() {
    GL11.glPushMatrix();
    {
      GL11.glMultMatrixd(Primitives3d.matrix44(getSE3()));
      { // --- draw car body
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(.2f, .7f, .5f, .8f);
        double xf = x_front.number().doubleValue();
        double xb = x_back.number().doubleValue();
        double yl = y_left.number().doubleValue();
        double yr = y_right.number().doubleValue();
        GL11.glVertex2d(xf, yl * .9);
        GL11.glVertex2d(xb, yl);
        GL11.glVertex2d(xb, yr);
        GL11.glVertex2d(xf, yr * .9);
        GL11.glEnd();
      }
      { // --- draw angle
        Tensor u = getU();
        Tensor dir = Tensors.of(u.Get(1), u.Get(0));
        GL11.glColor3f(1, 0, 0);
        PlanarHelper.drawArrow2D(dir);
      }
    }
    GL11.glPopMatrix();
  }
}
