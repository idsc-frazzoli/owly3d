// code adapted from hello world example from lwjgl
// code by jph
package ch.ethz.idsc.owly3d;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import ch.ethz.idsc.owly.data.TimeKeeper;
import ch.ethz.idsc.owly.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owly3d.ani.obj.Avatar;
import ch.ethz.idsc.owly3d.ani.obj.EjCar;
import ch.ethz.idsc.owly3d.ani.obj.EjCarDrawable;
import ch.ethz.idsc.owly3d.ani.obj.Rice2Mover;
import ch.ethz.idsc.owly3d.ani.obj.Rice2MoverDrawable;
import ch.ethz.idsc.owly3d.ani.obj.Se2Car;
import ch.ethz.idsc.owly3d.ani.obj.Se2CarDrawable;
import ch.ethz.idsc.owly3d.ani.obj.Tracker;
import ch.ethz.idsc.owly3d.demo.DemoPointCloud;
import ch.ethz.idsc.owly3d.demo.DemoTriangle1;
import ch.ethz.idsc.owly3d.demo.DemoTriangle2;
import ch.ethz.idsc.owly3d.demo.Example2_17;
import ch.ethz.idsc.owly3d.lcm.Hdl32eLcmRender;
import ch.ethz.idsc.owly3d.lcm.LcmLidarRender;
import ch.ethz.idsc.owly3d.lcm.Mark8LcmRender;
import ch.ethz.idsc.owly3d.lcm.Urg04lxLcmRender;
import ch.ethz.idsc.owly3d.lcm.Vlp16LcmRender;
import ch.ethz.idsc.owly3d.util.AxesHelper;
import ch.ethz.idsc.owly3d.util.IntervalTask;
import ch.ethz.idsc.owly3d.util.Primitives2;
import ch.ethz.idsc.owly3d.util.gfx.CubemapUtils;
import ch.ethz.idsc.owly3d.util.gfx.Programs;
import ch.ethz.idsc.owly3d.util.gfx.ShipProgram;
import ch.ethz.idsc.owly3d.util.usr.JoystickControl;
import ch.ethz.idsc.retina.util.IntervalClock;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Clip;

public class Owly3d extends Workspace {
  // final boolean cameraOverEjcar = false;
  // ---
  Avatar avatar;
  Tracker tracker;
  Rice2Mover rice2Mover = new Rice2MoverDrawable(Tensors.vector(2, 2, -1, 1));
  Se2Car se2Car = new Se2CarDrawable(Tensors.vector(0, 3, 0), RealScalar.of(2));
  EjCar ejCar = new EjCarDrawable();
  Scenario scenario = new Scenario();
  TrajectoryPlanner trajectoryPlanner;
  IntervalTask updateHz = new IntervalTask();
  IntervalClock ic = new IntervalClock();
  JLabel jLabel = new JLabel("test");
  JCheckBox jCheckBox = new JCheckBox("camera on car");
  JTextArea jTextArea = new JTextArea();
  Bulletin bulletin = new Bulletin();

  public Owly3d() {
    updateHz.setRepeated(1_000_000_000L);
  }

  @Override
  protected void loop() {
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
    GL11.glEnableClientState(GL_VERTEX_ARRAY);
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
    final DemoTriangle1 demoTriangle1 = new DemoTriangle1();
    demoTriangle1.init();
    final DemoTriangle2 demoTriangle2 = new DemoTriangle2();
    Example2_17 example2_17 = new Example2_17();
    example2_17.init();
    final DemoPointCloud demoPointCloud = new DemoPointCloud();
    List<LcmLidarRender> pointClouds = new LinkedList<>();
    pointClouds.add(new Hdl32eLcmRender("center"));
    pointClouds.add(new Vlp16LcmRender("center"));
    pointClouds.add(new Mark8LcmRender("center"));
    pointClouds.add(new Urg04lxLcmRender("front"));
    try {
      CubemapUtils.createCubemapTexture("cube/space/space_", true);
      createCubemapProgram();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      new ShipProgram("program/ship.vfs");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    TimeKeeper timeKeeper = new TimeKeeper();
    while (!GLFW.glfwWindowShouldClose(windowObject.window)) {
      final Scalar now = timeKeeper.now();
      // ---
      { // controls
        if (keyboardHander.hit(GLFW.GLFW_KEY_R)) {
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
              Clip.unit().apply(tensor.Get(0).negate()), // brake
              keyboardControl.pressed(GLFW.GLFW_KEY_E), // handbrake
              keyboardControl.pressed(GLFW.GLFW_KEY_W), //
              keyboardControl.pressed(GLFW.GLFW_KEY_Q));
        }
        {
          // JoystickControl.printAxes();
          Tensor axes = JoystickControl.getAxes();
          Scalar mono = Clip.unit().apply(axes.Get(1).negate());
          // TODO based on joystick
          // ejCar.addControl( //
          // axes.Get(0).negate(), // delta
          // Clip.unit().apply(axes.Get(1)), // brake
          // Clip.unit().apply(axes.Get(2)), // handbrake
          // mono, mono); // throttle
          if (false) // <- activate for gamepad joystick
            ejCar.addControl( //
                axes.Get(0).negate(), // delta
                Clip.unit().apply(axes.Get(1)), // brake
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
      if (false) {
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // confirmed
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_PROJECTION); // confirmed
        GL11.glLoadIdentity();
        // TODO glu Ortho2D
        GL11.glRasterPos2i(30, 30);
        // Tensor vec = RandomVariate.of(DiscreteUniformDistribution.of(0, 256 * 255 * 255), 10 * 10);
        ByteBuffer pixels = BufferUtils.createByteBuffer(10000);
        for (int c = 0; c < 10000; ++c)
          pixels.put((byte) 128);
        pixels.flip();
        GL11.glDrawPixels(20, 10, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
      }
      windowObject.setViewportAndPerspective();
      { // camera
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // confirmed
        GL11.glLoadIdentity();
        {
          Tensor mat = scenario.cameraMatrix();
          if (jCheckBox.isSelected()) {
            Tensor car = ejCar.getSE3();
            mat.set(car.Get(0, 3), 0, 3);
            mat.set(car.Get(1, 3).add(RealScalar.of(-0)), 1, 3);
          }
          Tensor matrix = Inverse.of(mat);
          GL11.glMultMatrixd(Primitives2.matrix44(matrix)); // confirmed
        }
      }
      {
        FloatBuffer fb = ByteBuffer //
            .allocateDirect(16 * 4) //
            .order(ByteOrder.nativeOrder()) //
            .asFloatBuffer();
        // FloatBuffer.wrap(new float[16]);
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
        render_scene();
        // demoTriangle1.draw();
        // demoTriangle2.draw();
        // example2_17.draw();
        // demoPointCloud.draw();
        pointClouds.forEach(LcmLidarRender::draw);
        for (LcmLidarRender llr : pointClouds) {
          bulletin.append("" + llr.size());
        }
        // mesh.drawTest();
        // drawCubemap(); // TODO
      }
      // ---
      // p.25
      GLFW.glfwSwapBuffers(windowObject.window); // swap the color buffers
      keyboardHander.reset();
      // Poll for window events. The key callback above will only be
      // invoked during this call.
      GLFW.glfwPollEvents(); // also for joystick...(?)
      if (keyboardHander.hit(GLFW.GLFW_KEY_ESCAPE))
        GLFW.glfwSetWindowShouldClose(windowObject.window, true); // We will detect this in the rendering loop
    }
  }

  private int cubemapProgram;
  private int cubemap_invViewProjUniform;

  private void createCubemapProgram() throws IOException {
    int program = Programs.of("program/cubemap.vfs").program;
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
    double hz = ic.hertz();
    if (updateHz.isReady()) {
      jLabel.setText(String.format("%6.3f Hz", hz));
      jTextArea.setText(bulletin.toString());
    }
    bulletin.clear();
  }

  private static Scalar unit_one(Scalar convert) {
    return convert.add(RealScalar.ONE).multiply(RealScalar.of(0.5));
  }

  public static void main(String[] args) {
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(1700, 100, 250, 500);
    Container container = jFrame.getContentPane();
    container.setLayout(new BorderLayout());
    Owly3d owly3d = new Owly3d();
    {
      JPanel jPanel = new JPanel(new GridLayout(2, 1));
      jPanel.add(owly3d.jLabel);
      jPanel.add(owly3d.jCheckBox);
      container.add(jPanel, BorderLayout.NORTH);
    }
    {
      container.add(owly3d.jTextArea, BorderLayout.CENTER);
    }
    jFrame.setVisible(true);
    owly3d.windowObject.init();
    owly3d.init();
    owly3d.loop();
    jFrame.setVisible(false);
    jFrame.dispose();
    owly3d.windowObject.terminate();
  }
}