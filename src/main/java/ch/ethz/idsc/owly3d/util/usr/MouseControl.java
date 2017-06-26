// code by jph
package ch.ethz.idsc.owly3d.util.usr;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class MouseControl {
  private final MouseHandler mouseHandler;

  public MouseControl(MouseHandler mouseHandler) {
    this.mouseHandler = mouseHandler;
  }

  public Tensor getDrag() {
    Tensor local = mouseHandler.drag(true).negate();
    return Tensors.of(local.Get(1), local.Get(0), RealScalar.of(0));
  }
}
