// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32eSpacialProvider;

public class Hdl32eLcmRender implements LcmLidarRender {
  private static final int MAX_POINTS = 2304 * 32;
  private final LidarPointCloud laserPointCloud;

  public Hdl32eLcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud(MAX_POINTS);
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    VelodyneDecoder velodyneDecoder = new Hdl32eDecoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(MAX_POINTS, 3);
    LidarSpacialProvider lidarSpacialProvider = new Hdl32eSpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider hdl32eRotationProvider = new LidarRotationProvider();
    hdl32eRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider); // TODO check if this is the right ordering of adding listeners
    velodyneDecoder.addRayListener(hdl32eRotationProvider); //
    lidarAngularFiringCollector.addListener(this);
    velodyneLcmClient.startSubscriptions();
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    ByteBuffer byteBuffer = lidarRayBlockEvent.byteBuffer;
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
