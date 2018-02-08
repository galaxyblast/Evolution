package net.galaxyblast.evolution.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.Timer;

import net.galaxyblast.evolution.Board;
import net.galaxyblast.evolution.Evolution;
import net.galaxyblast.evolution.entity.Entity;
import net.galaxyblast.evolution.entity.EntityCreature;
import net.galaxyblast.evolution.util.GuiUtil;

public class GuiControls extends JPanel implements ActionListener
{
	private Board parent;
	private Timer timer;
	
	public GuiControls(Board p, int sizeX, int sizeY, int posX, int posY)
	{
		this.parent = p;
		this.setLayout(null);
		this.setBounds(posX, posY, posX + sizeX, posY + sizeY);
		this.setBackground(Color.DARK_GRAY);
		
		timer = new Timer(20, this);
        timer.start();
        
        this.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mousePressed(MouseEvent e)
        	{
        		
        	}
        	
        	@Override
        	public void mouseReleased(MouseEvent e)
        	{
        		int x = e.getX();
        		int y = e.getY();
        		
        		if(x >= 30 && x <= 180 && y >= 900 && y <= 935)
        			parent.resetCamera();
        		
        		else if(x >= 180 && x <= 180 + 16 && y >= 700 - 14 && y <= 700)
        			parent.targetEntity = parent.getWorld().highestPerfection;
        		
        		else if(y >= 859 && y <= 894)
        		{
        			if(x >= 180 && x <= 215)
        				GuiWorld.SPEED = 1;
        			else if(x >= 230 && x <= 265)
        				GuiWorld.SPEED = 2;
        			else if(x >= 280 && x <= 315)
        				GuiWorld.SPEED = 4;
        			else if(x >= 330 && x <= 365)
        				GuiWorld.SPEED = 8;
        			if(x >= 180 && x <= 362)
        				parent.resetWorldTimer();
        		}
        	}
        });
	}
	
	@Override
    public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		this.drawSelectedEntity(g);
		this.drawButtons(g);
		this.drawInfo(g);
		this.drawStatistics(g);
	}
	
	private void drawInfo(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		Font f = g2d.getFont();
		
		g2d.setColor(Color.white);
		g2d.setFont(new Font(f.getFontName(), f.getStyle(), 20)); 
		g2d.drawString("Year: " + this.parent.getWorld().age, 20, 25);
		g2d.drawString("Live Creautres: " + this.parent.getWorld().getLiveEntities(), 200, 25);
		g2d.setFont(new Font(f.getFontName(), f.getStyle(), 16)); 
		g2d.drawString("Simulation Speed", 30, 880);
		g2d.setFont(f);
	}
	
	private void drawButtons(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.magenta);
		g2d.fillRoundRect(30, 900, 150, 35, 7, 7);
		g2d.setColor(Color.black);
		g2d.drawString("Reset camera position", 41, 921);
		
		int s = 1;
		for(int i = 0; i < 4; i++)
		{
			g2d.setColor(Color.magenta);
			g2d.fillRoundRect(180 + (i * 50), 859, 35, 35, 7, 7);
			g2d.setColor(Color.black);
			g2d.drawString(s + "x", 191 + (i * 50), 880);
			
			if(GuiWorld.SPEED == (byte)s)
			{
				g2d.setColor(Color.white);
				g2d.drawRoundRect(180 + (i * 50), 859, 35, 35, 7, 7);
			}
			s *= 2;
		}
	}
	
	private void drawSelectedEntity(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		double x = 0D, y = 0D;
		int need = 0, health = 0, hunger = 0, age = 0, days = 0;
		int dir;
		Color c;
		int size = 0, speed = 0;
		boolean mute = false;
		int perf = 0;
		int agr = 0;
		int desper = 0;
		double desp = 0.0D;
		
		Entity ent;
		
		if(this.parent.targetEntity != null)
		{
			ent = this.parent.targetEntity;
			x = ent.getX();
			y = ent.getY();
			need = ent.getNeeds();
			health = ent.getHealth();
			hunger = ent.getHunger();
			c = ent.getColor();
			size = ent.getSize();
			speed = ent.getSpeed();
			dir = ent.dir;
			age = ent.age;
			days = ent.days;
			if(ent instanceof EntityCreature)
			{
				mute = ((EntityCreature)ent).mutated;
				perf = ((EntityCreature)ent).perfection;
				agr = ((EntityCreature)ent).getAI().getTraits().aggression;
				desper = ((EntityCreature)ent).getAI().desperation;
				desp = ((EntityCreature)ent).getAI().getTraits().despEffect;
			}
			
			g2d.setColor(c);
			g2d.fillOval(170, 100, 50, 50);
			g2d.setColor(Color.white);
			g2d.drawOval(170, 100, 50, 50);
			
			//triangle
			int x1[] = {((50 / 2) - (int)(4.0D * 2.0D) + 170), ((50 / 2) + 170), ((50 / 2) + (int)(4.0D * 2.0D) + 170)};
			int y1[] = {100, 100 - (int)(5.0D * 2.0D), 100};
			
			//apply rotation
			Polygon p = new Polygon(x1, y1, 3);
			g2d.setColor(Color.black);
			g2d.fillPolygon(GuiUtil.getRotatedPolygon(p, dir - 90, new Point(170 + (50 / 2), 100 + (50 / 2))));
		}
		
		Font f = g2d.getFont();
		DecimalFormat df = new DecimalFormat("#.##");

		g2d.setColor(Color.white);
		g2d.setFont(new Font(f.getFontName(), f.getStyle(), 16)); 
		g2d.drawString("Position: (" + df.format(x) + ", " + df.format(y) + ")", 40, 300);
		g2d.drawString("Needs: " + need, 40, 330);
		g2d.drawString("Hunger: " + hunger, 40, 360);
		g2d.drawString("Age: " + age + " Years, " + days + " Days", 40, 390);
		g2d.drawString("Perfection: " + perf, 40, 420);
		g2d.drawString("Desperation: " + desper, 40, 450);
		g2d.drawString("Health: " + health, 220, 300);
		g2d.drawString("Size: " + size, 220, 330);
		g2d.drawString("Walk Speed: " + speed, 220, 360);
		g2d.drawString("Mutated: " + mute, 220, 390);
		g2d.drawString("Aggression: " + agr, 220, 420);
		g2d.drawString("Desperation Mod: " + df.format(desp), 220, 450);
		g2d.setFont(f);
	}
	
	private void drawStatistics(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		Font f = g2d.getFont();
		
		g2d.setColor(Color.white);
		g2d.setFont(new Font(f.getFontName(), f.getStyle(), 16)); 
		g2d.drawString("Highest Perfection:", 40, 700);
		g2d.drawString("Highest Recorded: " + this.parent.getWorld().highestPoint, 220, 700);
		g2d.drawString("Births: " + this.parent.getWorld().births, 40, 730);
		g2d.drawString("Deaths: " + this.parent.getWorld().deaths, 40, 760);
		g2d.drawString("Mutations: " + this.parent.getWorld().mutations, 40, 790);
		g2d.setFont(f);
		
		EntityCreature ent;
		
		if(this.parent.getWorld().highestPerfection != null)
		{
			ent = this.parent.getWorld().highestPerfection;
			
			Color c = ent.getColor();
			int dir = ent.dir;
			
			int drawSize = 16;
			int xOff = 180, yOff = 700 - drawSize + 2;
			
			g2d.setColor(c);
			g2d.fillOval(xOff, yOff, drawSize, drawSize);
			g2d.setColor(Color.white);
			g2d.drawOval(xOff, yOff, drawSize, drawSize);
		}
	}
	
	public void tick()
	{
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		this.tick();
		this.repaint();
	}
}
