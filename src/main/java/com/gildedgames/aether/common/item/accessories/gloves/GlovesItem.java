package com.gildedgames.aether.common.item.accessories.gloves;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.client.registry.AetherSoundEvents;
import com.gildedgames.aether.client.renderer.accessory.model.GlovesModel;
import com.gildedgames.aether.common.item.accessories.AccessoryItem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class GlovesItem extends AccessoryItem
{
    protected double damage;
    protected ResourceLocation GLOVES_TEXTURE;
    protected ResourceLocation GLOVES_SLIM_TEXTURE;
    protected final Supplier<SoundEvent> equipSound;

    public GlovesItem(double punchDamage, String glovesName, Supplier<SoundEvent> glovesSound, Properties properties) {
        super(properties);
        this.damage = punchDamage;
        this.setRenderTexture(Aether.MODID, glovesName);
        this.equipSound = glovesSound;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Gloves Damage Bonus", this.damage, AttributeModifier.Operation.ADDITION));
        return attributes;
    }

    @Nonnull
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(this.equipSound.get(), 1.0f, 1.0f);
    }

//    @Override
//    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
//        return true;
//    }
//
//    @Override
//    public void renderModel(HumanoidModel<?> model, String identifier, int index, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
//        GlovesModel gloves;
//        VertexConsumer vertexBuilder;
//
//        if (!(model instanceof PlayerModel<?>)) {
//            gloves = new GlovesModel(false);
//            vertexBuilder = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(this.getGlovesTexture()), false, stack.isEnchanted());
//        }
//        else {
//            PlayerModel<?> playerModel = (PlayerModel<?>) model;
//            gloves = new GlovesModel(playerModel.slim);
//            vertexBuilder = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(playerModel.slim ? this.getGlovesSlimTexture() : this.getGlovesTexture()), false, stack.isEnchanted());
//        }
//
//        ICurio.RenderHelper.followBodyRotations(livingEntity, gloves);
//
//        gloves.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//    }

    public void setRenderTexture(String modId, String registryName) {
        this.GLOVES_TEXTURE = new ResourceLocation(modId, "textures/models/accessory/gloves/" + registryName + "_accessory.png");
        this.GLOVES_SLIM_TEXTURE = new ResourceLocation(modId, "textures/models/accessory/gloves/" + registryName + "_slim_accessory.png");
    }

    public ResourceLocation getGlovesTexture() {
        return this.GLOVES_TEXTURE;
    }

    public ResourceLocation getGlovesSlimTexture() {
        return this.GLOVES_SLIM_TEXTURE;
    }
}