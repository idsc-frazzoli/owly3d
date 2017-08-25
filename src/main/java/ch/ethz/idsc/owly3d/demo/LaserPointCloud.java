// code by jph
package ch.ethz.idsc.owly3d.demo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/** draws a single triangle using GL11.glDrawArrays */
public class LaserPointCloud {
  public static final int LENGTH = 2304 * 32;
  // ---
  private final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3 * LENGTH);
  private final IntBuffer intBuffer = BufferUtils.createIntBuffer(LENGTH);

  public LaserPointCloud() {
    for (int index = 0; index < LENGTH; ++index)
      intBuffer.put(index);
    intBuffer.flip();
    // ---
    // start with empty buffer
    floatBuffer.position(0);
    floatBuffer.limit(0);
    // ---
    intBuffer.limit(0);
  }

  public synchronized void fill(FloatBuffer position_data, ByteBuffer byteBuffer) {
    floatBuffer.limit(3 * LENGTH);
    floatBuffer.position(0);
    floatBuffer.put(position_data);
    floatBuffer.flip();
    // ---
    intBuffer.position(0); // TODO probably unnecessary
    intBuffer.limit(byteBuffer.limit());
  }

  public synchronized void draw() {
    GL11.glPushMatrix();
    GL11.glTranslated(0, 0, 3);
    GL11.glPointSize(2);
    GL11.glColor4d(.8, .8, .9, .7);
    GL11.glInterleavedArrays(GL11.GL_V3F, 0, floatBuffer);
    GL11.glDrawElements(GL11.GL_POINTS, intBuffer);
    GL11.glPopMatrix();
  }
}
