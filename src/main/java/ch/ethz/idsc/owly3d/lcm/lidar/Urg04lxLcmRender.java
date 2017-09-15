// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxSpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;

public class Urg04lxLcmRender implements LcmLidarRender {
  public static final int MAX_COORDINATES = 1000;
  // ---
  private final LidarPointCloud laserPointCloud;

  public Urg04lxLcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud(MAX_COORDINATES);
    laserPointCloud.color[0] = 1f;
    laserPointCloud.color[2] = 0;
    laserPointCloud.translate[0] = 1.55;
    laserPointCloud.translate[2] = 0.2; // 20 cm above gnd
    Urg04lxDecoder urg04lxDecoder = new Urg04lxDecoder();
    Urg04lxLcmClient urg04lxLcmClient = new Urg04lxLcmClient(urg04lxDecoder, lidarId);
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[MAX_COORDINATES * 3]); // 3 because of x y z
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[MAX_COORDINATES]);
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(floatBuffer, byteBuffer);
    LidarSpacialProvider lidarSpacialProvider = new Urg04lxSpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    urg04lxDecoder.addRayListener(lidarSpacialProvider);
    urg04lxDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    urg04lxLcmClient.startSubscriptions();
  }

  @Override
  public void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    laserPointCloud.fill(floatBuffer, byteBuffer);
  }

  @Override
  public void draw() {
    laserPointCloud.draw();
  }

  @Override
  public int size() {
    return laserPointCloud.size();
  }
}
