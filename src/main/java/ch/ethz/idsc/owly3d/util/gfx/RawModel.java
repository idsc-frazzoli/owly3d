package ch.ethz.idsc.owly3d.util.gfx;

public class RawModel {
  final int vaoID;
  final int vertexCount;

  public RawModel(int vaoID, final int vertexCount) {
    this.vaoID = vaoID;
    this.vertexCount = vertexCount;
  }
}
