package com.gildedgames.aether.core.capability.capabilities.rankings;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import com.gildedgames.aether.core.network.AetherPacketHandler;
import com.gildedgames.aether.core.network.packet.client.CGloveLayerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class AetherRankings implements IAetherRankings
{
    private final PlayerEntity player;

    private boolean hatGloves;

    public AetherRankings(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("HatGloves", this.areHatGloves());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("HatGloves")) {
            this.hatGloves = nbt.getBoolean("HatGloves");
        }
    }

    @Override
    public void copyFrom(IAetherRankings other) {
        this.setHatGloves(other.areHatGloves());
    }

    @Override
    public void sync() {
        this.sendGloveLayerPacket(this.hatGloves);
    }

    @Override
    public void onUpdate() {
        Aether.LOGGER.info(this.areHatGloves());
        //Aether.LOGGER.info(AetherPlayerRankings.getPrimaryRankOf(this.getPlayer().getUUID()));
    }

    @Override
    public void setHatGloves(boolean areHatGloves) {
        this.sendGloveLayerPacket(areHatGloves);
        this.hatGloves = areHatGloves;
    }

    @Override
    public boolean areHatGloves() {
        return this.hatGloves;
    }

    private void sendGloveLayerPacket(boolean areHatGloves) {
        if (this.getPlayer() instanceof ServerPlayerEntity && !this.getPlayer().level.isClientSide) {
            AetherPacketHandler.sendToPlayer(new CGloveLayerPacket(this.getPlayer().getId(), areHatGloves), (ServerPlayerEntity) this.getPlayer());
        }
    }
}
