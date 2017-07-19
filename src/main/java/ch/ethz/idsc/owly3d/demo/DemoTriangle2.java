package ch.ethz.idsc.owly3d.demo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/** draws a single triangle using GL11.glDrawElements */
public class DemoTriangle2 {
  private final FloatBuffer fb = BufferUtils.createFloatBuffer(3 * 3 * 1);
  private final IntBuffer intBuffer = BufferUtils.createIntBuffer(3);

  public DemoTriangle2() {
    float[] array = { //
        1 + 1.0f, 0.0f, 0.2f, //
        1 + 1.0f, 1.0f, 0.5f, //
        1 + 0.0f, 0.0f, 1.0f, //
        // -1.0f, -1.0f, -2.0f, //
        // 0.0f, 1.0f, -2.0f, //
        // 1.0f, -1.0f, -2.0f, //
    };
    fb.put(array);
    fb.flip();
    // ---
    int[] indices = new int[] { //
        0, 1, 2, //
        // 1, 2, 3, //
        // 4, 5, 6, //
        // 1, 3, 6
    };
    intBuffer.put(indices);
    intBuffer.flip();
  }

  public void draw() {
    GL11.glColor3f(0, 1, 1);
    GL11.glInterleavedArrays(GL11.GL_V3F, 0, fb);
    GL11.glDrawElements(GL11.GL_TRIANGLES, intBuffer);
  }
}
