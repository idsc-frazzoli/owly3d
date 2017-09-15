// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eSpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

public class Hdl32eLcmRender implements LcmLidarRender {
  private final LidarPointCloud laserPointCloud;

  public Hdl32eLcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud(2304 * 32);
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    VelodyneDecoder velodyneDecoder = new Hdl32eDecoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    LidarSpacialProvider lidarSpacialProvider = new Hdl32eSpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider hdl32eRotationProvider = new LidarRotationProvider();
    hdl32eRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(hdl32eRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    velodyneLcmClient.startSubscriptions();
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
