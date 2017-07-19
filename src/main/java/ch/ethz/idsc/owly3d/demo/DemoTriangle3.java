package ch.ethz.idsc.owly3d.demo;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

/** draws a single triangle using GL11.glDrawArrays */
public class DemoTriangle3 {
  private FloatBuffer fb = BufferUtils.createFloatBuffer(3 * 3 * 1);
  private int vertexbuffer;

  public DemoTriangle3() {
    float[] array = { //
        1.0f, 0.0f, 0.2f, //
        0.0f, 1.0f, 2.0f, //
        0.0f, 0.0f, 1.0f, //
    };
    fb.put(array);
    fb.flip();
  }

  public void init() {
    vertexbuffer = GL15.glGenBuffers();
    System.out.println("demotriangle " + vertexbuffer);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexbuffer);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
  }

  public void draw2() {
    glUseProgram(0);
    GL11.glColor3f(1, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexbuffer);
    GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
  }

  public void draw() {
    GL20.glEnableVertexAttribArray(0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexbuffer);
    // int index, int size, int type, boolean normalized, int stride, ByteBuffer pointer
    byte[] bytes = { 0, 1, 2 };
    ByteBuffer ind = ByteBuffer.wrap(bytes);
    GL20.glVertexAttribPointer( //
        0, //
        3, //
        GL11.GL_FLOAT, //
        false, //
        0, //
        ind);
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
    GL20.glDisableVertexAttribArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }
}
