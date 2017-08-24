// code by jph
package ch.ethz.idsc.owly3d.lcm;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import ch.ethz.idsc.owly3d.util.usr.JoystickControl;

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
      for (int c = 0; c < 100; ++c) {
        System.out.println(JoystickControl.getAxes());
        Thread.sleep(500);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
