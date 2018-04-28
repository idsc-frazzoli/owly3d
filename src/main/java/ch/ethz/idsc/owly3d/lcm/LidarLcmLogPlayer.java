// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.io.IOException;

import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

enum LidarLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig cfg = new LogPlayerConfig();
    cfg.logFile = "/media/datahaki/media/ethz/lcmlog/20171107T151536_9639c7f9.lcm.00_test_labday";
    cfg.speed_numerator = 1;
    cfg.speed_denominator = 8;
    LogPlayer.create(cfg);
  }
}
