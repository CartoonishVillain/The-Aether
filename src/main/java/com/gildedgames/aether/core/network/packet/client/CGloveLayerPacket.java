package com.gildedgames.aether.core.network.packet.client;

import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import com.gildedgames.aether.core.network.IAetherPacket.AetherPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class CGloveLayerPacket extends AetherPacket
{
    private final int playerID;
    private final boolean hatGloves;

    public CGloveLayerPacket(int playerID, boolean hatGloves) {
        this.playerID = playerID;
        this.hatGloves = hatGloves;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(this.playerID);
        buf.writeBoolean(this.hatGloves);
    }

    public static CGloveLayerPacket decode(PacketBuffer buf) {
        int playerID = buf.readInt();
        boolean hatGloves = buf.readBoolean();
        return new CGloveLayerPacket(playerID, hatGloves);
    }

    @Override
    public void execute(PlayerEntity playerEntity) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level != null) {
            Entity entity = Minecraft.getInstance().player.level.getEntity(this.playerID);
            if (entity instanceof PlayerEntity) {
                IAetherRankings.get((PlayerEntity) entity).ifPresent(aetherRankings -> aetherRankings.setHatGloves(this.hatGloves));
            }
        }
    }
}
