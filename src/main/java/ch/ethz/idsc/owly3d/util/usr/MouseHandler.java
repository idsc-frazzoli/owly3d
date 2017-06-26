// code by jph
package ch.ethz.idsc.owly3d.util.usr;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class MouseHandler {
  private final int[] pressed = new int[10];
  private double last_xpos = 0; // unit == PIXEL, Java conventions
  private double last_ypos = 0;
  private double drag_x = 0;
  private double drag_y = 0;
  public final GLFWMouseButtonCallbackI button = new GLFWMouseButtonCallbackI() {
    @Override
    public void invoke(long window, int button, int action, int mods) {
      // System.out.println("button " + button + " action=" + action);
      pressed[button] = action;
      if (pressed[0] == 0) {
        drag_x = 0;
        drag_y = 0;
      }
    }
  };
  public final GLFWCursorPosCallbackI position = new GLFWCursorPosCallbackI() {
    @Override
    public void invoke(long window, double xpos, double ypos) {
      if (pressed[0] == 1) {
        drag_x = xpos - last_xpos;
        drag_y = ypos - last_ypos;
        // System.out.println(Tensors.vector(drag_x, drag_y));
      } else {
        drag_x = 0;
        drag_y = 0;
      }
      last_xpos = xpos;
      last_ypos = ypos;
      // System.out.println("x,y " + xpos + " " + ypos);
      // GLFWScrollCallback
    }
  };
  public final GLFWScrollCallbackI scroll = new GLFWScrollCallbackI() {
    @Override
    public void invoke(long window, double xoffset, double yoffset) {
      System.out.println(xoffset + " " + yoffset);
    }
  };

  public Tensor drag(boolean reset) {
    Tensor drag = Tensors.vector(drag_x, drag_y);
    if (reset) {
      drag_x = 0;
      drag_y = 0;
    }
    return drag;
  }
}
