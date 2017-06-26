package ch.ethz.idsc.owly3d.util.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public enum Loader {
  ;
  private static List<Integer> vaos = new ArrayList<>();
  private static List<Integer> vbos = new ArrayList<>();

  public static RawModel loadToVAO(float[] positions, int[] indices) {
    int vaoID = createVAO();
    bindIndecesBuffer(indices);
    storeDataInAttributeList(0, positions);
    unbindVAO();
    // TODO /3
    return new RawModel(vaoID, indices.length);
  }

  public static void cleanUp() {
    for (int vao : vaos)
      GL30.glDeleteVertexArrays(vao);
    for (int vbo : vbos)
      GL15.glDeleteBuffers(vbo);
  }

  private static int createVAO() {
    int vaoID = GL30.glGenVertexArrays();
    vaos.add(vaoID);
    System.out.println("vaoID=" + vaoID);
    GL30.glBindVertexArray(vaoID);
    return vaoID;
  }

  private static void storeDataInAttributeList(int attribNum, float[] data) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    FloatBuffer buffer = storeDataInFloatBuffer(data);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attribNum, 3, GL11.GL_FLOAT, false, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
  }

  private static void unbindVAO() {
    GL30.glBindVertexArray(0);
  }

  private static void bindIndecesBuffer(int[] indices) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
    IntBuffer buffer = storeDataInIntBuffer(indices);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
  }

  private static IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

  private static FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
}
