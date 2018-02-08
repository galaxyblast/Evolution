package net.galaxyblast.evolution.entity.ai;

import java.util.Random;

import net.galaxyblast.evolution.entity.Entity;
import net.galaxyblast.evolution.entity.EntityCreature;

public class Traits
{
	public int speedOnTile[] = new int[7];
	public int tileAffinity[] = new int[7];
	public double despEffect;
	public int attackMod = 0;
	public int aggression = 0;
	public int turnRadius[] = new int[7];
	private Random rng = new Random();
	private EntityCreature creature;
	
	public Traits(EntityCreature e)
	{
		this.creature = e;
		for(int i = 0; i < this.speedOnTile.length; i++)
		{
			this.speedOnTile[i] = this.rng.nextInt(e.getSpeed()) + 1;
			this.tileAffinity[i] = 0;
			this.turnRadius[i] = 1;
		}
		this.despEffect = this.rng.nextDouble();
	}
	
	public void setCreature(EntityCreature e)
	{
		this.creature = e;
	}
	
	public void mutate()
	{
		if(this.creature.parent != null)
		{
			boolean flag = false;
			for(int i = 0; i < this.speedOnTile.length; i++)
			{
				if(this.rng.nextInt(this.creature.parent.perfection * 2 + 1) == 0)
				{
					if(this.tileAffinity[i] < 0)
					{
						this.speedOnTile[i] += 1;
						this.turnRadius[i]++;
						if(this.turnRadius[i] > 4)
							this.turnRadius[i] = 4;
					}
					else
					{
						this.speedOnTile[i] -= 1;
						this.turnRadius[i]--;
						if(this.turnRadius[i] < 1)
							this.turnRadius[i] = 1;
					}
					
					if(this.speedOnTile[i] > Entity.MAX_SPEED)
						this.speedOnTile[i] = Entity.MAX_SPEED;
					else if(this.speedOnTile[i] <= 0)
						this.speedOnTile[i] = 1;
					
					flag = true;
				}
			}
			
			if((double)this.creature.getAI().screwedFactor / 1500.0D > 0.6D && this.rng.nextBoolean() && this.despEffect < 1.0D)
			{
				this.despEffect += 0.125D;
				if(this.despEffect > 1.0D)
					this.despEffect = 1.0D;
				flag = true;
			}
			
			if(this.rng.nextInt(this.creature.parent.perfection + 1) == 0)
			{
				this.attackMod += 1;
				if(this.attackMod > 5)
					this.attackMod = 5;
				
				flag = true;
			}
			
			if(this.rng.nextInt(200) == 0)
			{
				this.aggression += 1;
				if(this.aggression > 100)
					this.aggression = 100;
				
				flag = true;
			}
			
			if(flag && !this.creature.mutated)
			{
				this.creature.mutated = true;
				this.creature.getWorld().mutations++;
			}
		}
	}
	
	public Random getRandom()
	{
		return this.rng;
	}
}
