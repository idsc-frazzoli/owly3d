// code by jph
package ch.ethz.idsc.owly3d.util;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public enum PlanarHelper {
  ;
  /** @param dir */
  public static void drawArrow2D(Tensor dir) {
    GL11.glBegin(GL11.GL_TRIANGLES);
    {
      Tensor orth = Cross2D.of(dir);
      Tensor r = orth.multiply(RealScalar.of(.025));
      GL11.glVertex2d(r.Get(0).number().doubleValue(), r.Get(1).number().doubleValue());
      r = r.negate();
      GL11.glVertex2d(r.Get(0).number().doubleValue(), r.Get(1).number().doubleValue());
      GL11.glVertex2d(dir.Get(0).number().doubleValue(), dir.Get(1).number().doubleValue());
    }
    GL11.glEnd();
  }
}
