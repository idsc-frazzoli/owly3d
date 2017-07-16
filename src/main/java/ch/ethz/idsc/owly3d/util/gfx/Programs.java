package ch.ethz.idsc.owly3d.util.gfx;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public enum Programs {
  ;
  private static final String SEPARATOR = "===";

  // ---
  public static Program of(String resource) throws IOException {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
    File file = new File(url.getFile());
    List<String> list = Files.lines(file.toPath()) //
        .map(String::trim) //
        .filter(string -> !string.isEmpty()) //
        .collect(Collectors.toList());
    int index = list.indexOf(SEPARATOR);
    String vstr = list.subList(0, index).stream().collect(Collectors.joining("\n"));
    String fstr = list.subList(index + 1, list.size()).stream().collect(Collectors.joining("\n"));
    return new Program( //
        new Shader(vstr, GL_VERTEX_SHADER).shader, //
        new Shader(fstr, GL_FRAGMENT_SHADER).shader);
  }
  // public static Program of2(String resource) throws IOException {
  // // createShader(filename+".vs", );
  // int vshader;
  // {
  // URL url = Thread.currentThread().getContextClassLoader().getResource(resource + ".vs");
  // File file = new File(url.getFile());
  // vshader = Shader.of(file, GL_VERTEX_SHADER).shader;
  // }
  // int fshader;
  // {
  // URL url = Thread.currentThread().getContextClassLoader().getResource(resource + ".fs");
  // File file = new File(url.getFile());
  // fshader = Shader.of(file, GL_FRAGMENT_SHADER).shader;
  // }
  // // int fshader = createShader(filename+".fs", GL_FRAGMENT_SHADER);
  // // int program = createProgram(vshader, fshader);
  // return Program.of(vshader, fshader);
  // }
}
