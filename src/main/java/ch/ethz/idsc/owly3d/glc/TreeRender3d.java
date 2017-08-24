// code by jph
package ch.ethz.idsc.owly3d.glc;

import java.awt.Color;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly.data.tree.StateCostNode;
import ch.ethz.idsc.owly3d.util.Render3dUtils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.Hue;

public class TreeRender3d {
  private final Collection<? extends StateCostNode> collection;

  public TreeRender3d(Collection<? extends StateCostNode> collection) {
    this.collection = collection;
  }

  public void render() {
    DoubleSummaryStatistics dss = collection.stream() //
        .map(StateCostNode::costFromRoot) //
        .map(Scalar::number) //
        .mapToDouble(Number::doubleValue) //
        .filter(Double::isFinite) //
        .summaryStatistics();
    final double min = dss.getMin();
    final double max = dss.getMax();
    GL11.glLineWidth(1);
    // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    // GL11.glEnable(GL11.GL_BLEND);
    GL11.glBegin(GL11.GL_LINES);
    GL11.glColor3d(.8, .5, .9);
    for (StateCostNode node : collection) {
      StateCostNode parent = node.parent();
      if (parent != null) {
        double val = node.costFromRoot().number().doubleValue();
        final double interp = (val - min) / (max - min);
        Color color = Hue.of(interp, 1, 1, .2);
        GL11.glColor4f(color.getRed() / 255.f, color.getGreen() / 255.f, color.getBlue() / 255.f, color.getAlpha() / 255.f);
        Render3dUtils.vertex(node.state());
        Render3dUtils.vertex(parent.state());
      }
    }
    GL11.glEnd();
  }
}
