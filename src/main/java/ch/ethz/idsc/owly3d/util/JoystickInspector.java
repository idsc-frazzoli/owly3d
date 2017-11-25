// code by jph
package ch.ethz.idsc.owly3d.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import ch.ethz.idsc.retina.dev.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEncoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;

public class JoystickInspector {
  private static String channel(String string) {
    // TODO check if trim
    return "joystick." + string.trim().replace(' ', '_').replaceAll("\\W", "").toLowerCase();
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
        System.out.println("JOYSTICK:" + string.toUpperCase());
        final String channel = channel(string);
        System.out.println(channel);
        {
          String guid = GLFW.glfwGetJoystickGUID(joystick);
          System.out.println("GUID: " + guid);
        }
        {
          FloatBuffer axes = GLFW.glfwGetJoystickAxes(joystick);
          System.out.println("axes: " + axes.limit());
        }
        {
          ByteBuffer byteBuffer = GLFW.glfwGetJoystickButtons(joystick);
          System.out.println("buttons: " + byteBuffer.limit());
        }
        {
          ByteBuffer hats = GLFW.glfwGetJoystickHats(joystick);
          System.out.println("hats: " + hats.limit());
        }
        for (int c = 0; c < 1000000; ++c) {
          {
            byte[] data = new byte[30];
            ByteBuffer dest = ByteBuffer.wrap(data);
            FloatBuffer axes = GLFW.glfwGetJoystickAxes(joystick);
            ByteBuffer buttons = GLFW.glfwGetJoystickButtons(joystick);
            ByteBuffer hats = GLFW.glfwGetJoystickHats(joystick);
            JoystickEncoder.encode(JoystickType.GENERIC_XBOX_PAD, axes, buttons, hats, dest);
            dest.flip();
            // System.out.println(dest.limit());
            JoystickEvent joystickEvent = JoystickDecoder.decode(dest);
            System.out.println(joystickEvent);
            // LogitechLogitechCordlessRumblepad2 genericXboxPadJoystick = (LogitechLogitechCordlessRumblepad2) abstractJoystick;
            // genericXboxPadJoystick.isButtonPressedA();
            // {
            // double val1 = genericXboxPadJoystick.getLeftSliderUnitValue();
            // double val2 = genericXboxPadJoystick.getRightSliderUnitValue();
            // // System.out.println(val1 + " " + val2);
            // }
            // {
            // double val1 = genericXboxPadJoystick.getRightKnobDirectionRight();
            // double val2 = genericXboxPadJoystick.getRightKnobDirectionUp();
            // System.out.println(val1 + " " + val2);
            // }
            // // System.out.println(genericXboxPadJoystick.isPressedAxisRight());
            // genericXboxPadJoystick.toInfoString();
            // set.add(genericXboxPadJoystick.getAxis0());
            // System.out.println(set.size());
            // System.out.println(dest.limit());
          }
          Thread.sleep(500);
        }
      } else {
        System.err.println("no joystick found");
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
