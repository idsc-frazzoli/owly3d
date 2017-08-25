// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class JoystickLcmServer {
  private static String channel(String string) {
    return "joystick." + string.replace(' ', '_').replaceAll("\\W", "").toLowerCase();
  }

  public static void main(String[] args) {
    try {
      GLFWErrorCallback.createPrint(System.err).set();
      // Initialize GLFW. Most GLFW functions will not work before doing this.
      if (!GLFW.glfwInit())
        throw new IllegalStateException("Unable to initialize GLFW");
      int joystick = GLFW.GLFW_JOYSTICK_1;
      final String string = GLFW.glfwGetJoystickName(joystick);
      if (Objects.nonNull(string)) {
        final String channel = channel(string);
        System.out.println(channel);
        FloatBuffer floatBuffer = GLFW.glfwGetJoystickAxes(joystick);
        final int axes = floatBuffer.limit();
        BinaryBlob binaryBlob = new BinaryBlob();
        binaryBlob.data_length = 8 + 4 * axes;
        binaryBlob.data = new byte[8 + 4 * axes];
        while (true) {
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          byteBuffer.putLong(System.currentTimeMillis()); // append time
          floatBuffer = GLFW.glfwGetJoystickAxes(joystick);
          for (int count = 0; count < axes; ++count)
            byteBuffer.putFloat(floatBuffer.get()); // append axis
          LCM.getSingleton().publish(channel, binaryBlob);
          Thread.sleep(50);
        }
      } else {
        System.err.println("no joystick found");
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
