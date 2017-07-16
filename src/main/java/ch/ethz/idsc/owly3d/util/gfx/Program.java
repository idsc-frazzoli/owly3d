// code by jph
package ch.ethz.idsc.owly3d.util.gfx;

import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;

import org.lwjgl.opengl.GL20;

public class Program {
  public static Program of(int vs, int fs) {
    return new Program(vs, fs);
  }

  public final int program;

  Program(int vs, int fs) {
    program = GL20.glCreateProgram();
    GL20.glAttachShader(program, vs);
    GL20.glAttachShader(program, fs);
    GL20.glLinkProgram(program);
    int linked = glGetProgrami(program, GL_LINK_STATUS);
    String programLog = glGetProgramInfoLog(program);
    if (programLog != null && programLog.trim().length() > 0) {
      System.err.println(programLog);
    }
    if (linked == 0) {
      throw new AssertionError("Could not link program");
    }
    // return program;
  }
}
