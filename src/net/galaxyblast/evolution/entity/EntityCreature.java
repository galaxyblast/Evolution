package net.galaxyblast.evolution.entity;

import java.awt.Color;

import net.galaxyblast.evolution.Evolution;
import net.galaxyblast.evolution.entity.ai.CreatureAI;
import net.galaxyblast.evolution.entity.ai.Traits;
import net.galaxyblast.evolution.world.World;

public class EntityCreature extends Entity
{
	private CreatureAI ai;
	public byte canBirth = 0;
	public int perfection = 0;
	public int eatingTicks = 0;
	private int healthyTicks = 0;
	private int dyingTicks = 0;
	public boolean mutated = false;
	public EntityCreature parent = null;
	
	public EntityCreature(World w, int x, int y, byte s, byte sp, Color c)
	{
		super(w, x, y, s, sp, c);
		this.ai = new CreatureAI(this, new Traits(this));
	}
	
	/*
	 * Use this one for birthing new creatures
	 */
	public EntityCreature(EntityCreature e, byte s, byte sp, Color c)
	{
		super(e.getWorld(), (int)e.getX(), (int)e.getY(), s, sp, c);
		this.parent = e;
		this.ai = new CreatureAI(this, e.getAI().getTraits());
		this.ai.getTraits().setCreature(this);
		this.ai.mutate();
		this.getWorld().addCreatureToWorld(this);
	}

	@Override
	public void tick()
	{
		super.tick();
		
		this.ai.makeDecision();
		
		if(this.getX() < 0.0D)
			this.setX(World.MAPSIZEX - 1);
		if(this.getX() > World.MAPSIZEX - 1)
			this.setX(1.0D);
		if(this.getY() < 0.0D)
			this.setY(World.MAPSIZEY - 1);
		if(this.getY() > World.MAPSIZEY - 1)
			this.setY(1.0D);
		
		this.days++;
		if(days > 365)
		{
			this.age++;
			this.days = 0;
		}
		
		/*if(this.age >= 4 && this.canBirth == 0)
			this.canBirth = 1;*/
		
		if(this.getWorld().getTile((int)this.getX(), (int)this.getY()).getTileType() == 0)
			this.healthyTicks = 0;
		
		if(this.getHealth() == 100 && ((double)this.getHunger() / (double)this.getNeeds()) >= 0.6D)
		{
			this.healthyTicks++;
			if(this.healthyTicks >= 40)
			{
				this.perfection++;
				this.healthyTicks = 0;
			}
		}
		else
		{
			this.healthyTicks = 0;
			this.dyingTicks++;
			if(this.dyingTicks >= 40)
			{
				this.dyingTicks = 0;
				this.perfection--;
				if(this.perfection < 0) this.perfection = 0;
			}
		}
	}
	
	public CreatureAI getAI()
	{
		return this.ai;
	}
}
