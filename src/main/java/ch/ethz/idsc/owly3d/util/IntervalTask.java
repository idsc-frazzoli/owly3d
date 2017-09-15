// code by jph
package ch.ethz.idsc.owly3d.util;

public class IntervalTask {
  public static enum Mode {
    DISABLED, //
    PERIODIC, //
    REPEATED, //
    ONLYONCE, //
    ;
  }

  public Mode mode = Mode.DISABLED;
  public long from_clock;
  public long delay;

  /** @param nanoPeriod in nano seconds */
  public void setRepeated(long nanoPeriod) {
    mode = Mode.REPEATED;
    delay = nanoPeriod;
  }

  public boolean isReady() {
    final long now = now();
    if (mode.equals(Mode.DISABLED))
      from_clock = now;
    else {
      long delta = now - from_clock;
      if (delay <= delta) {
        switch (mode) {
        case ONLYONCE:
          from_clock += delay;
          mode = Mode.DISABLED;
          break;
        case PERIODIC:
          from_clock += delay;
          break;
        case REPEATED:
          from_clock = now + delay;
          break;
        default:
          break;
        }
        return true;
      }
    }
    return false;
  }

  private static long now() {
    return System.nanoTime();
  }
}
