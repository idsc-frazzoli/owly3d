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
    // TODO free shader resources after creating program
  }
}
