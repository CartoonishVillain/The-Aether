package com.gildedgames.aether.core.capability.capabilities.player;

import com.gildedgames.aether.core.capability.AetherCapabilities;
import com.gildedgames.aether.core.capability.interfaces.IAetherPlayer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class AetherPlayerProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT>
{
	private final IAetherPlayer aetherPlayer;
	
	public AetherPlayerProvider(IAetherPlayer aetherPlayer) {
		this.aetherPlayer = aetherPlayer;
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		return this.aetherPlayer.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.aetherPlayer.deserializeNBT(nbt);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == AetherCapabilities.AETHER_PLAYER_CAPABILITY) {
			return LazyOptional.of(() -> (T) this.aetherPlayer);
		}
		return LazyOptional.empty();
	}
}
