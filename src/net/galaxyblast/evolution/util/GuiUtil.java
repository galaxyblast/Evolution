package net.galaxyblast.evolution.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class GuiUtil
{
	public static Polygon getRotatedPolygon(Polygon p, double angle, Point center)
	{
		AffineTransform at = new AffineTransform();
		int x[] = p.xpoints;
		int y[] = p.ypoints;
		int rx[] = new int[x.length];
		int ry[] = new int[y.length];
		
		for(int i = 0; i < p.npoints; i++)
		{
			Point2D p2d = new Point2D.Double(x[i], y[i]);
			Point2D store = null;
			store = at.getRotateInstance(Math.toRadians(angle), center.x, center.y).transform(p2d, store);
			rx[i] = (int)store.getX();
			ry[i] = (int)store.getY();
		}
		
		return new Polygon(rx, ry, p.npoints);
	}
}
