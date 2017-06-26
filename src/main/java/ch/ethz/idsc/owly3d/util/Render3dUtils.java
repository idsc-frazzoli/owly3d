// code by jph
package ch.ethz.idsc.owly3d.util;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.tensor.Tensor;

public enum Render3dUtils {
  ;
  // ---
  public static void vertex(Tensor state) {
    switch (state.length()) {
    case 0:
      break;
    case 1:
      GL11.glVertex2f( //
          state.Get(0).number().floatValue(), 0);
      break;
    case 2:
      GL11.glVertex2f( //
          state.Get(0).number().floatValue(), //
          state.Get(1).number().floatValue());
      break;
    default:
      GL11.glVertex3f( //
          state.Get(0).number().floatValue(), //
          state.Get(1).number().floatValue(), //
          state.Get(2).number().floatValue());
      break;
    }
  }
}
