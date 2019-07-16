// code by jph
package ch.ethz.idsc.owly3d.util;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;

public enum PlanarHelper {
  ;
  /** @param dir */
  public static void drawArrow2D(Tensor dir) {
    GL11.glBegin(GL11.GL_TRIANGLES);
    {
      Tensor orth = Cross.of(dir);
      Tensor r = orth.multiply(RealScalar.of(.025));
      GL11.glVertex2d(r.Get(0).number().doubleValue(), r.Get(1).number().doubleValue());
      r = r.negate();
      GL11.glVertex2d(r.Get(0).number().doubleValue(), r.Get(1).number().doubleValue());
      GL11.glVertex2d(dir.Get(0).number().doubleValue(), dir.Get(1).number().doubleValue());
    }
    GL11.glEnd();
  }
}
