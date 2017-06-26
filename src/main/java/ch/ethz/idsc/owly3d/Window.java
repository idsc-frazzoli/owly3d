// code adapted from hello world example from lwjgl
// code by jph
package ch.ethz.idsc.owly3d;

import java.awt.Dimension;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.owly3d.util.WindowUtils;
import ch.ethz.idsc.owly3d.util.math.ProjectionMatrix;
import ch.ethz.idsc.tensor.Tensor;

public class Window {
  public long window;

  public void init() {
    System.out.println("LWJGL " + Version.getVersion());
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();
    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!GLFW.glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW");
    // Configure GLFW
    GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable
    // Create the window
    window = GLFW.glfwCreateWindow(1000, 1000, "owly3d", MemoryUtil.NULL, MemoryUtil.NULL);
    if (window == MemoryUtil.NULL)
      throw new RuntimeException("Failed to create the GLFW window");
    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    // Get the thread stack and push a new frame
    Dimension dimension = WindowUtils.getWindowSize(window);
    // Get the resolution of the primary monitor
    GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
    // Center the window
    GLFW.glfwSetWindowPos(window, (vidmode.width() - dimension.width) / 2, (vidmode.height() - dimension.height) / 2);
    // Make the OpenGL context current
    GLFW.glfwMakeContextCurrent(window);
    // Enable v-sync
    GLFW.glfwSwapInterval(1);
    // Make the window visible
    GLFW.glfwShowWindow(window);
  }

  public void terminate() {
    // Free the window callbacks and destroy the window
    Callbacks.glfwFreeCallbacks(window);
    GLFW.glfwDestroyWindow(window);
    // Terminate GLFW and free the error callback
    GLFW.glfwTerminate();
    GLFW.glfwSetErrorCallback(null).free();
  }

  public void setViewportAndPerspective() {
    Dimension dimension = WindowUtils.getWindowSize(window);
    // TODO only update this when window size changes
    GL11.glViewport(0, 0, dimension.width, dimension.height);
    GL11.glMatrixMode(GL11.GL_PROJECTION); // confirmed
    GL11.glLoadIdentity();
    Tensor matrix = ProjectionMatrix.perspective(1.1, dimension.width / (double) dimension.height, 1, 100);
    GL11.glMultMatrixd(Primitives2.matrix44(matrix)); // confirmed
  }
}
