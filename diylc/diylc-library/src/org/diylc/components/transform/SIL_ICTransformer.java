package org.diylc.components.transform;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import org.diylc.common.IComponentTransformer;
import org.diylc.common.Orientation;
import org.diylc.components.semiconductors.SIL_IC;
import org.diylc.core.IDIYComponent;

public class SIL_ICTransformer implements IComponentTransformer {

  @Override
  public boolean canRotate(IDIYComponent<?> component) {
    return component.getClass().equals(SIL_IC.class);
  }

  @Override
  public boolean canMirror(IDIYComponent<?> component) {
    return component.getClass().equals(SIL_IC.class);
  }

  @Override
  public boolean mirroringChangesCircuit() {
    return false;
  }

  @Override
  public void rotate(IDIYComponent<?> component, Point center, int direction) {
    AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2 * direction, center.x, center.y);
    for (int index = 0; index < component.getControlPointCount(); index++) {
      Point p = new Point(component.getControlPoint(index));
      rotate.transform(p, p);
      component.setControlPoint(p, index);
    }

    SIL_IC ic = (SIL_IC) component;
    Orientation o = ic.getOrientation();
    int oValue = o.ordinal();
    oValue += direction;
    if (oValue < 0)
      oValue = Orientation.values().length - 1;
    if (oValue >= Orientation.values().length)
      oValue = 0;
    o = Orientation.values()[oValue];
    ic.setOrientation(o);
  }

  @SuppressWarnings("incomplete-switch")
  @Override
  public void mirror(IDIYComponent<?> component, Point center, int direction) {
    SIL_IC ic = (SIL_IC) component;
    int dx = center.x - ic.getControlPoint(0).x;
    int dy = center.y - ic.getControlPoint(0).y;
    if (direction == IComponentTransformer.HORIZONTAL) {
      Orientation o = ic.getOrientation();
      switch (o) {
        case _90:
          o = Orientation._270;
          break;
        case _270:
          o = Orientation._90;
      }

      for (int i = 0; i < ic.getControlPointCount(); i++) {
        Point p = ic.getControlPoint(i);
        ic.setControlPoint(new Point(p.x + 2 * dx, p.y), i);
      }

      ic.setOrientation(o);
    } else {
      Orientation o = ic.getOrientation();
      switch (o) {
        case DEFAULT:
          o = Orientation._180;
          break;
        case _180:
          o = Orientation.DEFAULT;
      }

      for (int i = 0; i < ic.getControlPointCount(); i++) {
        Point p = ic.getControlPoint(i);
        ic.setControlPoint(new Point(p.x, p.y + 2 * dy), i);
      }

      ic.setOrientation(o);
    }
  }
}
