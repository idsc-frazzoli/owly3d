// code by jph
package ch.ethz.idsc.owly3d.util.usr;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class KeyboardHander implements GLFWKeyCallbackI {
  public static final int KEY_UP = GLFW.GLFW_KEY_UP;
  public static final int KEY_DOWN = GLFW.GLFW_KEY_DOWN;
  public static final int KEY_LEFT = GLFW.GLFW_KEY_LEFT;
  public static final int KEY_RIGHT = GLFW.GLFW_KEY_RIGHT;
  public static final int KEY_PAGE_UP = GLFW.GLFW_KEY_PAGE_UP;
  public static final int KEY_PAGE_DOWN = GLFW.GLFW_KEY_PAGE_DOWN;
  // ---
  private final int[] pressed = new int[512];
  private final int[] hit = new int[512];

  @Override
  public void invoke(long window, int key, int scancode, int action, int mods) {
    // GLFW.GLFW_KEY_PAGE_DOWN
    if (action == GLFW.GLFW_PRESS) {
      pressed[key] = 1;
      hit[key] = 1;
    }
    if (action == GLFW.GLFW_RELEASE)
      pressed[key] = 0;
    // TODO not final location
    if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
      GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
  }

  public int pressed(int key) {
    return pressed[key];
  }

  public int hit(int key) {
    return hit[key];
  }

  public void reset() {
    for (int c = 0; c < hit.length; ++c)
      hit[c] = 0;
  }
}
