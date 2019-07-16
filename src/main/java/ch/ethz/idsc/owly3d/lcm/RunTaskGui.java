// code by jph
package ch.ethz.idsc.owly3d.lcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ch.ethz.idsc.gokart.lcm.LoggerModule;
import ch.ethz.idsc.gokart.lcm.SpyModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.TabbedTaskGui;

enum RunTaskGui {
  ;
  public static void main(String[] args) {
    List<Class<? extends AbstractModule>> modules = new ArrayList<>();
    modules.add(SpyModule.class);
    modules.add(LoggerModule.class);
    // ---
    // modules.add(Hdl32eLcmServerModule.class);
    // modules.add(Mark8LcmServerModule.class);
    // modules.add(Urg04lxLcmServerModule.class);
    // ---
    modules.add(GenericXboxPadLcmServerModule.class);
    // modules.add(LogitechGamepadLcmServerModule.class);
    TabbedTaskGui tabbedTaskGui = new TabbedTaskGui(new Properties());
    tabbedTaskGui.tab("asd", modules);
  }
}
