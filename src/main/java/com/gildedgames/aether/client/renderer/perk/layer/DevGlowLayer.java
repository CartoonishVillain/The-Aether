package com.gildedgames.aether.client.renderer.perk.layer;

import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import com.gildedgames.aether.core.registry.AetherPlayerRankings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class DevGlowLayer<T extends PlayerEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M>
{
    private final A glowModel;

    public DevGlowLayer(IEntityRenderer<T, M> renderer, A glowModel) {
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
                    p_225628_1_.pushPose();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                    IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(p_225628_2_, RenderType.entityTranslucent(texture), false, false);
                    this.glowModel.renderToBuffer(p_225628_1_, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                    p_225628_1_.popPose();
                    /*

			this.manager.renderEngine.bindTexture(player.getLocationSkin());
			GlStateManager.enableBlend();
			GlStateManager.pushMatrix();

	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        GlStateManager.enableNormalize();
	        GlStateManager.enableBlend();
	        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

	        this.modelGlow.render(player, limbSwing, prevLimbSwing, partialTicks, interpolateRotation, prevRotationPitch, scale);

	        GlStateManager.disableBlend();
	        GlStateManager.disableNormalize();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
                     */
                }
            });
        }
    }
}
