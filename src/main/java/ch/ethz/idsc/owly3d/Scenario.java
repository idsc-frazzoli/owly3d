// code by jph
package ch.ethz.idsc.owly3d;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import ch.ethz.idsc.owly3d.ani.Animated;
import ch.ethz.idsc.owly3d.ani.Drawable;
import ch.ethz.idsc.owly3d.ani.obj.Avatar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class Scenario {
  private final Collection<Animated> set = new LinkedHashSet<>();
  private final Map<Animated, Drawable> map = new LinkedHashMap<>();

  public void integrate(Scalar now) {
    set.stream().forEach(a -> a.integrate(now));
  }

  public void add(Animated animated) {
    set.add(animated);
    if (animated instanceof Drawable)
      map.put(animated, (Drawable) animated);
  }

  public void draw() {
    map.values().stream().forEach(Drawable::draw);
  }

  public void resetControl() {
    set.stream().forEach(Animated::resetControl);
  }

  public Tensor cameraMatrix() {
    // TODO not final implementation
    Avatar camera = (Avatar) set.stream().filter(animated -> animated instanceof Avatar).findFirst().get();
    return camera.getSE3();
  }
}
