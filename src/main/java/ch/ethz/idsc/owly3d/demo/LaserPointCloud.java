// code by jph
package ch.ethz.idsc.owly3d.demo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

//import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eAngularFiringCollector;
/** draws a single triangle using GL11.glDrawArrays */
public class LaserPointCloud {
  // FIXME
  public static final int LENGTH = 123;// Hdl32eAngularFiringCollector.MAX_COORDINATES;
  // ---
  private final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4 * LENGTH);
  private final IntBuffer intBuffer = BufferUtils.createIntBuffer(3 * LENGTH);

  public LaserPointCloud() {
    for (int index = 0; index < LENGTH; ++index)
      intBuffer.put(index);
    intBuffer.flip();
    System.out.println(floatBuffer.capacity());
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
    // System.out.println(LENGTH + " " + length);
    floatBuffer.position(0);
    int index = 0;
    try {
      for (; index < Math.min(length, 3 * LENGTH); ++index) {
        // System.out.println(index);
        floatBuffer.put(position_data[index]);
      }
    } catch (Exception exception) {
      // ---
      // FIXME doesn't always draw everything
      // System.out.println(index);
    }
    floatBuffer.flip();
  }
}
