// code by jph
package ch.ethz.idsc.owly3d.util.usr;

import java.util.stream.IntStream;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public final class KeyboardHander implements GLFWKeyCallbackI {
  private final int[] pressed = new int[GLFW.GLFW_KEY_LAST]; // 348
  private final boolean[] hit = new boolean[GLFW.GLFW_KEY_LAST]; // 348

  @Override
  public void invoke(long window, int key, int scancode, int action, int mods) {
    if (action == GLFW.GLFW_PRESS) {
      pressed[key] = 1;
      hit[key] = true;
    }
    if (action == GLFW.GLFW_RELEASE)
      pressed[key] = 0;
  }

  public int pressed(int key) {
    return pressed[key];
  }

  public boolean hit(int key) {
    return hit[key];
  }

  public void reset() {
    IntStream.range(0, hit.length) //
        .forEach(key -> hit[key] = false);
  }
}
