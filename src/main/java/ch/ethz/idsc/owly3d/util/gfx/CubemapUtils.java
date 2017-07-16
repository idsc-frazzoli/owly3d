package ch.ethz.idsc.owly3d.util.gfx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBSeamlessCubeMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBImage;

public enum CubemapUtils {
  ;
  public static ByteBuffer quadVertices = createFullScreenQuad();

  public static int createCubemapTexture(String string, boolean seamless) throws IOException {
    final int tex = GL11.glGenTextures();
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, tex);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    ByteBuffer byteBuffer;
    IntBuffer w = BufferUtils.createIntBuffer(1);
    IntBuffer h = BufferUtils.createIntBuffer(1);
    IntBuffer comp = BufferUtils.createIntBuffer(1);
    String[] names = { "right", "left", "top", "bottom", "front", "back" };
    ByteBuffer image;
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
    for (int i = 0; i < 6; i++) {
      byteBuffer = DemoUtils.ioResourceToByteBuffer(string + names[i] + (i + 1) + ".jpg", 8 * 1024);
      if (!STBImage.stbi_info_from_memory(byteBuffer, w, h, comp))
        throw new IOException("Failed to read image information: " + STBImage.stbi_failure_reason());
      image = STBImage.stbi_load_from_memory(byteBuffer, w, h, comp, 0);
      if (image == null)
        throw new IOException("Failed to load image: " + STBImage.stbi_failure_reason());
      GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGB8, w.get(0), h.get(0), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
      STBImage.stbi_image_free(image);
    }
    if (seamless) {
      // System.out.println("seamless");
      GL11.glEnable(ARBSeamlessCubeMap.GL_TEXTURE_CUBE_MAP_SEAMLESS);
    }
    return tex;
  }

  public static ByteBuffer createFullScreenQuad() {
    ByteBuffer quadVertices = BufferUtils.createByteBuffer(4 * 2 * 6);
    FloatBuffer fv = quadVertices.asFloatBuffer();
    // fv.put(-0.5f).put(-0.5f);
    fv.put(-1.0f).put(-1.0f);
    fv.put(1.0f).put(-1.0f);
    fv.put(1.0f).put(1.0f);
    fv.put(1.0f).put(1.0f);
    fv.put(-1.0f).put(1.0f);
    fv.put(-1.0f).put(-1.0f);
    return quadVertices;
  }
}
