// code by jph
package ch.ethz.idsc.owly3d.util.draw;

import org.lwjgl.opengl.GL11;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;

public enum Cylinder {
  ;
  public static void drawY0(double rad, int slices, int swp, Tensor mas, final double y) {
    double fac = Math.PI * 2 / slices;
    double lam = 0;
    double ang;
    Tensor col = Tensors.vector(1, 1, 1, mas.Get(3).number().doubleValue());
    GL11.glBegin(GL11.GL_TRIANGLE_FAN);
    GL11.glColor4dv(Primitives.toDoubleArray(col));
    // glNormal3d(0,0,math::Sign(rad));
    GL11.glVertex3d(0, y, 0);
    for (int c0 = 0; c0 <= slices; ++c0) {
      if (c0 % swp == 0)
        lam = 1 - lam;
      ang = (c0 + 0.5) * fac;
      GL11.glColor4dv(Primitives.toDoubleArray( //
          col.multiply(RealScalar.of(lam)).add(mas.multiply(RealScalar.of(1 - lam)))));
      GL11.glVertex3d(rad * Math.cos(ang), y, rad * Math.sin(ang));
    }
    GL11.glEnd();
  }

  public static void drawY1(double rad, int slices, int swp, Tensor mas, final double y) {
    double fac = Math.PI * 2 / slices;
    double lam = 0;
    double ang;
    Tensor col = Tensors.vector(1, 1, 1, mas.Get(3).number().doubleValue());
    GL11.glBegin(GL11.GL_TRIANGLE_FAN);
    GL11.glColor4dv(Primitives.toDoubleArray(col));
    // glNormal3d(0,0,math::Sign(rad));
    GL11.glVertex3d(0, y, 0);
    for (int c0 = 0; c0 <= slices; ++c0) {
      if (c0 % swp == 0)
        lam = 1 - lam;
      ang = -(c0 + 0.5) * fac; // FIXME
      GL11.glColor4dv(Primitives.toDoubleArray( //
          col.multiply(RealScalar.of(lam)).add(mas.multiply(RealScalar.of(1 - lam)))));
      GL11.glVertex3d(rad * Math.cos(ang), y, rad * Math.sin(ang));
    }
    GL11.glEnd();
  }

  public static void drawY(double gnd, double hgt, double rad, int slices, int swp, Tensor mas) {
    if (gnd > hgt)
      throw new RuntimeException();
    double fac = Math.PI * 2 / slices;
    double lam = 0;
    double cc0;
    double sc0;
    double ang;
    Tensor col = Tensors.vector(1, 1, 1, mas.Get(3).number().doubleValue());
    {
      GL11.glBegin(GL11.GL_QUAD_STRIP);
      for (int c0 = 0; c0 <= slices; ++c0) {
        if (c0 % swp == 0)
          lam = 1 - lam;
        ang = (c0 + 0.5) * fac;
        cc0 = Math.cos(ang);
        sc0 = Math.sin(ang);
        // glNormal3d(cc0=cos(ang),sc0=sin(ang),0);
        GL11.glColor4dv(Primitives.toDoubleArray( //
            col.multiply(RealScalar.of(lam)).add(mas.multiply(RealScalar.of(1 - lam)))));
        GL11.glVertex3d(rad * cc0, gnd, rad * sc0);
        GL11.glVertex3d(rad * cc0, hgt, rad * sc0);
      }
      GL11.glEnd();
    }
    {
      drawY0(rad, slices, swp, mas, gnd);
      drawY1(rad, slices, swp, mas, hgt);
    }
  }

  public static void drawZ(double rad, int slices, int swp, Tensor mas) {
    double fac = Math.PI * 2 / slices;
    double lam = 0;
    double ang;
    Tensor col = Tensors.vector(1, 1, 1, mas.Get(3).number().doubleValue());
    GL11.glBegin(GL11.GL_TRIANGLE_FAN);
    GL11.glColor4dv(Primitives.toDoubleArray(col));
    // glNormal3d(0,0,math::Sign(rad));
    GL11.glVertex3d(0, 0, 0);
    for (int c0 = 0; c0 <= slices; ++c0) {
      if (c0 % swp == 0)
        lam = 1 - lam;
      ang = (c0 + 0.5) * fac;
      GL11.glColor4dv(Primitives.toDoubleArray( //
          col.multiply(RealScalar.of(lam)).add(mas.multiply(RealScalar.of(1 - lam)))));
      GL11.glVertex3d(rad * Math.cos(ang), rad * Math.sin(ang), 0);
    }
    GL11.glEnd();
  }

  public static void drawZ(double gnd, double hgt, double rad, int slices, int swp, Tensor mas) {
    double fac = Math.PI * 2 / slices;
    double lam = 0;
    double cc0;
    double sc0;
    double ang;
    Tensor col = Tensors.vector(1, 1, 1, mas.Get(3).number().doubleValue());
    GL11.glBegin(GL11.GL_QUAD_STRIP);
    for (int c0 = 0; c0 <= slices; ++c0) {
      if (c0 % swp == 0)
        lam = 1 - lam;
      ang = (c0 + 0.5) * fac;
      cc0 = Math.cos(ang);
      sc0 = Math.sin(ang);
      // glNormal3d(cc0=cos(ang),sc0=sin(ang),0);
      GL11.glColor4dv(Primitives.toDoubleArray( //
          col.multiply(RealScalar.of(lam)).add(mas.multiply(RealScalar.of(1 - lam)))));
      GL11.glVertex3d(rad * cc0, rad * sc0, hgt);
      GL11.glVertex3d(rad * cc0, rad * sc0, gnd);
    }
    GL11.glEnd();
    GL11.glPushMatrix();
    GL11.glTranslated(0, 0, hgt);
    drawZ(rad, slices, swp, mas);
    GL11.glPopMatrix();
  }
}
