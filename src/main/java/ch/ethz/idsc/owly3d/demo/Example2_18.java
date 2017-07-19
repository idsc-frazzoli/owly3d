package ch.ethz.idsc.owly3d.demo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class Example2_18 {
  private final FloatBuffer fb = BufferUtils.createFloatBuffer(3 * 4);
  private final IntBuffer intBuffer = BufferUtils.createIntBuffer(4);
  private int vertexbuffer;
  private int indexbuffer;

  public Example2_18() {
    float[] array = { //
        -1 + 0.0f, 0.0f, 0.0f, //
        -1 + 1.0f, 0.0f, 0.2f, //
        -1 + 1.0f, 1.0f, 0.5f, //
        -1 + 0.0f, 1.0f, 0.5f, //
        // -1.0f, -1.0f, -2.0f, //
        // 0.0f, 1.0f, -2.0f, //
        // 1.0f, -1.0f, -2.0f, //
    };
    fb.put(array);
    fb.flip();
    // ---
    int[] indices = new int[] { //
        0, 1, 2, 3,//
    };
    intBuffer.put(indices);
    intBuffer.flip();
  }

  public void init() {
    vertexbuffer = GL15.glGenBuffers();
    indexbuffer = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexbuffer);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    // // ---
    // GL11.glEnableClientState(GL_VERTEX_ARRAY);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexbuffer);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
  }

  public void draw() {
    GL11.glColor3f(1, 1, 1);
    GL11.glInterleavedArrays(GL11.GL_V3F, 0, fb);
    GL11.glDrawElements(GL11.GL_QUADS, intBuffer);
    // GL11.glVerP
    // GL11.glDrawElements(GL11.GL_QUADS, 4, GL11.GL_INT, 0);
  }
}
