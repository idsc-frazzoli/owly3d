package ch.ethz.idsc.owly3d;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class DemoTriangle {
  public final int vertexbuffer;

  public DemoTriangle() {
    float[] array = { //
        -1.0f, -1.0f, 0.0f, //
        0.0f, 1.0f, 0.0f, //
        1.0f, -1.0f, 0.0f, //
    };
    vertexbuffer = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexbuffer);
    FloatBuffer data = FloatBuffer.wrap(array);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
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
  }
}
