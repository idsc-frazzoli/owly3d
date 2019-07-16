// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;

public interface LcmLidarRender extends LidarRayBlockListener {
  void draw();

  int size();
}
