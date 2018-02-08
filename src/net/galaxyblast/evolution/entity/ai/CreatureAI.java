package net.galaxyblast.evolution.entity.ai;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import net.galaxyblast.evolution.entity.Entity;
import net.galaxyblast.evolution.entity.EntityCreature;
import net.galaxyblast.evolution.util.MathUtils;
import net.galaxyblast.evolution.world.Tile;
import net.galaxyblast.evolution.world.World;

public class CreatureAI
{
	private EntityCreature creature;
	private Traits traits;
	public int desperation;
	private Random rng;
	private short targetDir;
	private byte addDir = 0;
	private int[] dest = new int[2];
	public int screwedFactor = 0;
	
	private static int moveMod = 30;
	private int tickDelay = 0;
	
	public CreatureAI(EntityCreature c, Traits t)
	{
		this.creature = c;
		this.traits = t;
		this.rng = t.getRandom();
		this.targetDir = c.dir;
	}
	
	public void makeDecision()
	{
		if((double)this.creature.getHealth() / 100.0D <= 0.9)
		{
			this.desperation++;
		}
		else if(this.desperation > 0)
		{
			this.desperation -= 4;
			if(this.desperation < 0) this.desperation = 0;
		}
		
		Tile t = this.creature.getWorld().getTile((int)this.creature.getX(), (int)this.creature.getY());
		if(t != null)
		{
			int type = t.getTileType();
			int speed = (int)((double)this.traits.speedOnTile[type] - Math.ceil((double)(this.creature.age + 1) / 6.0D)) + 1;
			if(speed <= 0)
				speed = 1;
			double xOff = (Math.cos(Math.toRadians(this.creature.dir + 180)) * speed) / moveMod;
			double yOff = (Math.sin(Math.toRadians(this.creature.dir + 180)) * speed) / moveMod;
			
			this.creature.move(xOff, yOff);
			
			this.tickDelay++;
			
			if(this.tickDelay == 6)
			{
				int decAmt = speed;
				this.creature.decHunger(decAmt + 2);
				this.tickDelay = 0;
			}
			
			if((double)this.creature.getHunger() / (double)this.creature.getNeeds() < 0.8D)
			{
				Tile ti = this.creature.getWorld().getTile((int)this.creature.getX(), (int)this.creature.getY());
				if(ti != null)
				{
					int amt = ti.decreaseResources((int)Math.ceil(this.creature.getSize()));
					this.creature.addHunger(amt);
					this.creature.eatingTicks++;
					if(amt <= 2)
						this.traits.tileAffinity[ti.getTileType()] -= 1;
					else
						this.traits.tileAffinity[ti.getTileType()] += 1;
				}
			}
		}
		
		int turn = Math.abs(this.targetDir - this.creature.dir);
		if(t != null && turn >= this.traits.turnRadius[t.getTileType()])
			turn = this.traits.turnRadius[t.getTileType()];
		else
			turn = 1;
		
		/*ArrayList<Entity> ents = this.creature.getWorld().getEntitiesWithin((int)this.creature.getX(), (int)this.creature.getY(), 2);
		if(!ents.isEmpty())
		{
			int atk = (this.creature.getSize() * this.traits.attackMod) + 1;
			for(Entity e : ents)
			{
				if(e != this.creature && e.getSize() < this.creature.getSize() && this.rng.nextInt(100) < this.traits.aggression && this.creature.parent != e && e instanceof EntityCreature && ((EntityCreature)e).parent != this.creature)
				{
					this.creature.addHunger(e.getAttacked(atk));
				}
			}
		}*/
		
		if(MathUtils.DistanceBetween(this.creature.getX(), this.creature.getY(), this.dest[0], this.dest[1]) <= 1.0D || MathUtils.DistanceBetween(this.creature.getX(), this.creature.getY(), this.dest[0], this.dest[1]) >= 32.0D || (this.dest[0] == 0 && this.dest[1] == 0))
		{
			//this.targetDir += this.rng.nextInt(180) - 90;
			
			boolean flag = false;
			int count = 0;
			int lookDist = 5 + (int)Math.round((double)this.desperation * this.getTraits().despEffect);
			if(lookDist > 16)
				lookDist = 16;
			int lookDist2 = lookDist / 2;
			
			while(!flag && count < 15)
			{
				this.dest[0] = (int)this.creature.getX() + (this.rng.nextInt(lookDist) - lookDist2);
				this.dest[1] = (int)this.creature.getY() + (this.rng.nextInt(lookDist) - lookDist2);
				if(this.dest[0] < 0)
					this.dest[0] = World.MAPSIZEX - 1;
				if(this.dest[0] > World.MAPSIZEX - 1)
					this.dest[0] = 1;
				if(this.dest[1] < 0)
					this.dest[1] = World.MAPSIZEY - 1;
				if(this.dest[1] > World.MAPSIZEY - 1)
					this.dest[1] = 1;
				Tile destTile = this.creature.getWorld().getTile(this.dest[0], this.dest[1]);
				if(destTile !=null && this.traits.tileAffinity[destTile.getTileType()] >= 0 - (int)Math.round((double)this.desperation * this.getTraits().despEffect) && destTile.getResources() > 0)
				{
					flag = true;
					this.screwedFactor -= 5;
				}
				else if(this.screwedFactor < 1500)
				{
					this.screwedFactor++;
					if(this.screwedFactor > 1500)
						this.screwedFactor = 1500;
				}
				count++;
			}

			double xOff = this.creature.getX()- (double)this.dest[0];
			double yOff = (double)this.dest[1] - this.creature.getY();
			
			double rads;
			rads = Math.atan2(yOff, xOff);
			if(rads < 0.0D)
				rads = Math.abs(rads);
			else
				rads = (2 * Math.PI) - rads;
			
			this.targetDir = (short)(((int)Math.toDegrees(rads)));
			
			if(this.targetDir > 360)
				this.targetDir -= 360;
			if(this.targetDir < 0)
				this.targetDir += 360;
		}
		else if(this.targetDir == this.creature.dir)
		{
			double xOff = this.creature.getX()- (double)this.dest[0];
			double yOff = (double)this.dest[1] - this.creature.getY();
			
			double rads;
			rads = Math.atan2(yOff, xOff);
			if(rads < 0.0D)
				rads = Math.abs(rads);
			else
				rads = (2 * Math.PI) - rads;
			
			this.targetDir = (short)(((int)Math.toDegrees(rads)));
			
			if(this.targetDir > 360)
				this.targetDir -= 360;
			if(this.targetDir < 0)
				this.targetDir += 360;
		}
		
		if(this.targetDir > this.creature.dir)
			this.addDir = (byte)turn;
		else if(this.targetDir < this.creature.dir)
			this.addDir = (byte)-turn;
		else
			this.addDir = 0;
		
		if(this.targetDir != this.creature.dir)
		{
			this.creature.dir += this.addDir;
		}
		
		if(this.creature.canBirth == 1 && this.rng.nextInt(20) == 0 && this.creature.getHealth() > 95 && ((double)this.creature.getHunger() / (double)this.creature.getNeeds()) > 0.6D)
		{
			int mutateChance = 50;
			this.creature.canBirth = 0;
			boolean flag = false;
			
			byte s = this.creature.getSize();
			if(this.rng.nextInt(mutateChance) == 0)
			{
				s += this.rng.nextInt(3) - 1;
				flag = true;
			}
			if(s <= 0) s = 1;
			
			byte sp = this.creature.getSpeed();
			if(this.rng.nextInt(mutateChance) == 0)
			{
				sp += this.rng.nextInt(3) - 1;
				flag = true;
			}
			if(sp <= 0) sp = 1;
			if(sp > 10) sp = 10;
			
			Color c = this.creature.getColor();
			if(this.rng.nextInt(mutateChance) == 0)
			{
				int r = c.getRed() + this.rng.nextInt(40) - 20;
				int g = c.getGreen() + this.rng.nextInt(40) - 20;
				int b = c.getBlue() + this.rng.nextInt(40) - 20;
				flag = true;
				
				if(r < 0)
					r = 0;
				if(r > 255)
					r = 255;
				if(g < 0)
					g = 0;
				if(g > 255)
					g = 255;
				if(b < 0)
					b = 0;
				if(b > 255)
					b = 255;
				
				c = new Color(r, g, b);
			}
			
			EntityCreature child = new EntityCreature(this.creature, s, sp, c);
			if(flag && !this.creature.mutated)
			{
				child.mutated = true;
				this.creature.getWorld().mutations++;
			}
		}
		
		if(this.creature.canBirth == 2 && this.rng.nextInt(700) == 5)
			this.creature.canBirth = 0;
		
		//old age
		if(this.creature.age >= 16)
			this.creature.damage(999);
	}
	
	public void mutate()
	{
		this.traits.mutate();
	}
	
	public Traits getTraits()
	{
		return this.traits;
	}
	
	public int[] getDest()
	{
		return this.dest;
	}
	
	public double getTargetDir()
	{
		return this.targetDir;
	}
}
