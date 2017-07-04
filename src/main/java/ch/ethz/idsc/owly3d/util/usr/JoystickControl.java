// code by jph
package ch.ethz.idsc.owly3d.util.usr;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Round;

public enum JoystickControl {
  ;
  // TODO get name of hama
  // "Generic X-Box pad"
  public static void printInfo() {
    for (int i = GLFW.GLFW_JOYSTICK_1; i < GLFW.GLFW_JOYSTICK_10; ++i) {
      String string = GLFW.glfwGetJoystickName(i);
      if (string != null)
        System.out.println(string);
    }
  }

  /** for hama joystick:
   * [0] = +1 right
   * [1] = +1 pull towards user (!)
   * [2] = +1 lever down, -1 lever up
   * [3] = twist
   * [4] = unknown
   * [5] = blue knob +1 right
   * [6] = blue knob +1 pull towards user
   * 
   * for Generic X-Box pad:
   * [0] = +1 right (on left knob)
   * [1] = +1 pull towards user (on left knob)
   * [2] -1 for as-is, +1 for all inward
   * [3] = +1 right (on right knob)
   * [4] = +1 pull towards user (on right knob)
   * [5] -1 for as-is, +1 for all inward
   * 
   * @return vector with axes as available and with values as-is */
  public static Tensor getAxes() {
    int joystick = GLFW.GLFW_JOYSTICK_1;
    FloatBuffer floatBuffer = GLFW.glfwGetJoystickAxes(joystick);
    Tensor tensor = Array.zeros(12);
    if (floatBuffer != null) {
      // System.out.println("capacity=" + floatBuffer.capacity());
      int min = Math.min(floatBuffer.capacity(), tensor.length());
      for (int index = 0; index < min; ++index)
        tensor.set(RealScalar.of(floatBuffer.get(index)), index);
    }
    return tensor;
  }

  /** @return vector with axes as available and with values as-is */
  public static Tensor getButtons() {
    int joystick = GLFW.GLFW_JOYSTICK_1;
    ByteBuffer byteBuffer = GLFW.glfwGetJoystickButtons(joystick);
    Tensor tensor = Array.zeros(13);
    if (byteBuffer != null) {
      int min = Math.min(byteBuffer.capacity(), tensor.length());
      for (int index = 0; index < min; ++index) {
        int value = byteBuffer.get(index);
        tensor.set(RealScalar.of(value), index);
      }
    }
    return tensor;
  }

  public static void printAxes() {
    System.out.println(Pretty.of(getAxes().map(Round._2)));
  }
}
