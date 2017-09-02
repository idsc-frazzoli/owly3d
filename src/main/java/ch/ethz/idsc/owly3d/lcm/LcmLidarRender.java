// code by jph
package ch.ethz.idsc.owly3d.lcm;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;

public interface LcmLidarRender extends LidarRayBlockListener {
  void draw();

  int size();
}
