package net.galaxyblast.evolution.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;
import javax.swing.Timer;

import net.galaxyblast.evolution.Board;
import net.galaxyblast.evolution.Evolution;
import net.galaxyblast.evolution.entity.Entity;
import net.galaxyblast.evolution.entity.EntityCreature;
import net.galaxyblast.evolution.event.MouseEventHandler;
import net.galaxyblast.evolution.world.Tile;
import net.galaxyblast.evolution.world.World;

public class GuiWorld extends JPanel implements ActionListener
{
	public static byte SPEED = 1;
	
	private Board parent;
	private Point cameraPosition = new Point();
	private Timer timer;
	private World theWorld;
	private double scale = 1.0D;
	private int tipDelay = 10;
	private boolean entityFlag = false;
	private int tps = 0;
	private long time = System.currentTimeMillis();
	private Timer[] extraTimers = new Timer[7];
	
	private MouseEventHandler mouseListener = new MouseEventHandler();
	
	private GuiTile[] tiles = new GuiTile[World.MAPSIZEX * World.MAPSIZEY];
	
	public GuiWorld(Board p, int sizeX, int sizeY, int posX, int posY, World w)
	{
		this.parent = p;
		this.setLayout(null);
		this.setBounds(posX, posY, posX + sizeX, posY + sizeY);
		this.theWorld = w;
		
		this.cameraPosition.setLocation(0, 0);
		this.setBackground(Color.black);
		
		this.addMouseListener(this.mouseListener);
		this.addMouseWheelListener(this.mouseListener);
		this.addMouseMotionListener(this.mouseListener);
		
		timer = new Timer(20, this);
        timer.start();
        
        for(int i = 0; i < 7; i++)
        	extraTimers[i] = new Timer(20, this);

        if(this.getMousePosition() != null)
        {
	        MouseEventHandler.oldX = (int)this.getMousePosition().getX();
	        MouseEventHandler.oldY = (int)this.getMousePosition().getY();
        }
        
        Tile[] mapGrid = this.theWorld.getMap();
        for(int i = 0; i < mapGrid.length; i++)
        {
        	this.tiles[i] = new GuiTile(mapGrid[i]);
        }
	}
	
	public void setCameraPosition(int x, int y)
	{
		this.cameraPosition.setLocation(x, y);
	}
	
	public void moveCamera(int amtX, int amtY)
	{
		int tmpX = (int)this.cameraPosition.getX();
		int tmpY = (int)this.cameraPosition.getY();
		
		this.cameraPosition.setLocation(tmpX + amtX, tmpY + amtY);
	}
	
	@Override
    public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		this.drawWorld(g);
		
		Entity entity = this.parent.targetEntity;
		if(entity instanceof EntityCreature)
		{
			EntityCreature ent = (EntityCreature)entity;
			int x = (int)Math.floor((ent.getAI().getDest()[0] * (int)Math.round(this.scale * 16.0D)) + this.cameraPosition.getX());
			int y = (int)Math.floor((ent.getAI().getDest()[1] * (int)Math.round(this.scale * 16.0D)) + this.cameraPosition.getY());
			
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.red);
			g2d.fillOval(x, y, 5, 5);
		}
		
		this.theWorld.redrawEntities(g, this.scale, this.cameraPosition);
		
		if(MouseEventHandler.showTooltip && this.getMousePosition() != null && this.tipDelay == 0)
		{
			int x = (int)Math.floor(((this.getMousePosition().getX() - this.cameraPosition.getX()) / (int)Math.round(this.scale * 16.0D)));
			int y = (int)Math.floor(((this.getMousePosition().getY() - this.cameraPosition.getY()) / (int)Math.round(this.scale * 16.0D)));
			
			if(this.theWorld.getTile(x, y) != null)
				GuiTooltip.drawTooltip(g, (int)this.getMousePosition().getX(), (int)this.getMousePosition().getY(), new String[] {this.theWorld.getTile(x, y).getTileTypeString(), "X: " + x, "Y: " + y, "Resources: " + this.theWorld.getTile(x, y).getResources()});
		}
		else if(MouseEventHandler.showTooltip && this.tipDelay > 0)
			this.tipDelay--;
		else if(!MouseEventHandler.showTooltip && this.tipDelay == 0)
			this.tipDelay = 10;
		
		if(Evolution.debugMode)
			this.drawDebugCameraPoint(g);
	}
	
	private void drawDebugCameraPoint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		Ellipse2D e = new Ellipse2D.Double(this.cameraPosition.getX(), this.cameraPosition.getY(), 3, 3);
        g2d.setColor(Color.red);
        g2d.draw(e);
	}
	
	private void drawWorld(Graphics g)
	{
		for(int i = 0; i < this.tiles.length; i++)
		{
			this.tiles[i].draw(g, this.scale, this.cameraPosition);
		}
	}
	
	private void tick()
	{
		/*tps++;
		if(System.currentTimeMillis() - this.time >= 1000)
		{
			System.out.println(tps + " ticks per second");
			tps = 0;
			this.time = System.currentTimeMillis();
		}*/
		
		if(this.getMousePosition() != null && !this.hasFocus())
		{
			Point mouse = this.getMousePosition();
			
			MouseEventHandler.deltaX = (int)mouse.getX() - MouseEventHandler.oldX;
			MouseEventHandler.deltaY = (int)mouse.getY() - MouseEventHandler.oldY;
			
			MouseEventHandler.oldX = (int)mouse.getX();
			MouseEventHandler.oldY = (int)mouse.getY();
		}
		else
		{
			MouseEventHandler.deltaX = 0;
			MouseEventHandler.deltaY = 0;
		}
		
		if(MouseEventHandler.mouseDown && this.getMousePosition() != null)
		{
			this.moveCamera(MouseEventHandler.deltaX, MouseEventHandler.deltaY);
		}
		
		if(this.scale != MouseEventHandler.zoom && this.getMousePosition() != null)
		{
			
			
			this.scale = MouseEventHandler.zoom;
		}
		
		if(MouseEventHandler.deltaX != 0 || MouseEventHandler.deltaY != 0)
			MouseEventHandler.toggleTooltip();
		
		if(MouseEventHandler.showTooltip && !this.entityFlag && this.getMousePosition() != null && !MouseEventHandler.mouseDragged)
		{
			Point mouse = this.getMousePosition();
			
			int x = (int)Math.floor(((mouse.getX() - this.cameraPosition.getX()) / (int)Math.round(this.scale * 16.0D)));
			int y = (int)Math.floor(((mouse.getY() - this.cameraPosition.getY()) / (int)Math.round(this.scale * 16.0D)));
			Entity ent = this.theWorld.getEntityAt(x, y);
			this.parent.targetEntity = ent;
			
			this.entityFlag = true;
		}
		
		if(!MouseEventHandler.showTooltip && this.entityFlag)
		{
			this.entityFlag = false;
		}
		
		this.theWorld.updateSelected(this.parent.targetEntity);
		this.theWorld.tick();
	}
	
	public void resetTimer()
	{
		for(int i = 0; i < 7; i++)
			extraTimers[i].stop();
		
		for(int i = 0; i < SPEED - 1; i++)
		{
			extraTimers[i].start();
		}
		
        System.out.println("Sim speed set to: " + SPEED);
	}

	public void actionPerformed(ActionEvent e)
	{
		this.tick();
		this.repaint();
	}
}
