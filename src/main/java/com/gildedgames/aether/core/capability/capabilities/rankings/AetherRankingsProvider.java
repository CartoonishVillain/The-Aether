package com.gildedgames.aether.core.capability.capabilities.rankings;

import com.gildedgames.aether.core.capability.AetherCapabilities;
import com.gildedgames.aether.core.capability.interfaces.IAetherPlayer;
import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class AetherRankingsProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT>
{
    private final IAetherRankings aetherRankings;

    public AetherRankingsProvider(IAetherRankings aetherRankings) {
        this.aetherRankings = aetherRankings;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.aetherRankings.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.aetherRankings.deserializeNBT(nbt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == AetherCapabilities.AETHER_RANKINGS_CAPABILITY) {
            return LazyOptional.of(() -> (T) this.aetherRankings);
        }
        return LazyOptional.empty();
    }
}
