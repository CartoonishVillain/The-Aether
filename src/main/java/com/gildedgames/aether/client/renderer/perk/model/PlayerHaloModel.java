package com.gildedgames.aether.client.renderer.perk.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerHaloModel extends SegmentedModel<PlayerEntity>
{
    public ModelRenderer main;

    public PlayerHaloModel() {
        super(RenderType::entityTranslucent);
        this.texWidth = 16;
        this.texHeight = 16;
        this.main = new ModelRenderer(this);
        this.main.texOffs(0, 0).addBox(-2.0F, -10.0F, 2.0F, 4.0F, 1.0F, 1.0F, 0.0F);
        this.main.texOffs(0, 0).addBox(-2.0F, -10.0F, -3.0F, 4.0F, 1.0F, 1.0F, 0.0F);
        this.main.texOffs(0, 0).addBox(-3.0F, -10.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F);
        this.main.texOffs(0, 0).addBox(2.0F, -10.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F);
        this.main.setPos(0.0F, 0.0F, 0.0F);
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(this.main);
    }

    @Override
    public void setupAnim(PlayerEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) { }
}
