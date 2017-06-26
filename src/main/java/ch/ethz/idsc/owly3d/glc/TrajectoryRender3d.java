// code by jph
package ch.ethz.idsc.owly3d.glc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly.glc.core.GlcNode;
import ch.ethz.idsc.owly.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owly.glc.core.TrajectorySample;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.owly3d.util.Render3dUtils;

public class TrajectoryRender3d {
  private final TrajectoryPlanner trajectoryPlanner;

  public TrajectoryRender3d(TrajectoryPlanner trajectoryPlanner) {
    this.trajectoryPlanner = trajectoryPlanner;
  }

  public void render() {
    { // draw detailed trajectory from root to goal
      Optional<GlcNode> optional = trajectoryPlanner.getBestOrElsePeek();
      if (optional.isPresent()) {
        GlcNode node = optional.get();
        final List<TrajectorySample> list = trajectoryPlanner.detailedTrajectoryTo(node);
        // { // draw control vectors u along trajectory
        // int rgb = 64;
        // graphics.setColor(new Color(rgb, rgb, rgb, 192));
        // for (TrajectorySample trajectorySample : list)
        // if (trajectorySample.hasU()) {
        // Tensor u = trajectorySample.getU().copy();
        // while (u.length() < 2)
        // u.append(RealScalar.ZERO);
        // graphics.draw( //
        // owlyLayer.toVector( //
        // trajectorySample.stateTime().x(), //
        // u.multiply(U_SCALE) //
        // ));
        // }
        // }
        { // draw trajectory as thick green line with white background
          List<StateTime> LIST = list.stream().map(TrajectorySample::stateTime).collect(Collectors.toList());
          GL11.glLineWidth(2);
          GL11.glBegin(GL11.GL_LINE_STRIP);
          GL11.glColor3f(0, 1, 0);
          for (StateTime stateTime : LIST) {
            Render3dUtils.vertex(stateTime.x());
          }
          GL11.glEnd();
          // Path2D path2d = owlyLayer.toPath2D(LIST);
          // graphics.setStroke(new BasicStroke(5.0f));
          // graphics.setColor(new Color(255, 255, 255, 128));
          // graphics.draw(path2d);
          // graphics.setStroke(new BasicStroke(2.0f));
          // graphics.setColor(new Color(0, 192, 0, 192));
          // graphics.draw(path2d);
          // graphics.setStroke(new BasicStroke());
        }
      }
    }
  }
}
