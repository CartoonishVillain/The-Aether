package com.gildedgames.aether.client.renderer.entity;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.client.renderer.entity.model.AerbunnyModel;
import com.gildedgames.aether.common.entity.passive.AerbunnyEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class AerbunnyRenderer extends MobRenderer<AerbunnyEntity, AerbunnyModel>
{
    private static final ResourceLocation AERBUNNY_TEXTURE = new ResourceLocation(Aether.MODID, "textures/entity/mobs/aerbunny/aerbunny.png");

    public AerbunnyRenderer(EntityRendererManager manager) {
        super(manager, new AerbunnyModel(), 0.3F);
    }

    @Override
    protected void scale(AerbunnyEntity aerbunny, MatrixStack matrixStack, float scale) {
        matrixStack.translate(0, 0.2D, 0);
        if (aerbunny.isBaby()) {
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    protected void setupRotations(AerbunnyEntity aerbunny, MatrixStack matrixStack, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
        if (!aerbunny.isOnGround()) {
            if (aerbunny.getDeltaMovement().y > 0.5D) {
                matrixStack.mulPose(Vector3f.XN.rotationDegrees(15.0F));
            }
            else if (aerbunny.getDeltaMovement().y < -0.5D) {
                matrixStack.mulPose(Vector3f.XN.rotationDegrees(-15.0F));
            }
            else {
                matrixStack.mulPose(Vector3f.XN.rotationDegrees((float)(aerbunny.getDeltaMovement().y * 30.0D)));
            }
        }
        super.setupRotations(aerbunny, matrixStack, p_225621_3_, p_225621_4_, p_225621_5_);
    }

    @Override
    public ResourceLocation getTextureLocation(AerbunnyEntity p_110775_1_) {
        return AERBUNNY_TEXTURE;
    }
}
