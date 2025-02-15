package com.gildedgames.aether.common.item.combat.loot;

import com.gildedgames.aether.common.registry.AetherItemGroups;
import com.gildedgames.aether.common.registry.AetherItems;

import com.gildedgames.aether.common.registry.AetherTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class PigSlayerItem extends SwordItem
{
	public PigSlayerItem() {
		super(ItemTier.IRON, 3, -2.4f, new Item.Properties().durability(200).rarity(AetherItems.AETHER_LOOT).tab(AetherItemGroups.AETHER_WEAPONS));
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (AetherTags.Entities.PIGS.contains(target.getType())) {
			if (target.getHealth() > 0.0F) {
				target.hurt(DamageSource.mobAttack(attacker), 9999);
			}
			if (target.level instanceof ServerWorld) {
				ServerWorld world = (ServerWorld) target.level;
				for (int i = 0; i < 20; i++) {
					double d0 = world.getRandom().nextGaussian() * 0.02;
					double d1 = world.getRandom().nextGaussian() * 0.02;
					double d2 = world.getRandom().nextGaussian() * 0.02;
					double d3 = 5.0D;
					double x = target.getX() + (world.getRandom().nextFloat() * target.getBbWidth() * 2.0) - target.getBbWidth() - d0 * d3;
					double y = target.getY() + (world.getRandom().nextFloat() * target.getBbHeight()) - d1 * d3;
					double z = target.getZ() + (world.getRandom().nextFloat() * target.getBbWidth() * 2.0) - target.getBbWidth() - d2 * d3;
					world.sendParticles(ParticleTypes.FLAME, x, y, z, 1, d0, d1, d2, 0.0F);
				}
			}
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@SubscribeEvent
	public static void doPigSlayerDrops(LivingDropsEvent event) {
		if (event.getSource() instanceof EntityDamageSource) {
			LivingEntity entity = event.getEntityLiving();
			EntityDamageSource source = (EntityDamageSource) event.getSource();
			if (source.getDirectEntity() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) source.getDirectEntity();
				ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
				Item item = stack.getItem();
				if (item == AetherItems.PIG_SLAYER.get() && entity.getType().is(AetherTags.Entities.PIGS)) {
					if (entity.getRandom().nextInt(4) == 0) {
						ArrayList<ItemEntity> newDrops = new ArrayList<>(event.getDrops().size());
						for (ItemEntity drop : event.getDrops()) {
							ItemStack droppedStack = drop.getItem();
							if (droppedStack.getItem().is(AetherTags.Items.PIG_DROPS)) {
								ItemEntity dropEntity = new ItemEntity(entity.level, drop.getX(), drop.getY(), drop.getZ(), droppedStack.copy());
								dropEntity.setDefaultPickUpDelay();
								newDrops.add(dropEntity);
							}
						}
						event.getDrops().addAll(newDrops);
					}
				}
			}
		}
	}
}
