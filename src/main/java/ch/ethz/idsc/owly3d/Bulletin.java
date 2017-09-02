// code by jph
package ch.ethz.idsc.owly3d;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Bulletin {
  private List<String> list = new LinkedList<>();

  public void append(String line) {
    list.add(line);
  }

  @Override
  public String toString() {
    return list.stream().collect(Collectors.joining("\n"));
  }

  public void clear() {
    list.clear();
  }
}
