// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.util.Objects;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class GenericXboxPadLcmServerModule extends AbstractModule {
  /** refresh period in [ms] for joystick events */
  public static final int PERIOD_MS = 20;
  // ---
  private JoystickLcmServer joystickLcmServer;

  @Override
  protected void first() throws Exception {
    joystickLcmServer = new JoystickLcmServer(JoystickType.GENERIC_XBOX_PAD, PERIOD_MS);
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
