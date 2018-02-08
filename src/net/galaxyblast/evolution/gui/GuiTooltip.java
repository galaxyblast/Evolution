package net.galaxyblast.evolution.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GuiTooltip
{
	public static void drawTooltip(Graphics g, int x, int y, String items[])
	{
		int xSize, ySize;
		int longestString;
		Graphics2D g2d = (Graphics2D)g;
		
		ySize = (items.length * 12) + 8;
		
		longestString = items[0].length();
		for(int i = 1; i < items.length; i++)
		{
			if(items[i].length() > longestString)
				longestString = items[i].length();
		}
		
		xSize = (longestString * 10) + 8;
		g2d.setColor(new Color(0, 0, 0, 128));
		g2d.fillRoundRect(x, y - ySize, xSize, ySize, 5, 5);
		
		g2d.setColor(Color.white);
		int xOff = 4, yOff = 14 - ySize;
		for(int i = 0; i < items.length; i++)
		{
			g2d.drawString(items[i], x + xOff, y + yOff);
			yOff += 13;
		}
	}
}
