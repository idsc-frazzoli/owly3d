// code by jph
package ch.ethz.idsc.owly3d;

import java.io.File;

import ch.ethz.idsc.retina.dev.velodyne.HDL32EFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.HDL32EPacketConsumer;
import ch.ethz.idsc.retina.dev.velodyne.LaserPositionConsumer;
import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

public class LaserPcalPlayback implements Runnable {
  final LaserPositionConsumer laserPositionConsumer;
  final Thread thread;

  public LaserPcalPlayback(LaserPositionConsumer laserPositionConsumer) {
    this.laserPositionConsumer = laserPositionConsumer;
    thread = new Thread(this);
  }

  @Override
  public void run() {
    PacketConsumer packetConsumer = new HDL32EPacketConsumer( //
        new HDL32EFiringCollector(laserPositionConsumer));
    try {
      new PcapParse(
          new File( //
              "/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
              "HDL32-V2_R into Butterfield into Digital Drive.pcap"
//              "HDL32-V2_Tunnel.pcap"
              ), //
          packetConsumer);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
