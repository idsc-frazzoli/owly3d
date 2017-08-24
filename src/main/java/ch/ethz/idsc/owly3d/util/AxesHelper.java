// code by jph
package ch.ethz.idsc.owly3d.util;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Mod;

public enum AxesHelper {
  ;
  // ---
  public static void draw() {
    GL11.glLineWidth(1);
    GL11.glBegin(GL11.GL_LINES);
    GL11.glColor3d(.2, .5, .1);
    int num = 5;
    for (int ind = -num; ind <= num; ++ind) {
      GL11.glVertex3i(-num, ind, 0);
      GL11.glVertex3i(+num, ind, 0);
      GL11.glVertex3i(ind, -num, 0);
      GL11.glVertex3i(ind, +num, 0);
    }
    GL11.glEnd();
    // ---
    GL11.glLineWidth(2);
    GL11.glBegin(GL11.GL_LINES);
    GL11.glColor3f(.8f, 0, 0);
    GL11.glVertex3d(-10, 0, 0);
    GL11.glVertex3d(+10, 0, 0);
    GL11.glColor3f(0, .8f, 0);
    GL11.glVertex3d(0, -10, 0);
    GL11.glVertex3d(0, +10, 0);
    GL11.glColor3f(0, 0, .8f);
    GL11.glVertex3d(0, 0, -10);
    GL11.glVertex3d(0, 0, +10);
    GL11.glEnd();
  }

  public static void checker(Tensor pos) {
    GL11.glBegin(GL11.GL_QUADS);
    Tensor ras = Floor.of(pos.multiply(RealScalar.of(1 / 2.))).multiply(RealScalar.of(2));
    int cx = Scalars.intValueExact(ras.Get(0));
    int cy = Scalars.intValueExact(ras.Get(1));
    // System.out.println(cx + " " + cy);
    Mod mod = Mod.function(RealScalar.of(2));
    int blub = 30;
    for (int x = cx - blub; x <= cx + blub; x += 2) {
      for (int y = cy - blub; y <= cy + blub; y += 2) {
        Scalar ex = mod.apply(RealScalar.of(x / 2));
        Scalar ey = mod.apply(RealScalar.of(y / 2));
        if ((ex.equals(RealScalar.ZERO) && ey.equals(RealScalar.ONE)) || //
            (ex.equals(RealScalar.ONE) && ey.equals(RealScalar.ZERO))) {
          double rr = SimplexContinuousNoise.FUNCTION.at(x * .2 + 10, y * .2 - 10);
          double rb = SimplexContinuousNoise.FUNCTION.at(x * .2, y * .2);
          GL11.glColor4d(.2 * (rr + 1), .5, .2 * (1 + rb), .2);
          GL11.glVertex3f(x, y, -.1f);
          GL11.glVertex3f(x + 2, y, -.1f);
          GL11.glVertex3f(x + 2, y + 2, -.1f);
          GL11.glVertex3f(x, y + 2, -.1f);
        }
      }
    }
    GL11.glEnd();
  }
}
