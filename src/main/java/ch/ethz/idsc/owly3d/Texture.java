// code by jph
package ch.ethz.idsc.owly3d;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

public class Texture {
  public static int bind() {
    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
    int ret = GL11.glGenTextures();
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, ret);
    GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE); // opengl default
    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT); // this is default
    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT); // this is default
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 16 * 16 * 2);
    ByteBuffer pixels = Primitives2.toByteBuffer(tensor);
    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 16, 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
    // GL11.gluBuild2DMipmaps(); //GL11.GL_TEXTURE_2D,fmt,tex.size[1],tex.size[2],fmt,GL_UNSIGNED_BYTE,tex.data);
    // GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    // System.out.println("texture " + ret);
    // System.out.println("more tex=" + GL11.glGenTextures());
    return ret;
  }

  public static void draw() {
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    // GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
    GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE); // opengl default
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 1);
    GL11.glColor4f(1, 1, 1, 1);
    GL11.glBegin(GL11.GL_QUADS);
    GL11.glTexCoord2f(0, 0);
    GL11.glVertex2d(0, 0);
    GL11.glTexCoord2f(10, 0);
    GL11.glVertex2d(10, 0);
    GL11.glTexCoord2f(1, 1);
    GL11.glVertex2d(10, 10);
    GL11.glTexCoord2f(0, 1);
    GL11.glVertex2d(0, 10);
    GL11.glEnd();
    GL11.glDisable(GL11.GL_TEXTURE_2D);
  }
}
