// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.velodyne.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.velodyne.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16RayDecoder;
import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

public class Vlp16LcmRender implements LidarRayBlockListener {
  private final LidarPointCloud laserPointCloud;

  public Vlp16LcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud();
    VelodyneLcmClient client = VelodyneLcmClient.vlp16(lidarId);
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    Vlp16SpacialProvider vlp16SpacialProvider = new Vlp16SpacialProvider();
    vlp16SpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    ((Vlp16RayDecoder) client.rayDecoder).addListener(vlp16SpacialProvider);
    ((Vlp16RayDecoder) client.rayDecoder).addListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    client.startSubscriptions();
  }

  @Override
  public void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    laserPointCloud.fill(floatBuffer, byteBuffer);
  }

  public void draw() {
    laserPointCloud.draw();
  }
}
