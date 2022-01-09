package com.gildedgames.aether.common.inventory.container;

import java.util.UUID;
import java.util.function.Consumer;

import com.gildedgames.aether.common.entity.tile.IncubatorTileEntity;

import com.gildedgames.aether.common.inventory.container.slot.IncubatorEggSlot;
import com.gildedgames.aether.common.inventory.container.slot.IncubatorFuelSlot;
import com.gildedgames.aether.common.registry.AetherContainerTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IncubatorContainer extends AbstractContainerMenu {
	public final Container incubatorInventory;
	public final ContainerData incubatorData;
	public final Consumer<UUID> playerUUIDAcceptor;
	public final Level world;

	public IncubatorContainer(int id, Inventory playerInventoryIn) {
		this(id, playerInventoryIn, new SimpleContainer(2), new SimpleContainerData(3), (uuid) -> {});
	}
	
	public IncubatorContainer(int id, Inventory playerInventoryIn, Container incubatorInventoryIn, ContainerData incubatorDataIn, Consumer<UUID> playerUUIDConsumerIn) {
		super(AetherContainerTypes.INCUBATOR.get(), id);
		checkContainerSize(incubatorInventoryIn, 2);
		checkContainerDataCount(incubatorDataIn, 3);
		this.incubatorInventory = incubatorInventoryIn;
		this.incubatorData = incubatorDataIn;
		this.playerUUIDAcceptor = playerUUIDConsumerIn;
		this.world = playerInventoryIn.player.level;
		this.addSlot(new IncubatorEggSlot(this, incubatorInventoryIn, 0, 73, 17, playerInventoryIn.player));
		this.addSlot(new IncubatorFuelSlot(this, incubatorInventoryIn, 0, 73, 53));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
		}

		this.addDataSlots(incubatorDataIn);
	}
	
	public void clear() {
		this.incubatorInventory.clearContent();
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return incubatorInventory.stillValid(playerIn);
	}
	
	public boolean isFuel(ItemStack stack) {
		return IncubatorTileEntity.isFuel(stack);
	}
	
	public boolean isEgg(ItemStack stack) {
		return true;
		//return stack.getItem() == AetherItems.MOA_EGG;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index != 1 && index != 0) {
				if (this.isFuel(itemstack1)) {
					if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (this.isEgg(itemstack1)) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (index >= 2 && index < 29) {
					if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (index >= 29 && index < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}
			else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getProgressionScaled() {
		int progress = this.incubatorData.get(0);
		int ticksRequired = this.incubatorData.get(2);
		return progress != 0 && ticksRequired != 0? progress * 24 / ticksRequired : 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean isIncubating() {
		return this.incubatorData.get(1) > 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getFreezingTimeRemaining() {
		return (this.incubatorData.get(1) * 12) / 500; 
	}

}
