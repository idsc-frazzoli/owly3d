// code by jph
package ch.ethz.idsc.owly3d.util.gfx;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer {
  public static void render(RawModel model) {
    GL30.glBindVertexArray(model.vaoID);
    GL20.glEnableVertexAttribArray(0); // TODO
    // GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.vertexCount);
    GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
  }
}
