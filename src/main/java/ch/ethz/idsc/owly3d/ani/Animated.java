// code by jph
package ch.ethz.idsc.owly3d.ani;

import ch.ethz.idsc.tensor.Scalar;

public interface Animated {
  void control();

  /** @param now */
  void integrate(Scalar now);

  /** called after draw routine */
  void resetControl();
}
