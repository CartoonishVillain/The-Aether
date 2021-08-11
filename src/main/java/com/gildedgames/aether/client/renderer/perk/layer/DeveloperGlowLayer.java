package com.gildedgames.aether.client.renderer.perk.layer;

import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import com.gildedgames.aether.core.registry.AetherPlayerRankings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class DeveloperGlowLayer<T extends PlayerEntity, M extends PlayerModel<T>, A extends PlayerModel<T>> extends LayerRenderer<T, M>
{
    private final A glowModel;

    public DeveloperGlowLayer(IEntityRenderer<T, M> renderer, A glowModel) {
        super(renderer);
        this.glowModel = glowModel;
    }

    @Override
    public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (p_225628_4_ instanceof ClientPlayerEntity) {
            ClientPlayerEntity player = (ClientPlayerEntity) p_225628_4_;
            IAetherRankings.get(player).ifPresent(aetherRankings -> {
                if (AetherPlayerRankings.hasDevGlow(player.getUUID())) {
                    ResourceLocation texture = player.getSkinTextureLocation();
                    this.getParentModel().copyPropertiesTo(this.glowModel);
                    this.glowModel.prepareMobModel(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_);
                    this.glowModel.setupAnim(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
                    IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.eyes(texture));
                    this.glowModel.renderToBuffer(p_225628_1_, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            });
        }
    }
}
