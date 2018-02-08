package net.galaxyblast.evolution.entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import net.galaxyblast.evolution.util.GuiUtil;
import net.galaxyblast.evolution.world.World;

public class Entity
{
	public static int MAX_SPEED = 8;
	
	private double xPos, yPos;
	private int needs, hunger, health;
	private Color color;
	private byte size;
	private byte speed;
	private World theWorld;
	public int age = 0;
	public int days = 0;
	public boolean isSelected = false;
	
	public short dir = 90;
	
	private Ellipse2D circle;

	public Entity(World w, int x, int y, byte s, byte sp, Color c)
	{
		this.xPos = (double)x;
		this.yPos = (double)y;
		this.needs = s * 25;
		this.size = s;
		this.speed = sp;
		this.color = c;
		this.hunger = s * 25;
		this.health = 100;
		this.theWorld = w;
	}
	
	public double getX()
	{
		return this.xPos;
	}

	public void setX(double x)
	{
		this.xPos = x;
	}

	public double getY()
	{
		return this.yPos;
	}

	public void setY(double y)
	{
		this.yPos = y;
	}
	
	public void move(double xAmt, double yAmt)
	{
		this.xPos += xAmt;
		this.yPos += yAmt;
	}

	public int getNeeds()
	{
		return this.needs;
	}

	public void setNeeds(int needs)
	{
		this.needs = needs;
	}

	public int getHunger()
	{
		return this.hunger;
	}

	public void setHunger(int hunger)
	{
		this.hunger = hunger;
	}
	
	public void addHunger(int amt)
	{
		this.hunger += amt;
	}
	
	public void decHunger(int amt)
	{
		this.hunger -= amt;
		if(this.hunger < 0)
		{
			this.hunger = 0;
			this.damage(1);
		}
	}

	public int getHealth()
	{
		return this.health;
	}

	public void setHealth(int health)
	{
		this.health = health;
	}

	public Color getColor()
	{
		return this.color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public byte getSize()
	{
		return this.size;
	}

	public void setSize(byte size)
	{
		this.size = size;
	}

	public byte getSpeed()
	{
		return this.speed;
	}

	public void setSpeed(byte speed)
	{
		this.speed = speed;
	}
	
	public World getWorld()
	{
		return this.theWorld;
	}
	
	public void damage(int amt)
	{
		this.health -= amt;
		if(this.health <= 0)
			this.theWorld.removeCreature(this);
	}
	
	public int getAttacked(int amt)
	{
		int out = Math.round((float)amt * ((float)this.size / 15.0F));
		this.damage(out);
		
		return out;
	}
	
	public void tick()
	{
		if((double)this.hunger / (double)this.needs > 0.6D)
		{
			this.health++;
			if(this.health > 100)
				this.health = 100;
		}
	}
	
	public void draw(Graphics g, double scale, Point camera)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		int s, s1, offX, offY;
		
		s = (int)Math.round(scale * 16.0D);
		s1 = (int)Math.round(scale * this.size);
		offX = (int)(this.xPos * s) + (int)camera.getX();
		offY = (int)(this.yPos * s) + (int)camera.getY();
		
		this.circle = new Ellipse2D.Double(offX, offY, s1, s1);
		
		g2d.setColor(this.color);
		g2d.fill(this.circle);
		
		Stroke stroke = g2d.getStroke();
		
		if(this.isSelected)
		{
			g2d.setColor(Color.yellow);
			g2d.setStroke(new BasicStroke(3));
		}
		else
			g2d.setColor(Color.white);
		
		g2d.draw(this.circle);
		g2d.setStroke(stroke);
		
		//triangle
		int x[] = {(s1 / 2) - (int)(4.0D * scale) + offX, (s1 / 2) + offX, (s1 / 2) + (int)(4.0D * scale) + offX};
		int y[] = {offY, offY - (int)(5.0D * scale), offY};
		
		//apply rotation
		
		Polygon p = new Polygon(x, y, 3);
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillPolygon(GuiUtil.getRotatedPolygon(p, this.dir - 90, new Point(offX + (s1 / 2), offY + (s1 / 2))));
	}
}
