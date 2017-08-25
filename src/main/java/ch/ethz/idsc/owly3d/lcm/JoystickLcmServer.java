// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import ch.ethz.idsc.owly3d.util.usr.JoystickControl;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class JoystickLcmServer {
  public static void main(String[] args) throws InterruptedException {
    try {
      System.out.println("hello world");
      System.out.println("LWJGL " + Version.getVersion());
      // Setup an error callback. The default implementation
      // will print the error message in System.err.
      GLFWErrorCallback.createPrint(System.err).set();
      // Initialize GLFW. Most GLFW functions will not work before doing this.
      if (!GLFW.glfwInit())
        throw new IllegalStateException("Unable to initialize GLFW");
      JoystickControl.printInfo();
      while (true) {
        System.out.println(JoystickControl.getAxes());
        BinaryBlob binaryBlob = new BinaryBlob();
        binaryBlob.data = new byte[8 + 4 * 12];
        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(System.currentTimeMillis());
        LCM.getSingleton().publish("joystick.all", binaryBlob);
        Thread.sleep(100);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
