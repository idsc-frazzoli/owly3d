// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.util.Objects;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class GenericXboxPadLcmServerModule extends AbstractModule {
  public static final int PERIOD = 50;
  // ---
  private JoystickLcmServer joystickLcmServer;

  @Override
  protected void first() throws Exception {
    joystickLcmServer = new JoystickLcmServer(JoystickType.GENERIC_XBOX_PAD, PERIOD);
    joystickLcmServer.start();
  }

  @Override
  protected void last() {
    if (Objects.nonNull(joystickLcmServer)) {
      joystickLcmServer.stop();
      joystickLcmServer = null;
    }
  }
}
