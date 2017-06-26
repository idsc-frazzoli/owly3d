// code by jph
package ch.ethz.idsc.owly3d.util;

import java.awt.Dimension;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

public enum WindowUtils {
  ;
  // ---
  public static Dimension getWindowSize(long window) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*
      // Get the window size passed to glfwCreateWindow
      GLFW.glfwGetWindowSize(window, pWidth, pHeight);
      return new Dimension(pWidth.get(0), pHeight.get(0));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }
}
