package com.gildedgames.aether.core.network.packet.server;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import static com.gildedgames.aether.core.network.IAetherPacket.*;

public class SGloveLayerPacket extends AetherPacket
{
    private final int playerID;
    private final boolean hatGloves;

    public SGloveLayerPacket(int playerID, boolean hatGloves) {
        this.playerID = playerID;
        this.hatGloves = hatGloves;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(this.playerID);
        buf.writeBoolean(this.hatGloves);
    }

    public static SGloveLayerPacket decode(PacketBuffer buf) {
        int playerID = buf.readInt();
        boolean hatGloves = buf.readBoolean();
        return new SGloveLayerPacket(playerID, hatGloves);
    }

    @Override
    public void execute(PlayerEntity playerEntity) {
        if (playerEntity != null && playerEntity.level != null) {
            Entity entity = playerEntity.level.getEntity(this.playerID);
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                //Aether.LOGGER.info("server");
                IAetherRankings.get(player).ifPresent(aetherRankings -> aetherRankings.setHatGloves(this.hatGloves));
            }
            if (entity instanceof ClientPlayerEntity) {
                ClientPlayerEntity player = (ClientPlayerEntity) entity;
                Aether.LOGGER.info("client");
                IAetherRankings.get(player).ifPresent(aetherRankings -> aetherRankings.setHatGloves(this.hatGloves));
            }
        }
    }
}
