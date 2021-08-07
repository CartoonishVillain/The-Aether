package com.gildedgames.aether.core.capability.capabilities.rankings;

import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class AetherRankingsStorage implements Capability.IStorage<IAetherRankings>
{
    @Override
    public INBT writeNBT(Capability<IAetherRankings> capability, IAetherRankings instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IAetherRankings> capability, IAetherRankings instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}
