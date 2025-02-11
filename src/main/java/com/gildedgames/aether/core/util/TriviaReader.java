package com.gildedgames.aether.core.util;

import com.gildedgames.aether.Aether;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class TriviaReader
{
    private static final Random random = new Random();

    public static ITextComponent getTriviaLine() {
        String localization = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        if (lineFromLocalization(localization) != null) {
            return lineFromLocalization(localization);
        } else if (lineFromLocalization("en_us") != null) {
            return lineFromLocalization("en_us");
        } else {
            return null;
        }
    }

    public static ITextComponent lineFromLocalization(String localization) {
        IResource resource = null;
        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(Aether.MODID, "texts/trivia/" + localization + ".txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            List<String> list = Lists.newArrayList();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    list.add(line);
                }
            }
            if (!list.isEmpty()) {
                return new TranslationTextComponent("gui.aether.pro_tip").append(new StringTextComponent(" " + list.get(random.nextInt(list.size()))));
            }

        } catch (IOException ignore) { }
        finally {
            IOUtils.closeQuietly(resource);
        }
        return null;
    }
}
