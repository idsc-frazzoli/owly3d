// code by jph
package ch.ethz.idsc.owly3d.util;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Numel;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Primitives;

public enum Primitives2 {
  ;
  // ---
  public static Tensor matrix44(double[] values) {
    if (values.length != 16)
      throw new RuntimeException();
    return Transpose.of(Partition.of(Tensors.vectorDouble(values), 4));
  }

  public static double[] matrix44(Tensor matrix) {
    return Primitives.toArrayDouble(Transpose.of(matrix));
  }

  /** @param tensor
   * @return */
  public static ByteBuffer toByteBuffer(Tensor tensor) {
    ByteBuffer intBuffer = ByteBuffer.allocate(Numel.of(tensor));
    Primitives.toStreamNumber(tensor).map(Number::byteValue).forEach(intBuffer::put);
    intBuffer.flip();
    // intBuffer.rewind();
    return intBuffer;
  }
}
