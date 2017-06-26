// code by jph
package ch.ethz.idsc.owly3d.ani.obj;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.owly3d.ani.Drawable;
import ch.ethz.idsc.tensor.Tensor;

public class Rice2MoverDrawable extends Rice2Mover implements Drawable {
  public Rice2MoverDrawable(Tensor state) {
    super(state);
  }

  @Override
  public void draw() {
    StateTime stateTime = getStateTime();
    GL11.glPushMatrix();
    GL11.glTranslated( //
        stateTime.x().Get(0).number().doubleValue(), //
        stateTime.x().Get(1).number().doubleValue(), //
        0);
    GL11.glBegin(GL11.GL_QUADS);
    GL11.glColor4f(.7f, .5f, .7f, .5f);
    double w = .2;
    GL11.glVertex3d(+w, +w, 0);
    GL11.glVertex3d(-w, +w, 0);
    GL11.glVertex3d(-w, -w, 0);
    GL11.glVertex3d(+w, -w, 0);
    GL11.glEnd();
    GL11.glPopMatrix();
  }
}
