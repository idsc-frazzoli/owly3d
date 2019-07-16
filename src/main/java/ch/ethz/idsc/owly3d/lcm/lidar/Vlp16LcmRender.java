// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SpacialProvider;

public class Vlp16LcmRender implements LcmLidarRender {
  private final LidarPointCloud laserPointCloud;

  public Vlp16LcmRender(String lidarId) {
    final int max_points = 2304 * 32;
    laserPointCloud = new LidarPointCloud(max_points);
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(max_points, 3);
    double angle_offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SpacialProvider(angle_offset);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    velodyneLcmClient.startSubscriptions();
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
