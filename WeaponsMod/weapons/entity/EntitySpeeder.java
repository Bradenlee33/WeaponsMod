package weapons.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import weapons.Weapons;
import weapons.events.EventShipControl;
import weapons.particles.ParticleThrust;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySpeeder extends Entity
{
	private boolean field_70279_a;
	private double speedMultiplier;
	private int boatPosRotationIncrements;
	private double boatX;
	private double boatY;
	private double boatZ;
	private double boatYaw;
	private double boatPitch;
	public double renderrotationPitch = 0;
	public double renderrotationYaw = 0;
	@SideOnly(Side.CLIENT)
	private double velocityX;
	@SideOnly(Side.CLIENT)
	private double velocityY;
	@SideOnly(Side.CLIENT)
	private double velocityZ;

	public EntitySpeeder(World par1World)
	{
		super(par1World);
		this.field_70279_a = true;
		this.speedMultiplier = 0.07D;
		this.preventEntitySpawning = true;
		this.setSize(2.5F, 2F);
		this.yOffset = this.height / 2.0F;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking()
	{
		return false;
	}

	protected void entityInit()
	{
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(1));
		this.dataWatcher.addObject(19, new Integer(0));
	}

	/**
	 * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
	 * pushable on contact, like boats or minecarts.
	 */
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		return par1Entity.boundingBox;
	}

	/**
	 * returns the bounding box for this entity
	 */
	public AxisAlignedBB getBoundingBox()
	{
		return this.boundingBox;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	public boolean canBePushed()
	{
		return true;
	}

	public EntitySpeeder(World par1World, double par2, double par4, double par6)
	{
		this(par1World);
		this.setPosition(par2, par4 + (double)this.yOffset, par6);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = par2;
		this.prevPosY = par4;
		this.prevPosZ = par6;
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding this one.
	 */
	public double getMountedYOffset()
	{
		return (double)this.height * 0.0D - 0.30000001192092896D;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
	{
		if (this.isEntityInvulnerable())
		{
			return false;
		}
		else if (!this.worldObj.isRemote && !this.isDead)
		{
			this.setForwardDirection(-this.getForwardDirection());
			this.setTimeSinceHit(10);
			this.setDamageTaken(this.getDamageTaken() + par2 * 10);
			this.setBeenAttacked();
			boolean flag = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode;

			if (flag || this.getDamageTaken() > 40)
			{
				if (this.riddenByEntity != null)
				{
					this.riddenByEntity.mountEntity(this);
				}

				if (!flag)
				{
					this.dropItemWithOffset(Weapons.spaceship.itemID, 1, 0.0F);
				}

				this.setDead();
			}

			return true;
		}
		else
		{
			return true;
		}
	}

	@SideOnly(Side.CLIENT)

	/**
	 * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
	 */
	public void performHurtAnimation()
	{
		this.setForwardDirection(-this.getForwardDirection());
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11);
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}

	@SideOnly(Side.CLIENT)

	/**
	 * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
	 * posY, posZ, yaw, pitch
	 */
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
	{
		if (this.field_70279_a)
		{
			this.boatPosRotationIncrements = par9 + 5;
		}
		else
		{
			double d3 = par1 - this.posX;
			double d4 = par3 - this.posY;
			double d5 = par5 - this.posZ;
			double d6 = d3 * d3 + d4 * d4 + d5 * d5;

			if (d6 <= 1.0D)
			{
				return;
			}

			this.boatPosRotationIncrements = 3;
		}

		this.boatX = par1;
		this.boatY = par3;
		this.boatZ = par5;
		this.boatYaw = (double)par7;
		this.boatPitch = (double)par8;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	@SideOnly(Side.CLIENT)

	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	public void setVelocity(double par1, double par3, double par5)
	{
		this.velocityX = this.motionX = par1;
		this.velocityY = this.motionY = par3;
		this.velocityZ = this.motionZ = par5;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		super.onUpdate();
		float pointX = (float) Math.cos(Math.toRadians(this.renderrotationPitch)) * (float) Math.sin(Math.toRadians(this.renderrotationYaw) + Math.PI);
		float pointZ = (float) Math.cos(Math.toRadians(this.renderrotationPitch)) * (float) Math.cos(Math.toRadians(this.renderrotationYaw));
		float pointY = (float) Math.sin(Math.toRadians(this.renderrotationPitch) + Math.PI);
		ParticleThrust particle = new ParticleThrust(this.worldObj, this.posX - (2 * pointX), this.posY + pointY, this.posZ + (2 * pointZ), 0, 0, 0);
		Weapons.proxy.spawnParticle(particle);
		ParticleThrust particle2 = new ParticleThrust(this.worldObj, this.posX+ (2 * pointX), this.posY + pointY, this.posZ + (2 * pointZ), 0, 0, 0);
		Weapons.proxy.spawnParticle(particle2);
		ParticleThrust particle3 = new ParticleThrust(this.worldObj, this.posX+ pointX, this.posY + (2 * pointY), this.posZ + (2 * pointZ), 0, 0, 0);
		Weapons.proxy.spawnParticle(particle3);
		

		if (this.getTimeSinceHit() > 0)
		{
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if (this.getDamageTaken() > 0)
		{
			this.setDamageTaken(this.getDamageTaken() - 1);
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		double d0 = 0.0D;



		double d3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		double d4;
		double d5;



		double d10;
		double d11;

		if (this.worldObj.isRemote && this.field_70279_a)
		{
			if (this.boatPosRotationIncrements > 0)
			{
				d4 = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
				d5 = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
				d11 = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
				d10 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double)this.rotationYaw);
				this.rotationYaw = (float)((double)this.rotationYaw + d10 / (double)this.boatPosRotationIncrements);
				this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
				--this.boatPosRotationIncrements;
				this.setPosition(d4, d5, d11);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}
			else
			{
				d4 = this.posX + this.motionX;
				d5 = this.posY + this.motionY;
				d11 = this.posZ + this.motionZ;
				this.setPosition(d4, d5, d11);

				if (this.onGround)
				{
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.motionX *= 0.9900000095367432D;
				this.motionY *= 0.949999988079071D;
				this.motionZ *= 0.9900000095367432D;
			}
		}
		else
		{

			d4 = d0 * 2.0D - 1.0D;
			this.motionY += 0.03999999910593033D * d4;

			if (this.riddenByEntity != null)
			{
				this.motionX += this.riddenByEntity.motionX * this.speedMultiplier;
				this.motionZ += this.riddenByEntity.motionZ * this.speedMultiplier;
			}

			d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

			if (d4 > 0.35D)
			{
				d5 = 0.35D / d4;
				this.motionX *= d5;
				this.motionZ *= d5;
				d4 = 0.35D;
			}

			if (d4 > d3 && this.speedMultiplier < 0.35D)
			{
				this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

				if (this.speedMultiplier > 0.35D)
				{
					this.speedMultiplier = 0.35D;
				}
			}
			else
			{
				this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

				if (this.speedMultiplier < 0.07D)
				{
					this.speedMultiplier = 0.07D;
				}
			}

			if (this.onGround)
			{
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);

			if (this.isCollidedHorizontally && d3 > 0.2D)
			{
				if (!this.worldObj.isRemote && !this.isDead)
				{
					this.setDead();
					int k;

					int blockid = this.worldObj.getBlockId((int)this.posX, (int)this.posY, (int)this.posZ);
					if(blockid == 0){
						blockid = 51;
					}
					EntityFallingSand theentity = new EntityFallingSand(this.worldObj, this.posX, this.posY, this.posZ, blockid);
					this.worldObj.spawnEntityInWorld(theentity);
					this.worldObj.createExplosion(this, (float)this.posX, (float)this.posY, (float)this.posZ, 10, true);
					for (k = 0; k < 3; ++k)
					{
						this.dropItemWithOffset(Block.planks.blockID, 1, 0.0F);
					}

					for (k = 0; k < 2; ++k)
					{
						this.dropItemWithOffset(Item.stick.itemID, 1, 0.0F);
					}
				}
			}
			else
			{
				this.motionX *= 0.9900000095367432D;
				this.motionY *= 0.949999988079071D;
				this.motionZ *= 0.9900000095367432D;
			}

			this.rotationPitch = 0.0F;
			d5 = (double)this.rotationYaw;
			d11 = this.prevPosX - this.posX;
			d10 = this.prevPosZ - this.posZ;

			if (d11 * d11 + d10 * d10 > 0.001D)
			{
				d5 = (double)((float)(Math.atan2(d10, d11) * 180.0D / Math.PI));
			}

			double d12 = MathHelper.wrapAngleTo180_double(d5 - (double)this.rotationYaw);

			if (d12 > 20.0D)
			{
				d12 = 20.0D;
			}

			if (d12 < -20.0D)
			{
				d12 = -20.0D;
			}

			this.rotationYaw = (float)((double)this.rotationYaw + d12);
			this.setRotation(this.rotationYaw, this.rotationPitch);

			if (!this.worldObj.isRemote)
			{
				@SuppressWarnings("rawtypes")
				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
				int l;

				if (list != null && !list.isEmpty())
				{
					for (l = 0; l < list.size(); ++l)
					{
						Entity entity = (Entity)list.get(l);

						if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntitySpeeder)
						{
							entity.applyEntityCollision(this);
						}
					}
				}

				for (l = 0; l < 4; ++l)
				{
					int i1 = MathHelper.floor_double(this.posX + ((double)(l % 2) - 0.5D) * 0.8D);
					int j1 = MathHelper.floor_double(this.posZ + ((double)(l / 2) - 0.5D) * 0.8D);

					for (int k1 = 0; k1 < 2; ++k1)
					{
						int l1 = MathHelper.floor_double(this.posY) + k1;
						int i2 = this.worldObj.getBlockId(i1, l1, j1);

						if (i2 == Block.snow.blockID)
						{
							this.worldObj.setBlockToAir(i1, l1, j1);
						}
						else if (i2 == Block.waterlily.blockID)
						{
							this.worldObj.destroyBlock(i1, l1, j1, true);
						}
					}
				}

				if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				{
					this.riddenByEntity = null;
				}
			}
		}
		this.updateflying();
	}
	public void updateflying(){
		if(this.riddenByEntity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) this.riddenByEntity;
			float volocityX;
			float volocityY = 0;
			float volocityZ = 0; 
			this.renderrotationPitch = player.rotationPitch;
			this.renderrotationYaw = player.rotationYawHead;
			volocityX = (float) Math.cos(Math.toRadians(player.rotationPitch)) * (float) Math.sin(Math.toRadians(player.rotationYawHead) + Math.PI);
			volocityZ = (float) Math.cos(Math.toRadians(player.rotationPitch)) * (float) Math.cos(Math.toRadians(player.rotationYawHead));
			volocityY = (float) Math.sin(Math.toRadians(player.rotationPitch) + Math.PI);
			double startX = player.posX + volocityX;
			double startY = player.posY + volocityY + 1;
			double startZ = player.posZ + volocityZ;
			double x = this.posX + startX * 36;
			double y = this.posY + startY * 6;
			double z = this.posZ + startZ * 36;
			this.moveFlying((float)x, (float)y, (float)z);
			this.motionX = volocityX * 36;
			this.motionY = volocityY * 6;
			this.motionZ = volocityZ * 36;
			player.fallDistance = 0;
		}
	}
	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
		}

	}
	@ForgeSubscribe
	public void handleShipControl(EventShipControl event) {
		System.out.println("ship is being controled by: " + event.player);
		if(this.ridingEntity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) this.ridingEntity;
			if(event.player.equalsIgnoreCase(player.username)){
				System.out.println("Airship going up");
			}
		}
	}
	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer par1EntityPlayer)
	{
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != par1EntityPlayer)
		{
			return true;
		}
		else
		{
			if (!this.worldObj.isRemote)
			{
				par1EntityPlayer.mountEntity(this);
			}

			return true;
		}
	}

	/**
	 * Sets the damage taken from the last hit.
	 */
	public void setDamageTaken(int par1)
	{
		this.dataWatcher.updateObject(19, Integer.valueOf(par1));
	}

	/**
	 * Gets the damage taken from the last hit.
	 */
	public int getDamageTaken()
	{
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	/**
	 * Sets the time to count down from since the last time entity was hit.
	 */
	public void setTimeSinceHit(int par1)
	{
		this.dataWatcher.updateObject(17, Integer.valueOf(par1));
	}

	/**
	 * Gets the time since the last hit.
	 */
	public int getTimeSinceHit()
	{
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	/**
	 * Sets the forward direction of the entity.
	 */
	public void setForwardDirection(int par1)
	{
		this.dataWatcher.updateObject(18, Integer.valueOf(par1));
	}

	/**
	 * Gets the forward direction of the entity.
	 */
	public int getForwardDirection()
	{
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	@SideOnly(Side.CLIENT)
	public void func_70270_d(boolean par1)
	{
		this.field_70279_a = par1;
	}
}
