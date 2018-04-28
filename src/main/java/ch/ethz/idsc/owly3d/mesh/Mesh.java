// code by jph
package ch.ethz.idsc.owly3d.mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;

public class Mesh {
  public Tensor vertices = Tensors.empty();
  public Tensor triangles = Tensors.empty();
  public Tensor quads = Tensors.empty();

  public void drawAlt() {
    GL11.glColor4f(.7f, 1, .7f, 1);
    Random random = new Random();
    float[] array = new float[3 * 20];
    for (int index = 0; index < array.length; ++index)
      array[index] = random.nextFloat();
    GL11.glInterleavedArrays(GL11.GL_V3F, 3, array);
    // int[] indices = ExtractPrimitives.toArrayInt(triangles);
    int[] indices = new int[] { //
        0, 1, 2, //
        1, 2, 3, //
        4, 5, 6, //
        1, 3, 6 };
    IntBuffer intBuffer = IntBuffer.wrap(indices);
    intBuffer.position(6);
    intBuffer.flip();
    GL11.glDrawElements(GL11.GL_TRIANGLES, intBuffer);
  }

  public void draw() {
    Random random = new Random();
    float[] array = toArrayFloat(vertices);
    {
      int[] indices = Primitives.toIntArray(triangles);
      IntBuffer intBuffer = IntBuffer.wrap(indices);
      intBuffer.position(indices.length);
      intBuffer.flip();
      while (intBuffer.hasRemaining()) {
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glColor4f(.8f, .8f, .8f, .8f);
        int ind1 = intBuffer.get() * 3;
        int ind2 = intBuffer.get() * 3;
        int ind3 = intBuffer.get() * 3;
        GL11.glVertex3f(array[ind1 + 0], array[ind1 + 1], array[ind1 + 2]);
        GL11.glVertex3f(array[ind2 + 0], array[ind2 + 1], array[ind2 + 2]);
        GL11.glVertex3f(array[ind3 + 0], array[ind3 + 1], array[ind3 + 2]);
        GL11.glEnd();
      }
    }
    {
      int[] indices = Primitives.toIntArray(quads);
      IntBuffer intBuffer = IntBuffer.wrap(indices);
      intBuffer.position(indices.length);
      intBuffer.flip();
      while (intBuffer.hasRemaining()) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(.8f, .8f, .8f, .8f);
        int ind1 = intBuffer.get() * 3;
        int ind2 = intBuffer.get() * 3;
        int ind3 = intBuffer.get() * 3;
        int ind4 = intBuffer.get() * 3;
        GL11.glVertex3f(array[ind1 + 0], array[ind1 + 1], array[ind1 + 2]);
        GL11.glVertex3f(array[ind2 + 0], array[ind2 + 1], array[ind2 + 2]);
        GL11.glVertex3f(array[ind3 + 0], array[ind3 + 1], array[ind3 + 2]);
        GL11.glVertex3f(array[ind4 + 0], array[ind4 + 1], array[ind4 + 2]);
        GL11.glEnd();
      }
    }
  }

  public void drawTest() {
    int amountOfVertices = 4;
    int vertexSize = 2;
    int colorSize = 3;
    // Create buffer with vertex data
    FloatBuffer vertexData = BufferUtils.createFloatBuffer(amountOfVertices * vertexSize);
    vertexData.put(new float[] { 0, 0, 0, -1, -1, -1, -1, 0 });
    vertexData.flip();
    // Create buffer with color data
    FloatBuffer colorData = BufferUtils.createFloatBuffer(amountOfVertices * colorSize);
    colorData.put(new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0 });
    colorData.flip();
    int vboVertexHandle = GL15.glGenBuffers();
    System.out.println(vboVertexHandle);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandle);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL11.glVertexPointer(vertexSize, GL11.GL_FLOAT, 0, 0L);
    int vboColorHandle = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColorHandle);
    GL11.glColorPointer(colorSize, GL11.GL_FLOAT, 0, 0L);
    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
    GL11.glDrawArrays(GL11.GL_QUADS, 0, amountOfVertices);
    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
  }

  /** @param tensor
   * @return array of double values of all scalars in tensor */
  private static float[] toArrayFloat(Tensor tensor) {
    List<Float> list = Primitives.toStreamNumber(tensor) //
        .map(Number::floatValue).collect(Collectors.toList());
    float[] array = new float[list.size()];
    for (int index = 0; index < list.size(); ++index)
      array[index] = list.get(index);
    return array;
  }
}
