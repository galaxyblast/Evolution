package net.galaxyblast.evolution.world;

import java.util.Random;

public class Tile
{
	private byte type;
	private int x, y;
	private int resources, maxResources;
	private Random rng;
	
	public Tile(byte t, int x, int y, Random r)
	{
		this.type = t;
		this.x = x;
		this.y = y;
		this.rng = r;
		
		int tmp;
		
		if(t == 0)
			tmp = 0;
		else if(t == 1)
			tmp = 5;
		else if(t == 2)
			tmp = 100;
		else
			tmp = 10;
		
		this.maxResources = tmp;
		if(tmp > 0)
			this.resources = tmp;
		else
			this.resources = 0;
	}
	
	/*
	 * Tile types:
	 * 0 - water
	 * 1 - beach
	 * 2 - grassland
	 * 3 - stone
	 * 4 - forest
	 * 5 - mountain
	 * 6 - marsh
	 */
	public byte getTileType()
	{
		return this.type;
	}
	
	public void setTileType(int t)
	{
		this.type = (byte)t;
	}
	
	public void setTileType(byte t)
	{
		this.type = t;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public String getTileTypeString()
	{
		switch(this.type)
		{
			case 0:
			default:
				return "Water";
			case 1:
				return "Beach";
			case 2:
				return "Grassland";
			case 5:
				return "Mountain";
		}
	}
	
	public int getResources()
	{
		return this.resources;
	}
	
	public int getMaxResources()
	{
		return this.maxResources;
	}
	
	public int decreaseResources(int amt)
	{
		int out;
		if(this.resources - amt > 0)
		{
			this.resources -= amt;
			out = amt;
		}
		else
		{
			out = this.resources;
			this.resources = 0;
		}
		
		return out;
	}
	
	public void tick()
	{
		if(this.rng.nextInt(100) == 0)
		{
			this.resources++;
			if(this.resources > this.maxResources)
				this.resources = this.maxResources;
		}
	}
}
