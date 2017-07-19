// code by jph
package ch.ethz.idsc.owly3d;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ch.ethz.idsc.retina.dev.hdl32e.HDL32EFiringCollector;
import ch.ethz.idsc.retina.dev.hdl32e.HDL32EPacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.LaserPositionConsumer;
import ch.ethz.idsc.retina.util.io.PacketConsumer;

public class LiveLaser implements Runnable {
  final LaserPositionConsumer laserPositionConsumer;
  final Thread thread;

  public LiveLaser(LaserPositionConsumer laserPositionConsumer) {
    this.laserPositionConsumer = laserPositionConsumer;
    thread = new Thread(this);
  }

  boolean isLaunched;

  @Override
  public void run() {
    isLaunched = true;
    System.out.println("live laser");
    try (DatagramSocket datagramSocket = new DatagramSocket(2368)) {
      byte[] packet_data = new byte[4096];
      DatagramPacket datagramPacket = new DatagramPacket(packet_data, packet_data.length);
      PacketConsumer packetConsumer = new HDL32EPacketConsumer( //
          new HDL32EFiringCollector(laserPositionConsumer));
      while (isLaunched) {
        datagramSocket.receive(datagramPacket);
        packetConsumer.parse(packet_data, datagramPacket.getLength());
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
