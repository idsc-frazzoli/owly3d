package ch.ethz.idsc.owly3d;

import org.lwjgl.glfw.GLFW;

import ch.ethz.idsc.owly3d.util.usr.KeyboardControl;
import ch.ethz.idsc.owly3d.util.usr.KeyboardHander;
import ch.ethz.idsc.owly3d.util.usr.MouseControl;
import ch.ethz.idsc.owly3d.util.usr.MouseHandler;

public abstract class Workspace {
  final Window windowObject = new Window();
  final KeyboardHander keyboardHander = new KeyboardHander();
  KeyboardControl keyboardControl;
  MouseControl mouseControl;

  protected void init() {
    MouseHandler mouseHandler = new MouseHandler();
    GLFW.glfwSetKeyCallback(windowObject.window, keyboardHander);
    GLFW.glfwSetMouseButtonCallback(windowObject.window, mouseHandler.button);
    GLFW.glfwSetCursorPosCallback(windowObject.window, mouseHandler.position);
    GLFW.glfwSetScrollCallback(windowObject.window, mouseHandler.scroll);
    keyboardControl = new KeyboardControl(keyboardHander);
    mouseControl = new MouseControl(mouseHandler);
    // TODO p.28 search for equivalent to: glutInitDisplayMode(rgba, depth, double) ...?
  }

  protected abstract void loop();
}
