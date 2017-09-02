// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8SpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmClient;

public class Mark8LcmRender implements LcmLidarRender {
  public static final int MAX_COORDINATES = 35000 * 32;
  // ---
  private final LidarPointCloud laserPointCloud;

  public Mark8LcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud(MAX_COORDINATES);
    laserPointCloud.translate[2] = 1.2;
    Mark8Decoder mark8Decoder = new Mark8Decoder();
    Mark8LcmClient mark8LcmClient = new Mark8LcmClient(mark8Decoder, lidarId);
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[MAX_COORDINATES * 3]); // 3 because of x y z
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[MAX_COORDINATES]);
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(floatBuffer, byteBuffer);
    LidarSpacialProvider lidarSpacialProvider = new Mark8SpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    mark8Decoder.addRayListener(lidarSpacialProvider);
    mark8Decoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    mark8LcmClient.startSubscriptions();
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
