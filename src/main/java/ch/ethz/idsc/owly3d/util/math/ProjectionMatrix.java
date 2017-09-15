// code by jph
package ch.ethz.idsc.owly3d.util.math;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.owly3d.util.Primitives3d;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Tan;

public enum ProjectionMatrix {
  ;
  // TODO document from online resource
  public static Tensor perspective(double fovy, double aspect, double zNear, double zFar) {
    return perspective( //
        RealScalar.of(fovy), RealScalar.of(aspect), RealScalar.of(zNear), RealScalar.of(zFar));
  }

  // https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/gluPerspective.xml
  public static Tensor perspective(Scalar fovy, Scalar aspect, Scalar zNear, Scalar zFar) {
    Scalar f = Tan.of(fovy.multiply(RationalScalar.of(1, 2))).reciprocal();
    Tensor matrix = Array.zeros(4, 4);
    matrix.set(f.divide(aspect), 0, 0);
    matrix.set(f, 1, 1);
    matrix.set(zNear.add(zFar).divide(zNear.subtract(zFar)), 2, 2);
    matrix.set(zNear.multiply(zFar).multiply(RealScalar.of(2)).divide(zNear.subtract(zFar)), 2, 3);
    matrix.set(RealScalar.ONE.negate(), 3, 2);
    return matrix;
  }

  public static void print() {
    double[] values = new double[16];
    GL11.glGetDoublev(GL11.GL_PROJECTION_MATRIX, values);
    Tensor matrix = Primitives3d.matrix44(values);
    System.out.println("GL_PROJECTION_MATRIX=");
    System.out.println(Pretty.of(matrix));
  }

  /** ---
   * 4.253E+0 0.000E+0 0.000E+0 0.000E+0
   * 0.000E+0 5.671E+0 0.000E+0 0.000E+0
   * 0.000E+0 0.000E+0 -1.000E+0 -2.000E-1
   * 0.000E+0 0.000E+0 -1.000E+0 0.000E+0 */
  /** ---
   * [
   * [ 1.83 0.00 0.00 0.00 ]
   * [ 0.00 1.83 0.00 0.00 ]
   * [ 0.00 0.00 -1.02 -2.02 ]
   * [ 0.00 0.00 -1.00 0.00 ]
   * ] */
  public static void main(String[] args) {
    System.out.println(Pretty.of(perspective(1, 1, 1, 100).map(Round._2)));
  }
}
