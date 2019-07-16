// code by jph
package ch.ethz.idsc.owly3d.lcm;

import ch.ethz.idsc.retina.joystick.JoystickType;

public class GenericXboxPadLcmServerModule extends AbstractJoystickLcmServerModule {
  @Override
  public JoystickType getJoystickType() {
    return JoystickType.GENERIC_XBOX_PAD;
  }

  public static void main(String[] args) throws Exception {
    new GenericXboxPadLcmServerModule().first();
  }
}
