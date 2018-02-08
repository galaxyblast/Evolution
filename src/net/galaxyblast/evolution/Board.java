package net.galaxyblast.evolution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import net.galaxyblast.evolution.entity.Entity;
import net.galaxyblast.evolution.gui.GuiControls;
import net.galaxyblast.evolution.gui.GuiWorld;
import net.galaxyblast.evolution.world.World;

public class Board extends JPanel
{
	private World theWorld = new World();
	
	private GuiWorld world = new GuiWorld(this, 800, 1080, 0, 0, this.theWorld);
	private GuiControls controls = new GuiControls(this, 400, 1000, 800, 0);
	
	public Entity targetEntity;
	
	public Board()
	{
		this.setBackground(Color.black);
		this.setLayout(null);
		this.add(world);
		this.add(controls);
	}
	
	@Override
    public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		
	}
	
	public void resetCamera()
	{
		this.world.setCameraPosition(0, 0);
	}
	
	public World getWorld()
	{
		return this.theWorld;
	}
	
	public void resetWorldTimer()
	{
		world.resetTimer();
	}
}
