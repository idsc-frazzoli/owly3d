package ch.ethz.idsc.owly3d.mesh;

import java.io.File;

import ch.ethz.idsc.tensor.io.Get;

public enum MeshFormat {
  ;
  public static Mesh read(File directory) {
    //
    try {
      Mesh mesh = new Mesh();
      mesh.vertices = Get.of(new File(directory, "vertices.txt"));
      mesh.quads = Get.of(new File(directory, "quads.txt"));
      return mesh;
    } catch (Exception e) {
      // ---
    }
    return null;
  }
}
