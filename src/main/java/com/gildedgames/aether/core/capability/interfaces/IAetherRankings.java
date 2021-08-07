package com.gildedgames.aether.core.capability.interfaces;

import com.gildedgames.aether.core.capability.AetherCapabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface IAetherRankings extends INBTSerializable<CompoundNBT>
{
    PlayerEntity getPlayer();

    static LazyOptional<IAetherRankings> get(PlayerEntity player) {
        return player.getCapability(AetherCapabilities.AETHER_RANKINGS_CAPABILITY);
    }

    void copyFrom(IAetherRankings other);

    void sync();

    void onUpdate();

    void setHatGloves(boolean areHatGloves);
    boolean areHatGloves();
}
