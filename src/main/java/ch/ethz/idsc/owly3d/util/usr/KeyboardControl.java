// code by jph
package ch.ethz.idsc.owly3d.util.usr;

import org.lwjgl.glfw.GLFW;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class KeyboardControl {
  private final KeyboardHander keyHander;

  public KeyboardControl(KeyboardHander keyHander) {
    this.keyHander = keyHander;
  }

  public Tensor getPushAWSD() {
    return Tensors.vector( //
        +keyHander.pressed(GLFW.GLFW_KEY_W) - keyHander.pressed(GLFW.GLFW_KEY_S), // local x
        +keyHander.pressed(GLFW.GLFW_KEY_A) - keyHander.pressed(GLFW.GLFW_KEY_D), //
        0);
  }

  public Tensor getPushArrows() {
    return Tensors.vector( //
        keyHander.pressed(GLFW.GLFW_KEY_UP) - keyHander.pressed(GLFW.GLFW_KEY_DOWN), // local x
        keyHander.pressed(GLFW.GLFW_KEY_LEFT) - keyHander.pressed(GLFW.GLFW_KEY_RIGHT), // local y
        keyHander.pressed(GLFW.GLFW_KEY_PAGE_UP) - keyHander.pressed(GLFW.GLFW_KEY_PAGE_DOWN) // local z
    // + keyHander.pressed(GLFW.GLFW_KEY_KP_7) - keyHander.pressed(GLFW.GLFW_KEY_KP_1) //
    );
  }

  public Tensor getPushKP() {
    return Tensors.vector( //
        +keyHander.pressed(GLFW.GLFW_KEY_KP_8) - keyHander.pressed(GLFW.GLFW_KEY_KP_2), // local x
        +keyHander.pressed(GLFW.GLFW_KEY_KP_4) - keyHander.pressed(GLFW.GLFW_KEY_KP_6), // local y
        +keyHander.pressed(GLFW.GLFW_KEY_KP_9) - keyHander.pressed(GLFW.GLFW_KEY_KP_3) // local z
    );
  }

  public Tensor getDrag() {
    return Tensors.vector(0, 0, //
        keyHander.pressed(GLFW.GLFW_KEY_KP_1) - keyHander.pressed(GLFW.GLFW_KEY_KP_7) //
            + keyHander.pressed(GLFW.GLFW_KEY_INSERT) - keyHander.pressed(GLFW.GLFW_KEY_HOME));
  }

  public Scalar pressed(int id) {
    return RealScalar.of(keyHander.pressed(id));
  }
}
