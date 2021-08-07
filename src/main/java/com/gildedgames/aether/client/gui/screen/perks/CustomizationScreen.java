package com.gildedgames.aether.client.gui.screen.perks;

import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import com.gildedgames.aether.core.network.AetherPacketHandler;
import com.gildedgames.aether.core.network.packet.server.SGloveLayerPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomizationScreen extends Screen
{
    private final Screen lastScreen;

    public CustomizationScreen(Screen screen) {
        super(new TranslationTextComponent("gui.aether.customization.title"));
        this.lastScreen = screen;
    }

    protected void init() {
        if (this.minecraft.player != null) {
            IAetherRankings.get(this.minecraft.player).ifPresent(aetherRankings -> {
                this.addButton(new Button(this.width / 2 - 75, this.height / 6, 150, 20,
                        new TranslationTextComponent(aetherRankings.areHatGloves() ? "gui.aether.customization.gloves.hat" : "gui.aether.customization.gloves.skin"),
                        (pressed) -> {
                            aetherRankings.setHatGloves(!aetherRankings.areHatGloves());
                            AetherPacketHandler.sendToServer(new SGloveLayerPacket(this.minecraft.player.getId(), aetherRankings.areHatGloves()));
                        },
                        (button, matrixStack, x, y) -> button.setMessage(new TranslationTextComponent(aetherRankings.areHatGloves() ? "gui.aether.customization.gloves.hat" : "gui.aether.customization.gloves.skin"))
                ));
            });
        }


        //Might move this up a bit depending on spacing.
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, DialogTexts.GUI_DONE, (pressed) -> this.minecraft.setScreen(this.lastScreen)));
    }

    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
