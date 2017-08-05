// code by jph
package ch.ethz.idsc.owly3d.util.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public enum MatrixFunctions {
  ;
  public static Tensor getSE3(Tensor rotation, Tensor position) {
    Tensor matrix = IdentityMatrix.of(4);
    {
      matrix.set(rotation.Get(0, 0), 0, 0);
      matrix.set(rotation.Get(0, 1), 0, 1);
      matrix.set(rotation.Get(0, 2), 0, 2);
      matrix.set(rotation.Get(1, 0), 1, 0);
      matrix.set(rotation.Get(1, 1), 1, 1);
      matrix.set(rotation.Get(1, 2), 1, 2);
      matrix.set(rotation.Get(2, 0), 2, 0);
      matrix.set(rotation.Get(2, 1), 2, 1);
      matrix.set(rotation.Get(2, 2), 2, 2);
    }
    {
      matrix.set(position.Get(0), 0, 3);
      matrix.set(position.Get(1), 1, 3);
      matrix.set(position.Get(2), 2, 3);
    }
    return matrix;
  }

  public static Tensor getTranslation(Tensor position) {
    Tensor matrix = IdentityMatrix.of(4);
    {
      matrix.set(position.Get(0), 0, 3);
      matrix.set(position.Get(1), 1, 3);
      matrix.set(position.Get(2), 2, 3);
    }
    return matrix;
  }
}
