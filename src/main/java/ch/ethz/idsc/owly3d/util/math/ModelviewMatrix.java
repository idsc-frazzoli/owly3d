// code by jph
package ch.ethz.idsc.owly3d.util.math;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.red.Norm;

public enum ModelviewMatrix {
  ;
  // ---
  public static void print() {
    double[] values = new double[16];
    GL11.glGetDoublev(GL11.GL_MODELVIEW_MATRIX, values);
    Tensor matrix = Primitives2.matrix44(values);
    System.out.println("GL_MODELVIEW_MATRIX=");
    System.out.println(Pretty.of(matrix));
  }

  /** @param src 4-vector
   * @param dst 4-vector
   * @return */
  public static Tensor lookAt(Tensor src, Tensor dst) {
    Tensor dir = src.subtract(dst);
    dir.set(RealScalar.ZERO, 3);
    Scalar nrm = Norm._2.of(dir);
    if (Scalars.nonZero(nrm)) {
      dir = dir.divide(nrm);
      Tensor upv = UnitVector.of(4, 2);
      Tensor frw = Normalize.of(Cross.of(upv.extract(0, 3), dir.extract(0, 3)));
      frw.append(RealScalar.ZERO);
      Tensor side = Cross.of(dir.extract(0, 3), frw.extract(0, 3));
      side.append(RealScalar.ZERO);
      return Transpose.of(Tensors.of(frw, side, dir, src));
    }
    return IdentityMatrix.of(4);
  }
}
