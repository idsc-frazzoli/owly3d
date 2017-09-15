// code by jph
package ch.ethz.idsc.owly3d.demo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/** draws a single triangle using GL11.glDrawArrays */
public class LidarPointCloud {
  private final int length_max;
  private final FloatBuffer floatBuffer;
  private final IntBuffer intBuffer;
  public float[] color = new float[] { 0.8f, 0.8f, 0.9f, 0.7f };
  public double[] translate = new double[3];
  int size = 0;

  /** @param length_max for velodyne 2304 * 32 is sufficient */
  public LidarPointCloud(int length_max) {
    this.length_max = length_max;
    floatBuffer = BufferUtils.createFloatBuffer(length_max * 3); // 3 for {x, y, z}
    intBuffer = BufferUtils.createIntBuffer(length_max);
    // ---
    for (int index = 0; index < length_max; ++index)
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
    floatBuffer.limit(3 * length_max);
    floatBuffer.position(0);
    floatBuffer.put(position_data);
    floatBuffer.flip();
    // floatBuffer.remaining(); // TODO use to update point count
    // ---
    intBuffer.position(0); // TODO probably unnecessary
    intBuffer.limit(byteBuffer.limit());
    size = byteBuffer.limit();
  }

  public synchronized void draw() {
    GL11.glPushMatrix();
    GL11.glTranslated(translate[0], translate[1], translate[2]);
    GL11.glPointSize(2);
    GL11.glColor4fv(color); // TODO deprecated
    GL11.glInterleavedArrays(GL11.GL_V3F, 0, floatBuffer);
    GL11.glDrawElements(GL11.GL_POINTS, intBuffer);
    GL11.glPopMatrix();
  }

  public int size() {
    return size;
  }
}
