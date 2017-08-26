// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.velodyne.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.velodyne.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eRayDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32eSpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

public class Hdl32eLcmRender implements LidarRayBlockListener {
  private final LidarPointCloud laserPointCloud;

  public Hdl32eLcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud();
    VelodyneLcmClient client = VelodyneLcmClient.hdl32e(lidarId);
    LidarAngularFiringCollector hdl32eAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(hdl32eAngularFiringCollector);
    LidarRotationProvider hdl32eRotationProvider = new LidarRotationProvider();
    hdl32eRotationProvider.addListener(hdl32eAngularFiringCollector);
    ((Hdl32eRayDecoder) client.rayDecoder).addListener(hdl32eSpacialProvider);
    ((Hdl32eRayDecoder) client.rayDecoder).addListener(hdl32eRotationProvider);
    hdl32eAngularFiringCollector.addListener(this);
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
