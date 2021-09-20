package com.gildedgames.aether.common.entity.monster;

import com.gildedgames.aether.common.registry.AetherBlocks;
import com.gildedgames.aether.common.registry.AetherEntityTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class SentryEntity extends SlimeEntity {
	public static final DataParameter<Boolean> SENTRY_AWAKE = EntityDataManager.defineId(SentryEntity.class, DataSerializers.BOOLEAN);
	
	public float timeSpotted = 0.0F;
	
	public SentryEntity(EntityType<? extends SentryEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public SentryEntity(World worldIn) {
		super(AetherEntityTypes.SENTRY.get(), worldIn);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new SentryEntity.FloatGoal(this));
		this.goalSelector.addGoal(2, new SentryEntity.AttackGoal(this));
		this.goalSelector.addGoal(3, new SentryEntity.FaceRandomGoal(this));
		this.goalSelector.addGoal(5, new SentryEntity.HopGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> Math.abs(entity.getY() - this.getY()) <= 4.0));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
	}

	public static AttributeModifierMap.MutableAttribute createMobAttributes() {
		return MobEntity.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SENTRY_AWAKE, false);
	}
	
	@Override
	public void playerTouch(PlayerEntity entityIn) {
		if (EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entityIn)) {
			this.explodeAt(entityIn);
		}
	}
	
	@Override
	public void tick() {
		/*
		 Changed from no creative or spectators to no spectators. This is one method of having the mob hop around, as the reason it wasn't earlier seems to have been it wasn't awake.
		 This keeps the sentries awake so the AI can continue to work.
		 Alternatives include removing the awake check in the sentry's hop goal.
		*/
		if (this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 8.0, EntityPredicates.NO_SPECTATORS) != null) {
			if (!this.isAwake()) {
				if (this.timeSpotted >= 24) {
					this.setAwake(true);
				}
				this.timeSpotted++;
			}
		}
		else {
			this.setAwake(false);
		}
		
		super.tick();
	}
	
//	@Override
//	protected ResourceLocation getLootTable() {
//		return this.getType().getLootTable();
//	}
	
	@Override
	protected IParticleData getParticleType() {
		return new BlockParticleData(ParticleTypes.BLOCK, AetherBlocks.SENTRY_STONE.get().defaultBlockState());
	}
	
	@Override
	public void push(Entity entityIn) {
		super.push(entityIn);
		
		if (!(entityIn instanceof SentryEntity) && entityIn instanceof LivingEntity) {
			this.explodeAt((LivingEntity)entityIn);
		}
	}

	protected void explodeAt(LivingEntity entityIn) {
		if (this.isAwake() && this.canSee(entityIn) && entityIn.hurt(DamageSource.mobAttack(this), 1.0F) && this.tickCount > 20) {
			entityIn.push(0.5, 0.5, 0.5);
			
			this.level.explode(this, this.getX(), this.getY(), this.getZ(), 0.1F, Explosion.Mode.DESTROY);
			this.setHealth(0.0F);
			this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.2F*(this.random.nextFloat() - this.random.nextFloat()) + 1);
			this.doEnchantDamageEffects(this, entityIn);
		}
	}
	
	@Override
	protected void jumpFromGround() {
		if (this.isAwake()) {
			super.jumpFromGround();
		}
	}

	/* Do we need this..?
	@Override
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
		this.getAttribute(Attributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
		if (this.rand.nextFloat() < 0.05F) {
			this.setLeftHanded(true);
		}
		else {
			this.setLeftHanded(false);
		}

		return spawnDataIn;
	}
	*/
	
	public void setAwake(boolean isAwake) {
		this.entityData.set(SENTRY_AWAKE, isAwake);
	}
	
	public boolean isAwake() {
		return this.entityData.get(SENTRY_AWAKE);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EntityType<? extends SentryEntity> getType() {
		return (EntityType<? extends SentryEntity>) super.getType();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void remove(boolean keepData) {
		this.removed = true;
		super.remove(keepData);
	}
	
	public static class AttackGoal extends SlimeEntity.AttackGoal {
		private final SentryEntity sentry;
		
		public AttackGoal(SentryEntity sentryIn) {
			super(sentryIn);
			this.sentry = sentryIn;
		}
		
		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			return this.sentry.isAwake() && super.canUse();
		}
		
		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean canContinueToUse() {
			return this.sentry.isAwake() && super.canContinueToUse();
		}
		
	}
	
	public static class FaceRandomGoal extends SlimeEntity.FaceRandomGoal {
		private final SentryEntity sentry;
		
		public FaceRandomGoal(SentryEntity sentryIn) {
			super(sentryIn);
			this.sentry = sentryIn;
		}
		
		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			return this.sentry.isAwake() && super.canUse();
		}
		
		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean canContinueToUse() {
			return this.sentry.isAwake() && super.canContinueToUse();
		}
		
	}
	
	public static class FloatGoal extends SlimeEntity.FloatGoal {
		private final SentryEntity sentry;
		
		public FloatGoal(SentryEntity sentryIn) {
			super(sentryIn);
			this.sentry = sentryIn;
		}
		
		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			return this.sentry.isAwake() && super.canUse();
		}
		
		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean canContinueToUse() {
			return this.sentry.isAwake() && super.canContinueToUse();
		}
		
	}
	
	public static class HopGoal extends SlimeEntity.HopGoal {
		private final SentryEntity sentry;
		
		public HopGoal(SentryEntity sentryIn) {
			super(sentryIn);
			this.sentry = sentryIn;
		}
		
		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean canUse() {
			return this.sentry.isAwake() && super.canUse();
		}
		
		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean canContinueToUse() {
			return this.sentry.isAwake() && super.canContinueToUse();
		}
		
	}
	
}
