package net.galaxyblast.evolution.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import net.galaxyblast.evolution.entity.Entity;
import net.galaxyblast.evolution.entity.EntityCreature;

public class World
{
	public static final int MAPSIZEX = 128;
	public static final int MAPSIZEY = 128;
	
	private long seed = System.currentTimeMillis();
	private Random rng = new Random();
	
	private Tile map[] = new Tile[MAPSIZEX * MAPSIZEY];
	
	private CopyOnWriteArrayList<EntityCreature> creatureList = new CopyOnWriteArrayList<EntityCreature>();
	
	public int age = 0, days = 0;
	
	public EntityCreature highestPerfection = null;
	public int highestPoint = 0;
	public int deaths = 0, births = 0, mutations = 0;
	
	public World()
	{
		this.rng.setSeed(seed);
		this.generateMap();
		this.spawnEntities();
	}
	
	private void generateMap()
	{
		OpenSimplexNoise noise = new OpenSimplexNoise(this.seed);
		double d;
		byte t;
		double stretch = 12.0D;
		
		for(int x = 0; x < MAPSIZEX; x++)
		{
			for(int y = 0; y < MAPSIZEY; y++)
			{
				if(y + (128 * x) < MAPSIZEX * MAPSIZEY)
				{
					d = noise.eval((double)x / stretch, (double)y / stretch);
					if(d < -0.2D)
					{
						t = 0;
					}
					else if(d < 0.05D)
					{
						t = 1;
					}
					else if(d < 0.6D)
					{
						t = 2;
					}
					else
					{
						t = 5;
					}
					
					this.map[y + (128 * x)] = new Tile((byte)t, x, y, this.rng);
				}
			}
		}
	}
	
	private void spawnEntities()
	{
		int amt = this.rng.nextInt(20) + 1;
		int x, y, s, sp;
		Color c;
		int r, g, b;
		for(int i = 0; i < amt; i++)
		{
			x = this.rng.nextInt(MAPSIZEX);
			y = this.rng.nextInt(MAPSIZEY);
			s = this.rng.nextInt(10) + 5;
			sp = 4;
			r = this.rng.nextInt(255);
			g = this.rng.nextInt(255);
			b = this.rng.nextInt(255);
			c = new Color(r, g, b);
			
			this.creatureList.add(new EntityCreature(this, x, y, (byte)s, (byte)sp, c));
		}
		this.highestPerfection = this.creatureList.get(0);
	}
	
	public Tile[] getMap()
	{
		return this.map;
	}
	
	public Tile getTile(int x, int y)
	{
		int c = y + (128 * x);
		if(c < this.map.length && c >= 0 && x >= 0 && y >= 0 && x <= MAPSIZEX && y <= MAPSIZEY)
			return this.map[c];
		else
			return null;
	}
	
	public Random getRandom()
	{
		return this.rng;
	}
	
	public void tick()
	{
		int h = this.highestPerfection.perfection;
		int avgPerf = 0, avgFood = 0;
		for(EntityCreature e : this.creatureList)
		{
			e.tick();
			if(e.perfection > h)
			{
				h = e.perfection;
				this.highestPerfection = e;
				if(e.perfection > this.highestPoint)
					this.highestPoint = e.perfection;
			}
			
			if(this.age % 5 == 0 && this.age > 0 && this.days == 0)
			{
				avgPerf += e.perfection;
				avgFood += e.eatingTicks;
			}
		}
		
		if(this.age % 5 == 0 && this.age > 0 && this.days == 0)
		{
			avgPerf /= this.creatureList.size();
			avgFood /= this.creatureList.size();
			int chance;
			for(EntityCreature e : this.creatureList)
			{
				if(e.perfection < avgPerf && e.eatingTicks < avgFood)
				{
					chance = (50 * (int)((double)e.perfection / (double)avgPerf)) + (50 * (int)((double)e.eatingTicks / (double)avgFood));
					if(chance <= 1)
						chance = 2;
					if(this.rng.nextInt(chance) == 0)
					{
						e.damage(999);
					}
				}
				else
					e.canBirth = 1;
			}
		}
		
		for(Tile t : this.map)
		{
			t.tick();
		}
		
		this.days++;
		if(days > 365)
		{
			this.age++;
			this.days = 0;
		}
	}
	
	public void updateSelected(Entity e)
	{
		for(EntityCreature c : this.creatureList)
		{
			if(c == e)
				c.isSelected = true;
			else
				c.isSelected = false;
		}
	}
	
	public void redrawEntities(Graphics g, double s, Point camera)
	{
		for(EntityCreature e : this.creatureList)
		{
			e.draw(g, s, camera);
		}
	}
	
	public Entity getEntityAt(int x, int y)
	{
		Entity out = null;
		
		for(EntityCreature e : this.creatureList)
		{
			int x1 = Math.abs((int)Math.floor(e.getX()) - x);
			int y1 = Math.abs((int)Math.floor(e.getY()) - y);
			if(x1 < 2 && y1 < 2)
			{
				out = e;
			}
		}
		
		return out;
	}
	
	public ArrayList<Entity> getEntitiesWithin(int x, int y, int dist)
	{
		ArrayList<Entity> out = new ArrayList<Entity>();
		
		for(EntityCreature e : this.creatureList)
		{
			int x1 = Math.abs((int)Math.floor(e.getX()) - x);
			int y1 = Math.abs((int)Math.floor(e.getY()) - y);
			if(x1 < dist && y1 < dist)
			{
				out.add(e);
			}
		}
		
		return out;
	}
	
	public void addCreatureToWorld(EntityCreature e)
	{
		this.creatureList.add(e);
		this.births++;
	}
	
	public int getLiveEntities()
	{
		return this.creatureList.size();
	}
	
	public void removeCreature(Entity e)
	{
		this.creatureList.remove((EntityCreature)e);
		this.deaths++;
		
		if(e == this.highestPerfection)
			this.highestPerfection = this.creatureList.get(0);
	}
}
