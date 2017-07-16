// code adapted from hello world example from lwjgl
// code by jph
package ch.ethz.idsc.owly3d;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import ch.ethz.idsc.owly.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owly3d.ani.obj.Avatar;
import ch.ethz.idsc.owly3d.ani.obj.EjCar;
import ch.ethz.idsc.owly3d.ani.obj.EjCarDrawable;
import ch.ethz.idsc.owly3d.ani.obj.Rice2Mover;
import ch.ethz.idsc.owly3d.ani.obj.Rice2MoverDrawable;
import ch.ethz.idsc.owly3d.ani.obj.Se2Car;
import ch.ethz.idsc.owly3d.ani.obj.Se2CarDrawable;
import ch.ethz.idsc.owly3d.ani.obj.Tracker;
import ch.ethz.idsc.owly3d.mesh.Mesh;
import ch.ethz.idsc.owly3d.mesh.MeshFormat;
import ch.ethz.idsc.owly3d.sim.TimeKeeper;
import ch.ethz.idsc.owly3d.util.AxesHelper;
import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.owly3d.util.gfx.CubemapUtils;
import ch.ethz.idsc.owly3d.util.gfx.Programs;
import ch.ethz.idsc.owly3d.util.usr.JoystickControl;
import ch.ethz.idsc.owly3d.util.usr.KeyboardControl;
import ch.ethz.idsc.owly3d.util.usr.KeyboardHander;
import ch.ethz.idsc.owly3d.util.usr.MouseControl;
import ch.ethz.idsc.owly3d.util.usr.MouseHandler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Clip;

public class Owly3d {
  Window windowObject = new Window();
  Avatar avatar;
  Tracker tracker;
  Rice2Mover rice2Mover = new Rice2MoverDrawable(Tensors.vector(2, 2, -1, 1));
  Se2Car se2Car = new Se2CarDrawable(Tensors.vector(0, 3, 0), RealScalar.of(2));
  EjCar ejCar = new EjCarDrawable();
  Scenario scenario = new Scenario();
  KeyboardControl keyboardControl;
  MouseControl mouseControl;
  TrajectoryPlanner trajectoryPlanner;

  private void init() {
    KeyboardHander keyboardHander = new KeyboardHander();
    MouseHandler mouseHandler = new MouseHandler();
    GLFW.glfwSetKeyCallback(windowObject.window, keyboardHander);
    GLFW.glfwSetMouseButtonCallback(windowObject.window, mouseHandler.button);
    GLFW.glfwSetCursorPosCallback(windowObject.window, mouseHandler.position);
    GLFW.glfwSetScrollCallback(windowObject.window, mouseHandler.scroll);
    keyboardControl = new KeyboardControl(keyboardHander);
    mouseControl = new MouseControl(mouseHandler);
    // TODO p.28 search for equivalent to: glutInitDisplayMode(rgba, depth, double) ...?
  }

  private static Scalar unit_one(Scalar convert) {
    return convert.add(RealScalar.ONE).multiply(RealScalar.of(0.5));
  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();
    { // set the clear color
      GL11.glClearDepth(1);
      float gray = 0.2f;
      GL11.glClearColor(gray, gray, gray, 0);
    }
    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    JoystickControl.printInfo();
    avatar = new Avatar(Tensors.vector(0, 0, 15));
    scenario.add(avatar);
    scenario.add(se2Car);
    scenario.add(ejCar);
    scenario.add(rice2Mover);
    tracker = new Tracker(Array.zeros(6));
    scenario.add(tracker);
    // ---
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_BLEND);
    // ---
    GL11.glDepthFunc(GL11.GL_LESS); // confirmed
    GL11.glEnable(GL11.GL_DEPTH_TEST); // TODO
    // ---
    // GL11.glDepthMask(true); // enabled by default
    GL11.glDrawBuffer(GL11.GL_BACK); // confirmed
    // trajectoryPlanner = PsuDemo.simple();
    { // p.62
      GL11.glFrontFace(GL11.GL_CCW);
      GL11.glCullFace(GL11.GL_BACK);
      GL11.glEnable(GL11.GL_CULL_FACE);
    }
    // GL11.glEnable(GL44.GL_MAX_VERTEX_ATTRIB_STRIDE);
    Texture.bind();
    // Shader.load();
    DemoTriangle dt = new DemoTriangle();
    // Shader shader02 = Shader.of( //
    // new File("resources/shader/tutorial02/SimpleVertexShader.vertexshader"), //
    // new File("resources/shader/tutorial02/SimpleFragmentShader.fragmentshader") //
    // );
    // Shader shader03 = Shader.of( //
    // new File("resources/shader/tutorial03/SimpleTransform.vertexshader"), //
    // new File("resources/shader/tutorial03/SingleColor.fragmentshader") //
    // );
    // final int matrixID = GL20.glGetUniformLocation(shader03.program, "MVP");
    // System.out.println("shader = " + shader03.program + " MVP=" + matrixID);
    try {
      CubemapUtils.createCubemapTexture("cube/space/space_", true);
      createCubemapProgram();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Mesh cubesn = MeshFormat.read(new File("/home/datahaki/Documents/models/cubesn"));
    TimeKeeper timeKeeper = new TimeKeeper();
    while (!GLFW.glfwWindowShouldClose(windowObject.window)) {
      final Scalar now = timeKeeper.now();
      // ---
      { // controls
        if (keyboardControl.isHit(GLFW.GLFW_KEY_R)) {
          ejCar.reset();
        }
        {
          if (Scalars.nonZero(JoystickControl.getButtons().Get(0)))
            ejCar.reset();
        }
        {
          avatar.addPush(keyboardControl.getPushKP());
          avatar.addRotate( //
              keyboardControl.getDrag().add(mouseControl.getDrag()).multiply(RealScalar.of(.01)));
        }
        rice2Mover.addPush(keyboardControl.getPushArrows());
        {
          Tensor tensor = keyboardControl.getPushAWSD();
          se2Car.addControl(tensor.Get(1), tensor.Get(0));
        }
        {
          Tensor axes = JoystickControl.getAxes();
          se2Car.addControl(axes.Get(0).negate(), axes.Get(1).negate());
        }
        {
          Tensor tensor = keyboardControl.getPushAWSD();
          ejCar.addControl( //
              tensor.Get(1), //
              Clip.UNIT.apply(tensor.Get(0).negate()), // brake
              keyboardControl.pressed(GLFW.GLFW_KEY_E), // handbrake
              keyboardControl.pressed(GLFW.GLFW_KEY_W), //
              keyboardControl.pressed(GLFW.GLFW_KEY_Q));
        }
        {
          // JoystickControl.printAxes();
          Tensor axes = JoystickControl.getAxes();
          Scalar mono = Clip.UNIT.apply(axes.Get(1).negate());
          // TODO based on joystick
          // ejCar.addControl( //
          // axes.Get(0).negate(), // delta
          // Clip.UNIT.apply(axes.Get(1)), // brake
          // Clip.UNIT.apply(axes.Get(2)), // handbrake
          // mono, mono); // throttle
          if (false) // FIXME
            ejCar.addControl( //
                axes.Get(0).negate(), // delta
                Clip.UNIT.apply(axes.Get(1)), // brake
                RealScalar.ZERO, // handbrake
                unit_one(axes.Get(2)), //
                unit_one(axes.Get(5))); // throttle
        }
        {
          Tensor pose = ejCar.getSE3();
          Tensor src = pose.dot(Tensors.vector(-5, 0, 3, 1));
          tracker.setGoal(src);
        }
      }
      scenario.integrate(now);
      // ---
      // p.19, p.35
      GL11.glClear( //
          GL11.GL_COLOR_BUFFER_BIT | //
              GL11.GL_DEPTH_BUFFER_BIT | //
              GL11.GL_STENCIL_BUFFER_BIT // GL11.GL_ACCUM_BUFFER_BIT
      );
      GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL); // confirmed
      // ---
      // windowObject.setViewportAndPerspective();
      // {
      // GL11.glMatrixMode(GL11.GL_PROJECTION); // confirmed
      // GL11.glLoadIdentity();
      // TODO glu Ortho2D
      // GL11.glRasterPos2i(30, 30);
      // Tensor vec = RandomVariate.of(DiscreteUniformDistribution.of(0, 256 * 255 * 255), 1000 * 10 * 4);
      // // ByteBuffer pixels = Primitives2.toByteBuffer(vec);
      // IntBuffer pixels = Primitives.toIntBuffer(vec);
      // // ByteBuffer pixels =
      // GL11.glDrawPixels(10, 10, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
      // // opengl_font.size[2],opengl_font.size[1],GL_RGBA,GL_UNSIGNED_BYTE,opengl_font^(chr-32)*inc);
      // }
      windowObject.setViewportAndPerspective();
      { // camera
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // confirmed
        GL11.glLoadIdentity();
        {
          Tensor mat = scenario.cameraMatrix();
          // Tensor pose = ;
          // Tensor src = pose.dot(Tensors.vector(-5, 0, 3, 1));
          // Tensor dst = Transpose.of(ejCar.getSE3()).get(3);
          Tensor car = ejCar.getSE3();
          mat.set(car.Get(0, 3), 0, 3);
          mat.set(car.Get(1, 3).add(RealScalar.of(-0)), 1, 3);
          // Tensor src = Transpose.of(avatar.getSE3()).get(3);
          // mat = ModelviewMatrix.lookAt(src, dst);
          Tensor matrix = Inverse.of(mat);
          GL11.glMultMatrixd(Primitives2.matrix44(matrix)); // confirmed
        }
      }
      {
        FloatBuffer fb = FloatBuffer.wrap(new float[16]);
        fb.put(0.23510265f);
        fb.put(0);
        fb.put(0);
        fb.put(0);
        // ---
        fb.put(0);
        fb.put(0.17632699f);
        fb.put(0);
        fb.put(0);
        // ---
        fb.put(0);
        fb.put(0);
        fb.put(0);
        fb.put(-4.9999f);
        // ---
        fb.put(0);
        fb.put(0);
        fb.put(-1);
        fb.put(5.0001f);
        fb.flip();
        GL20.glUniformMatrix4fv(cubemap_invViewProjUniform, false, fb);
      }
      // GL11.glCullFace(GL11.GL_BACK); // confirmed
      {
        // Mesh mesh = new Mesh();
        // mesh.vertices.append(Tensors.vector(0, 0, 0));
        // mesh.vertices.append(Tensors.vector(1, 0, 0));
        // mesh.vertices.append(Tensors.vector(1, 1, 0));
        // mesh.triangles.append(Tensors.vector(0, 1, 2));
        // mesh.draw();
        render_scene();
        // mesh.drawTest();
        drawCubemap();
      }
      // {
      // for (int c = 1; c < 10; ++c) {
      // GL11.glPushMatrix();
      // GL11.glTranslatef(c, 0, 0);
      // Cylinder.drawZ(0, 1, .2, 20, 10, Tensors.vector(1, 0, 1, 1));
      // GL11.glPopMatrix();
      // }
      // // Cylinder.drawY(1, 60, 30, Tensors.vector(.3, 1, .2, .7));
      // }
      {
        // Cylinder.drawY(-.1, .1, .3, 60, 30, Tensors.vector(.3, 1, .2, .7));
      }
      // {
      // GL20.glUseProgram(shader03.program);
      // // int location, boolean transpose, FloatBuffer value
      // Tensor mat = Inverse.of(scenario.cameraMatrix());
      // float[] MVP = { //
      // 1, 0, 0, 0, //
      // 0, 1, 0, 0, //
      // 0, 0, 1, 0, //
      // 0, 0, -1, 1, 1, 0, 0, 0, //
      // 0, 1, 0, 0, //
      // 0, 0, 1, 0, //
      // 0, 0, -1, 1 };
      // FloatBuffer value = FloatBuffer.wrap(MVP);
      // // Primitives.toFloatBuffer(mat);
      // GL20.glUniformMatrix4fv(matrixID, false, value);
      // cubesn.draw();
      // GL11.glColor3f(1, 1, 1);
      // dt.draw();
      // GL20.glUseProgram(0);
      // }
      // ---
      // p.25
      GLFW.glfwSwapBuffers(windowObject.window); // swap the color buffers
      keyboardControl.reset();
      // Poll for window events. The key callback above will only be
      // invoked during this call.
      GLFW.glfwPollEvents(); // also for joystick...(?)
    }
  }

  private int cubemapProgram;
  private int cubemap_invViewProjUniform;

  private void createCubemapProgram() throws IOException {
    int program = Programs.of("cubemap.vfs").program;
    GL20.glUseProgram(program);
    int texLocation = GL20.glGetUniformLocation(program, "tex");
    GL20.glUniform1i(texLocation, 0); // specifies texture index as 0, not sure why 0?
    cubemap_invViewProjUniform = GL20.glGetUniformLocation(program, "invViewProj");
    GL20.glUseProgram(0);
    cubemapProgram = program;
  }

  private void drawCubemap() {
    glUseProgram(cubemapProgram);
    glVertexPointer(2, GL_FLOAT, 0, CubemapUtils.quadVertices);
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glUseProgram(0);
  }

  private void render_scene() {
    AxesHelper.draw();
    AxesHelper.checker(ejCar.getSE3().get(Tensor.ALL, 3));
    // GL11.glFlush(); // p.20 <- start processing buffered opengl routines
    // new TreeRender3d(trajectoryPlanner.getDomainMap().values()).render();
    // new TrajectoryRender3d(trajectoryPlanner).render();
    // // ---
    scenario.draw();
    // Texture.draw();
    scenario.resetControl();
  }

  public static void main(String[] args) {
    Owly3d owly3d = new Owly3d();
    owly3d.windowObject.init();
    owly3d.init();
    owly3d.loop();
    owly3d.windowObject.terminate();
  }
}