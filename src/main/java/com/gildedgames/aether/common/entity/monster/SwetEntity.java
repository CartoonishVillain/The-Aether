package com.gildedgames.aether.common.entity.monster;

import com.gildedgames.aether.client.registry.AetherSoundEvents;
import com.gildedgames.aether.common.entity.passive.MountableEntity;
import com.gildedgames.aether.common.registry.AetherItems;
import com.gildedgames.aether.common.registry.AetherLoot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;

public class SwetEntity extends MountableEntity {
    public static final EntityDataAccessor<Byte> SWET_TYPE = SynchedEntityData.defineId(SwetEntity.class, EntityDataSerializers.BYTE); // 1 for blue swet, 0 for golden swet

    public static final EntityDataAccessor<Boolean> MID_JUMP = SynchedEntityData.defineId(SwetEntity.class, EntityDataSerializers.BOOLEAN);

    public boolean wasOnGround;
    public boolean midJump;
    public int jumpTimer;
    public float swetHeight = 1.0F;
    public float swetWidth = 1.0F;

    public SwetEntity(EntityType<? extends SwetEntity> type, Level level) {
        super(type, level);
        this.moveControl = new SwetEntity.MoveHelperController(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ConsumeGoal(this));
        this.goalSelector.addGoal(1, new HuntGoal(this));
        this.goalSelector.addGoal(2, new RandomFacingGoal(this));
        this.goalSelector.addGoal(4, new HopGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 25.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWET_TYPE, (byte)1);
        this.entityData.define(MID_JUMP, false);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setSwetType((byte)this.random.nextInt(2));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.getTarget() != null) {
            if (this.isFriendlyTowardEntity(this.getTarget()) || this.isFriendly()) {
                this.setTarget(null);
            }
        }
    }

    @Override
    public void tick() {
        if(this.isInWater()) {
            this.dissolveSwet();
        }

        super.tick();

        if(!this.hasPrey()) {
            for (int i = 0; i < 5; i++) {
                double d = (float) this.getX() + (this.random.nextFloat() - this.random.nextFloat()) * 0.3F;
                double d1 = (float) this.getY() + this.getBbHeight();
                double d2 = (float) this.getZ() + (this.random.nextFloat() - this.random.nextFloat()) * 0.3F;
                this.level.addParticle(ParticleTypes.SPLASH, d, d1 - 0.25D, d2, 0.0D, 0.0D, 0.0D);
            }
        }

        if(this.onGround && !this.wasOnGround) {
            this.playSound(AetherSoundEvents.ENTITY_SWET_SQUISH.get(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
        }
        this.setMidJump(!this.onGround);
        if(!this.level.isClientSide) {
        } else {
            if (this.getMidJump()) {
                this.jumpTimer++;
            } else {
                this.jumpTimer = 0;
            }
            if (this.onGround) {
                this.swetHeight = this.swetHeight < 1.0F ? this.swetHeight += 0.25F : 1.0F;
                this.swetWidth = this.swetWidth > 1.0F ? this.swetWidth -= 0.25F : 1.0F;
            } else {
                this.swetHeight = 1.425F;
                this.swetWidth = 0.875F;

                if (this.getJumpTimer() > 3) {
                    float scale = Math.min(this.getJumpTimer(), 10);
                    this.swetHeight -= 0.05F * scale;
                    this.swetWidth += 0.05F * scale;
                }
            }
        }

        if(this.isFriendly())
            this.fallDistance = 0;

        this.wasOnGround = this.onGround;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level.isClientSide) {
            if (!this.hasPrey() && this.isFriendlyTowardEntity(player)) {
                this.capturePrey(player);
            }
        }

        return InteractionResult.PASS;
    }

    public void capturePrey(LivingEntity entity) {
        this.playSound(AetherSoundEvents.ENTITY_SWET_ATTACK.get(), 0.5F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xRotO = this.getXRot();
        this.setXRot(entity.getXRot());
        this.yRotO = this.getYRot();
        this.setYRot(entity.getYRot());
        this.setDeltaMovement(entity.getDeltaMovement());

        this.setPos(entity.getX(), entity.getY() + 0.01, entity.getZ());

        entity.startRiding(this);

        this.setXRot(this.random.nextFloat() * 360F);
    }

    public boolean hasPrey() {
        return this.isVehicle() && this.getPassengers().get(0) != null;
    }

    public boolean isFriendlyTowardEntity(LivingEntity entity) {
        Optional<ImmutableTriple<String, Integer, ItemStack>> swetCape = CuriosApi.getCuriosHelper().findEquippedCurio(AetherItems.SWET_CAPE.get(), entity);
        return swetCape.isPresent();
    }

    public void dissolveSwet() {
        for (int i = 0; i < 50; i++) {
            float f = this.random.nextFloat() * 3.141593F * 2.0F;
            float f1 = this.random.nextFloat() * 0.5F + 0.25F;
            float f2 = Mth.sin(f) * f1;
            float f3 = Mth.cos(f) * f1;

            this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double) f2, this.getBoundingBox().minY + 1.25D, this.getZ() + (double) f3, (double) f2 * 1.5D + this.getDeltaMovement().x, 4D, (double) f3 * 1.5D + this.getDeltaMovement().z);
        }

        if (this.getDeathSound() != null) this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getVoicePitch());

        this.discard();
    }

    public boolean isFriendly() {
        return this.hasPrey() && this.getPassengers().get(0) instanceof LivingEntity && isFriendlyTowardEntity((LivingEntity) this.getPassengers().get(0));
    }

    public int getJumpDelay() {
        if (this.isFriendly()) {
            return 2;
        } else {
            return this.random.nextInt(20) + 10;
        }
    }

    private void setSwetType(byte type) {
        this.entityData.set(SWET_TYPE, type);
    }

    public byte getSwetType() {
        return this.entityData.get(SWET_TYPE);
    }

    public void setMidJump(boolean flag) {
        this.entityData.set(MID_JUMP, flag);
    }

    public boolean getMidJump() {
        return this.entityData.get(MID_JUMP);
    }

    public int getJumpTimer() {
        return jumpTimer;
    }

    @Override
    public boolean canJump() {
        return this.isOnGround();
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.isFriendly();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return AetherSoundEvents.ENTITY_SWET_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AetherSoundEvents.ENTITY_SWET_DEATH.get();
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    protected boolean isSlime() {
        return true;
    }

    @Override
    public float getSteeringSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.28F;
    }

    @Override
    protected double getMountJumpStrength() {
        return 1.0D;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("SwetType", this.getSwetType());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSwetType(compound.getByte("SwetType"));
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return this.getSwetType() == 1 ? AetherLoot.ENTITIES_SWET_BLUE : AetherLoot.ENTITIES_SWET_GOLD;
    }

    static class ConsumeGoal extends Goal {
        private final SwetEntity swet;
        private int jumps = 0;

        private float chosenDegrees = 0;

        public ConsumeGoal(SwetEntity entity) {
            this.swet = entity;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.swet.hasPrey() && this.swet.getPassengers().get(0) instanceof LivingEntity && !this.swet.isFriendlyTowardEntity((LivingEntity) this.swet.getPassengers().get(0));
        }

        @Override
        public void tick() {
            if (this.jumps <= 3) {
                if (this.swet.onGround) {
                    this.swet.playSound(AetherSoundEvents.ENTITY_SWET_JUMP.get(), 1.0F, ((this.swet.getRandom().nextFloat() - this.swet.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);

                    chosenDegrees = (float)this.swet.getRandom().nextInt(360);

                    if (this.jumps == 0) {
                        this.swet.setDeltaMovement(swet.getDeltaMovement().add(0, 0.64999999403953552D, 0));
                    } else if (this.jumps == 1) {
                        this.swet.setDeltaMovement(swet.getDeltaMovement().add(0, 0.74999998807907104D, 0));
                    } else if (this.jumps == 2) {
                        this.swet.setDeltaMovement(swet.getDeltaMovement().add(0, 1.55D, 0));
                    } else {
                        this.swet.getPassengers().get(0).stopRiding();
                        this.swet.dissolveSwet();
                    }

                    if (!this.swet.getMidJump()) {
                        this.jumps++;
                    }
                }

                if (!this.swet.wasOnGround) {
                    if (this.swet.getJumpTimer() < 6) {
                        if (this.jumps == 1) {
                            this.moveHorizontal(0.0F, 0.2F, chosenDegrees);
                        } else if (this.jumps == 2) {
                            this.moveHorizontal(0.0F, 0.3F, chosenDegrees);
                        } else if (this.jumps == 3) {
                            this.moveHorizontal(0.0F, 0.6F, chosenDegrees);
                        }
                    }
                }
            }
        }

        public void moveHorizontal(float strafe, float forward, float rotation) {
            float f = strafe * strafe + forward * forward;

            f = Mth.sqrt(f);
            if (f < 1.0F) f = 1.0F;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = Mth.sin(rotation * 0.017453292F);
            float f2 = Mth.cos(rotation * 0.017453292F);

            this.swet.setDeltaMovement((strafe * f2 - forward * f1), swet.getDeltaMovement().y, (forward * f2 + strafe * f1));
        }
    }

    static class HuntGoal extends Goal {
        private final SwetEntity swet;

        public HuntGoal(SwetEntity entity) {
            this.swet = entity;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.swet.getTarget();
            if (swet.hasPrey() || target == null || !target.isAlive() || this.swet.isFriendlyTowardEntity(target) || (target instanceof Player && ((Player)target).getAbilities().invulnerable)) {
                return false;
            } else {
                return this.swet.getMoveControl() instanceof SwetEntity.MoveHelperController;
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.swet.getTarget();
            if (swet.hasPrey() || target == null || !target.isAlive()) {
                return false;
            } else if (target instanceof Player && ((Player)target).getAbilities().invulnerable) {
                return false;
            } else {
                return !this.swet.isFriendlyTowardEntity(target);
            }
        }

        @Override
        public void tick() {
            LivingEntity target = this.swet.getTarget();
            if(target != null) {
                this.swet.lookAt(target, 10.0F, 10.0F);
                ((SwetEntity.MoveHelperController)this.swet.getMoveControl()).setDirection(this.swet.getYRot(), true);
                if(swet.getBoundingBox().intersects(target.getBoundingBox())) {
                    swet.capturePrey(target);
                }
            }
        }
    }

    /**
     * Classes down here are based off of the AI classes used in SlimeEntity.
     * @see net.minecraft.world.entity.monster.Slime
     */
    static class RandomFacingGoal extends Goal {
        private final SwetEntity swet;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public RandomFacingGoal(SwetEntity swetEntity) {
            this.swet = swetEntity;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.swet.getTarget() == null && (this.swet.onGround || this.swet.isInWater() || this.swet.isInLava() || this.swet.hasEffect(MobEffects.LEVITATION)) && this.swet.getMoveControl() instanceof MoveHelperController;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = 40 + this.swet.getRandom().nextInt(60);
                this.chosenDegrees = (float)this.swet.getRandom().nextInt(360);
            }

            ((MoveHelperController)this.swet.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

    static class HopGoal extends Goal {
        private final SwetEntity swet;

        public HopGoal(SwetEntity swetEntity) {
            this.swet = swetEntity;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !this.swet.isPassenger();
        }

        public void tick() {
            ((MoveHelperController)this.swet.getMoveControl()).setWantedMovement(1.0D);
        }
    }

    static class MoveHelperController extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final SwetEntity swet;
        private boolean isAggressive;

        public MoveHelperController(SwetEntity swet) {
            super(swet);
            this.swet = swet;
            this.yRot = 180.0F * swet.getYRot() / (float)Math.PI;
        }

        public void setDirection(float yRotIn, boolean isAggressiveIn) {
            this.yRot = yRotIn;
            this.isAggressive = isAggressiveIn;
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.isOnGround()) {
                    boolean flag = true;

                    if (this.swet.isFriendly()) {
                        if (this.swet.getDeltaMovement().x == 0 || this.swet.getDeltaMovement().z == 0) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));

                        if (this.jumpDelay-- <= 0) {
                            this.jumpDelay = this.swet.getJumpDelay();

                            if (this.isAggressive) {
                                this.jumpDelay /= 3;
                            }

                            this.swet.getJumpControl().jump();

                            this.swet.playSound(AetherSoundEvents.ENTITY_SWET_JUMP.get(), 1.0F, ((this.swet.random.nextFloat() - this.swet.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        } else {
                            this.swet.xxa = 0.0F;
                            this.swet.zza = 0.0F;
                            this.mob.setSpeed(0.0F);
                        }
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }
}