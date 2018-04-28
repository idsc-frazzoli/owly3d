// code adapted from hello world example from lwjgl
// code by jph
package ch.ethz.idsc.owly3d;

import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.data.TimeKeeper;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owly3d.ani.obj.Avatar;
import ch.ethz.idsc.owly3d.lcm.lidar.Hdl32eLcmRender;
import ch.ethz.idsc.owly3d.lcm.lidar.LcmLidarRender;
import ch.ethz.idsc.owly3d.lcm.lidar.Mark8LcmRender;
import ch.ethz.idsc.owly3d.lcm.lidar.Urg04lxLcmRender;
import ch.ethz.idsc.owly3d.lcm.lidar.Vlp16LcmRender;
import ch.ethz.idsc.owly3d.util.AxesHelper;
import ch.ethz.idsc.owly3d.util.IntervalTask;
import ch.ethz.idsc.owly3d.util.Primitives3d;
import ch.ethz.idsc.retina.util.IntervalClock;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class LidarView extends Workspace {
  Avatar avatar;
  Scenario scenario = new Scenario();
  IntervalTask updateHz = new IntervalTask();
  IntervalClock ic = new IntervalClock();
  JLabel jLabel = new JLabel("test");
  JTextArea jTextArea = new JTextArea();
  Bulletin bulletin = new Bulletin();

  public LidarView() {
    updateHz.setRepeated(200_000_000L);
  }

  @Override
  protected void loop() {
    GL.createCapabilities();
    {
      GL11.glClearDepth(1);
      float gray = 0.2f;
      GL11.glClearColor(gray, gray, gray, 0);
    }
    avatar = new Avatar(Tensors.vector(0, 0, 15));
    scenario.add(avatar);
    GL11.glEnableClientState(GL_VERTEX_ARRAY);
    // ---
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_BLEND);
    // ---
    GL11.glDepthFunc(GL11.GL_LESS); // confirmed
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    // ---
    GL11.glDrawBuffer(GL11.GL_BACK); // confirmed
    { // p.62
      GL11.glFrontFace(GL11.GL_CCW);
      GL11.glCullFace(GL11.GL_BACK);
      GL11.glEnable(GL11.GL_CULL_FACE);
    }
    Texture.bind();
    List<LcmLidarRender> pointClouds = new LinkedList<>();
    pointClouds.add(new Hdl32eLcmRender("center"));
    pointClouds.add(new Vlp16LcmRender("center"));
    pointClouds.add(new Mark8LcmRender("center"));
    pointClouds.add(new Urg04lxLcmRender("front"));
    GokartPoseLcmLidar gokartPoseLcmLidar = new GokartPoseLcmLidar();
    gokartPoseLcmLidar.gokartPoseLcmClient.startSubscriptions();
    TimeKeeper timeKeeper = new TimeKeeper();
    while (!GLFW.glfwWindowShouldClose(windowObject.window)) {
      final Scalar now = timeKeeper.now();
      // ---
      {
        avatar.addPush(keyboardControl.getPushKP());
        Tensor drag1 = keyboardControl.getDrag().multiply(RealScalar.of(.001));
        Tensor drag2 = mouseControl.getDrag().multiply(RealScalar.of(.003));
        avatar.addRotate(drag1.add(drag2));
      }
      scenario.integrate(now);
      // ---
      // GL11.GL_ACCUM_BUFFER_BIT
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); //
      GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL); // confirmed
      // ---
      windowObject.setViewportAndPerspective();
      { // camera
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // confirmed
        GL11.glLoadIdentity();
        Tensor matrix = Inverse.of(scenario.cameraMatrix());
        GL11.glMultMatrixd(Primitives3d.matrix44(matrix)); // confirmed
      }
      {
        render_scene();
        GL11.glPushMatrix();
        Tensor pose_xya = gokartPoseLcmLidar.getPose().copy();
        pose_xya.set(s -> ((Quantity) s).value(), 0);
        pose_xya.set(s -> ((Quantity) s).value(), 1);
        Tensor se2_go = Se2Utils.toSE2Matrix(pose_xya);
        Tensor se2_se = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16);
        Tensor se2mat = se2_go.dot(se2_se);
        Tensor matrix = IdentityMatrix.of(4);
        matrix.set(se2mat.Get(0, 2), 0, 3);
        matrix.set(se2mat.Get(1, 2), 1, 3);
        matrix.set(se2mat.Get(0, 0), 0, 0);
        matrix.set(se2mat.Get(0, 1), 0, 1);
        matrix.set(se2mat.Get(1, 0), 1, 0);
        matrix.set(se2mat.Get(1, 1), 1, 1);
        bulletin.append(Pretty.of(matrix.map(Round._3)));
        // GL11.glMultMatrixd(Primitives3d.matrix44(matrix)); // confirmed
        pointClouds.forEach(LcmLidarRender::draw);
        GL11.glPopMatrix();
        for (LcmLidarRender llr : pointClouds)
          bulletin.append("" + llr.size());
      }
      // ---
      GLFW.glfwSwapBuffers(windowObject.window); // swap the color buffers
      keyboardHander.reset();
      GLFW.glfwPollEvents(); // also for joystick...(?)
      if (keyboardHander.hit(GLFW.GLFW_KEY_ESCAPE))
        GLFW.glfwSetWindowShouldClose(windowObject.window, true); // We will detect this in the rendering loop
    }
    gokartPoseLcmLidar.gokartPoseLcmClient.stopSubscriptions();
  }

  private void render_scene() {
    AxesHelper.draw();
    scenario.draw();
    scenario.resetControl();
    double hz = ic.hertz();
    if (updateHz.isReady()) {
      jLabel.setText(String.format("%6.3f Hz", hz));
      jTextArea.setText(bulletin.toString());
    }
    bulletin.clear();
  }

  public static void main(String[] args) {
    // TODO extract to different file!
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(1700, 100, 250, 500);
    Container container = jFrame.getContentPane();
    container.setLayout(new BorderLayout());
    LidarView owly3d = new LidarView();
    {
      JPanel jPanel = new JPanel(new GridLayout(2, 1));
      jPanel.add(owly3d.jLabel);
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