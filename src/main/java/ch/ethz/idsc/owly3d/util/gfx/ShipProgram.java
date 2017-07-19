package ch.ethz.idsc.owly3d.util.gfx;

import java.io.IOException;

import org.lwjgl.opengl.GL20;

public class ShipProgram {
  public final int shipProgram;
  public final int ship_viewUniform;
  public final int ship_projUniform;
  public final int ship_modelUniform;

  public ShipProgram(String resource) throws IOException {
    final int program = Programs.of(resource).program;
    GL20.glUseProgram(program);
    ship_viewUniform = GL20.glGetUniformLocation(program, "view");
    ship_projUniform = GL20.glGetUniformLocation(program, "proj");
    ship_modelUniform = GL20.glGetUniformLocation(program, "model");
    GL20.glUseProgram(0);
    shipProgram = program;
  }
}
