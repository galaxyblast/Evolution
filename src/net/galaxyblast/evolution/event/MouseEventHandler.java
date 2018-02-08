package net.galaxyblast.evolution.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseEventHandler extends MouseAdapter implements MouseWheelListener
{
	public static boolean mouseDown = false;
	public static boolean mouseDragged = false;
	public static int oldX = 0;
	public static int oldY = 0;
	public static int deltaX, deltaY;
	
	public static double zoom = 1.0D;
	
	public static int clickPosX = 0, clickPosY = 0;
	
	public static boolean showTooltip = false;
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		mouseDown = true;
		mouseDragged = false;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		mouseDown = false;
		clickPosX = e.getX();
		clickPosY = e.getY();
		showTooltip = true;
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		mouseDragged = true;
	}
	
	public static void toggleTooltip()
	{
		if(showTooltip)
			showTooltip = false;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if(e.getWheelRotation() < 0)
			zoom += 0.1D;
		else
			zoom -= 0.1D;
		
		if(zoom > 4.0D)
			zoom = 4.0D;
		
		if(zoom < 0.3D)
			zoom = 0.3D;
	}
}
