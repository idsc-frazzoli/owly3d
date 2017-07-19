// code by jph
package ch.ethz.idsc.owly3d.demo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.retina.dev.velodyne.HDL32EFiringCollector;

/** draws a single triangle using GL11.glDrawArrays */
public class LaserPointCloud {
  public static final int LENGTH = HDL32EFiringCollector.POINT_NUMEL;
  // ---
  private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3 * LENGTH);
  private final IntBuffer intBuffer = BufferUtils.createIntBuffer(LENGTH);

  public LaserPointCloud() {
    for (int index = 0; index < LENGTH; ++index)
      intBuffer.put(index);
    intBuffer.flip();
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

  public synchronized void fill(float[] position_data, int length) {
    for (int index = 0; index < length; ++index)
      floatBuffer.put(position_data[index]);
    floatBuffer.flip();
  }
}
