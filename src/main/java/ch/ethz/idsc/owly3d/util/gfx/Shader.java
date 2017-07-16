// code by jph
package ch.ethz.idsc.owly3d.util.gfx;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;

import org.lwjgl.opengl.GL20;

public class Shader {
  public final int shader;

  Shader(String string, int type) {
    shader = GL20.glCreateShader(type);
    // System.out.println("shaders " + vsid + " " + fsid);
    GL20.glShaderSource(shader, string);
    GL20.glCompileShader(shader);
    // int status =
    // GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
    int compiled = GL20.glGetShaderi(shader, GL_COMPILE_STATUS);
    // System.out.println("status=" + status);
    String log = GL20.glGetShaderInfoLog(shader);
    if (!log.isEmpty()) {
      System.out.println(log);
      throw new RuntimeException(log);
    }
    if (compiled == 0) {
      throw new AssertionError("Could not compile shader");
    }
  }
}
