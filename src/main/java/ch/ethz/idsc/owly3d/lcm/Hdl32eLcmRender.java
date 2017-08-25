// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.owly3d.demo.LaserPointCloud;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRayBlockListener;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eAngularFiringCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eRotationProvider;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32eSpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Hdl32eLcmClient;

public class Hdl32eLcmRender implements Hdl32eRayBlockListener {
  private final LaserPointCloud laserPointCloud;

  public Hdl32eLcmRender(String lidarId) {
    laserPointCloud = new LaserPointCloud();
    Hdl32eLcmClient client = new Hdl32eLcmClient(lidarId);
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[2310 * 32 * 3]);
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[2310 * 32]);
    Hdl32eAngularFiringCollector hdl32eAngularFiringCollector = //
        new Hdl32eAngularFiringCollector(floatBuffer, byteBuffer);
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(hdl32eAngularFiringCollector);
    Hdl32eRotationProvider hdl32eRotationProvider = new Hdl32eRotationProvider();
    hdl32eRotationProvider.addListener(hdl32eAngularFiringCollector);
    client.hdl32eRayDecoder.addListener(hdl32eSpacialProvider);
    client.hdl32eRayDecoder.addListener(hdl32eRotationProvider);
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
