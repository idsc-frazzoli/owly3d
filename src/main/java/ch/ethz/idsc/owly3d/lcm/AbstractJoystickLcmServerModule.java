package ch.ethz.idsc.owly3d.lcm;

import java.util.Objects;

import ch.ethz.idsc.retina.joystick.JoystickType;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public abstract class AbstractJoystickLcmServerModule extends AbstractModule {
  /** refresh period in [ms] for joystick events */
  public static final int PERIOD_MS = 20;
  // ---
  private JoystickLcmServer joystickLcmServer;

  @Override
  protected final void first() {
    joystickLcmServer = new JoystickLcmServer(getJoystickType(), PERIOD_MS);
    joystickLcmServer.start();
  }

  @Override
  protected final void last() {
    if (Objects.nonNull(joystickLcmServer)) {
      joystickLcmServer.stop();
      joystickLcmServer = null;
    }
  }

  public abstract JoystickType getJoystickType();
}
