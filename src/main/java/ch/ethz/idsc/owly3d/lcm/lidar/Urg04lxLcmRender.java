// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxSpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;

public class Urg04lxLcmRender implements LcmLidarRender {
  private final LidarPointCloud laserPointCloud;

  public Urg04lxLcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud(Urg04lxDevice.MAX_POINTS);
    laserPointCloud.color[0] = 1f;
    laserPointCloud.color[2] = 0;
    laserPointCloud.translate[0] = 1.55;
    laserPointCloud.translate[2] = 0.2; // 20 cm above gnd
    Urg04lxDecoder urg04lxDecoder = new Urg04lxDecoder();
    Urg04lxLcmClient urg04lxLcmClient = new Urg04lxLcmClient(urg04lxDecoder, lidarId);
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(Urg04lxDevice.MAX_POINTS, 3);
    LidarSpacialProvider lidarSpacialProvider = new Urg04lxSpacialProvider(3);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    urg04lxDecoder.addRayListener(lidarSpacialProvider);
    urg04lxDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    urg04lxLcmClient.startSubscriptions();
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    laserPointCloud.fill(lidarRayBlockEvent.floatBuffer, lidarRayBlockEvent.byteBuffer);
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
