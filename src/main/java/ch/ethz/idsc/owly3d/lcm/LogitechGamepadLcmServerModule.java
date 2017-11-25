// code by jph
package ch.ethz.idsc.owly3d.lcm;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;

public class LogitechGamepadLcmServerModule extends AbstractJoystickLcmServerModule {
  @Override
  public JoystickType getJoystickType() {
    return null; // JoystickType.LOGITECH_GAMEPAD_F710; // FIXME
  }
}
