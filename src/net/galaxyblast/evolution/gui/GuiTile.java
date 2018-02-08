package net.galaxyblast.evolution.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import net.galaxyblast.evolution.world.Tile;

public class GuiTile
{
	private int x, y;
	private Tile tile;
	private Rectangle rect = new Rectangle();
	
	public GuiTile(Tile t)
	{
		this.tile = t;
		this.x = t.getX();
		this.y = t.getY();
	}
	
	public void draw(Graphics g, double scale, Point camera)
	{
		Graphics2D g2d = (Graphics2D) g;
		int size, offX, offY;
		
		size = (int)Math.round(scale * 16.0D);
		offX = (this.x * size) + (int)camera.getX();
		offY = (this.y * size) + (int)camera.getY();
		
		this.rect.setBounds(offX, offY, size, size);
		
		Color color = getTileColor(this.tile);

		g2d.setColor(color);
		g2d.fill(this.rect);
		g2d.setColor(new Color(0, 0, 0, 128));
		g2d.draw(this.rect);
	}

	public Tile getTile()
	{
		return this.tile;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public static Color getTileColor(Tile t)
	{
		Color out = Color.red;
		
		switch(t.getTileType())
		{
			case 0:
				out = Color.blue;
				break;
			case 1:
				out = new Color(194, 178, 128);
				break;
			case 2:
				double per = (double)t.getResources() / (double)t.getMaxResources();
				int r = (int)Math.min(86 + ((1.0D - per) * 255.0D), 200.0D);
				int g = (int)Math.min(176 + ((1.0D - per) * 255.0D), 230.0D);
				int b = (int)Math.min(((1.0D - per) * 255.0D), 200.0D);
				out = new Color(r, g, b);
				break;
			case 5:
			default:
				out = new Color(150, 141, 153);
				break;
		}
		
		return out;
	}
	
	public Rectangle getRectangle()
	{
		return this.rect;
	}
}
