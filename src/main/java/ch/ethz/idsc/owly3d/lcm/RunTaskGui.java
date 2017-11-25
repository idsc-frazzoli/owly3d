// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.sys.LoggerModule;
import ch.ethz.idsc.retina.sys.SpyModule;
import ch.ethz.idsc.retina.sys.TaskGui;

enum RunTaskGui {
  ;
  public static void main(String[] args) {
    List<Class<?>> modules = new ArrayList<>();
    modules.add(SpyModule.class);
    modules.add(LoggerModule.class);
    // ---
    // modules.add(Hdl32eLcmServerModule.class);
    // modules.add(Mark8LcmServerModule.class);
    // modules.add(Urg04lxLcmServerModule.class);
    // ---
    modules.add(GenericXboxPadLcmServerModule.class);
    // modules.add(LogitechGamepadLcmServerModule.class);
    new TaskGui(modules);
  }
}
