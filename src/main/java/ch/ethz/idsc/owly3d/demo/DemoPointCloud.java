package ch.ethz.idsc.owly3d.demo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/** draws a single triangle using GL11.glDrawArrays */
public class DemoPointCloud {
  public static final int LENGTH = 200;
  // ---
  Random random = new Random();
  private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3 * LENGTH);
  private final IntBuffer intBuffer = BufferUtils.createIntBuffer(LENGTH);

  public DemoPointCloud() {
    for (int c = 0; c < LENGTH; ++c)
      intBuffer.put(c);
    intBuffer.flip();
  }

  public void draw() {
    GL11.glPointSize(3);
    for (int c = 0; c < LENGTH * 3; ++c)
      floatBuffer.put(random.nextFloat());
    floatBuffer.flip();
    GL11.glColor4d(.8, .8, .9, .7);
    GL11.glInterleavedArrays(GL11.GL_V3F, 0, floatBuffer);
    GL11.glDrawElements(GL11.GL_POINTS, intBuffer);
  }
}
