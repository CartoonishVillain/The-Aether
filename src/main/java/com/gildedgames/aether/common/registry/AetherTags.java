package com.gildedgames.aether.common.registry;

import com.gildedgames.aether.Aether;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;

public class AetherTags
{
	public static class Blocks
	{
		public static final ITag.INamedTag<Block> AETHER_PORTAL_BLOCKS = tag("aether_portal_blocks");
		public static final ITag.INamedTag<Block> AETHER_ISLAND_BLOCKS = tag("aether_island_blocks");
		public static final ITag.INamedTag<Block> ENCHANTABLE_GRASS_BLOCKS = tag("enchantable_grass_blocks");
		public static final ITag.INamedTag<Block> AETHER_DIRT = tag("aether_dirt");
		public static final ITag.INamedTag<Block> HOLYSTONE = tag("holystone");
		public static final ITag.INamedTag<Block> AERCLOUDS = tag("aerclouds");
		public static final ITag.INamedTag<Block> SKYROOT_LOGS = tag("skyroot_logs");
		public static final ITag.INamedTag<Block> GOLDEN_OAK_LOGS = tag("golden_oak_logs");
		public static final ITag.INamedTag<Block> AEROGEL = tag("aerogel");
		public static final ITag.INamedTag<Block> DUNGEON_BLOCKS = tag("dungeon_blocks");
		public static final ITag.INamedTag<Block> LOCKED_DUNGEON_BLOCKS = tag("locked_dungeon_blocks");
		public static final ITag.INamedTag<Block> TRAPPED_DUNGEON_BLOCKS = tag("trapped_dungeon_blocks");

		private static ITag.INamedTag<Block> tag(String name) {
			return BlockTags.bind(new ResourceLocation(Aether.MODID, name).toString());
		}
	}
	
	public static class Items
	{
		public static final ITag.INamedTag<Item> AETHER_DIRT = tag("aether_dirt");
		public static final ITag.INamedTag<Item> HOLYSTONE = tag("holystone");
		public static final ITag.INamedTag<Item> AERCLOUDS = tag("aerclouds");
		public static final ITag.INamedTag<Item> SKYROOT_LOGS = tag("skyroot_logs");
		public static final ITag.INamedTag<Item> GOLDEN_OAK_LOGS = tag("golden_oak_logs");
		public static final ITag.INamedTag<Item> AEROGEL = tag("aerogel");
		public static final ITag.INamedTag<Item> DUNGEON_BLOCKS = tag("dungeon_blocks");
		public static final ITag.INamedTag<Item> LOCKED_DUNGEON_BLOCKS = tag("locked_dungeon_blocks");
		public static final ITag.INamedTag<Item> TRAPPED_DUNGEON_BLOCKS = tag("trapped_dungeon_blocks");

		public static final ITag.INamedTag<Item> PLANKS_CRAFTING = tag("planks_crafting");
		public static final ITag.INamedTag<Item> STONE_CRAFTING = tag("stone_crafting");

		public static final ITag.INamedTag<Item> BANNED_IN_AETHER = tag("banned_in_aether");
		public static final ITag.INamedTag<Item> AETHER_PORTAL_ACTIVATION_ITEMS = tag("aether_portal_activation_items");
		public static final ITag.INamedTag<Item> BOOK_OF_LORE_MATERIALS = tag("book_of_lore_materials");
		public static final ITag.INamedTag<Item> SKYROOT_STICKS = tag("skyroot_stick");
		public static final ITag.INamedTag<Item> SKYROOT_TOOLS = tag("skyroot_tools");
		public static final ITag.INamedTag<Item> HOLYSTONE_TOOLS = tag("holystone_tools");
		public static final ITag.INamedTag<Item> ZANITE_TOOLS = tag("zanite_tools");
		public static final ITag.INamedTag<Item> GRAVITITE_TOOLS = tag("gravitite_tools");
		public static final ITag.INamedTag<Item> VALKYRIE_TOOLS = tag("valkyrie_tools");
		public static final ITag.INamedTag<Item> GOLDEN_AMBER_HARVESTERS = tag("golden_amber_harvesters");
		public static final ITag.INamedTag<Item> NO_SKYROOT_DOUBLE_DROPS = tag("no_skyroot_double_drops");
		public static final ITag.INamedTag<Item> PIG_DROPS = tag("pig_drops");
		public static final ITag.INamedTag<Item> DUNGEON_KEYS = tag("dungeon_keys");
		public static final ITag.INamedTag<Item> ACCEPTED_MUSIC_DISCS = tag("accepted_music_discs");
		public static final ITag.INamedTag<Item> SAVE_NBT_IN_RECIPE = tag("save_nbt_in_recipe");

		private static ITag.INamedTag<Item> tag(String name) {
			return ItemTags.bind(new ResourceLocation(Aether.MODID, name).toString());
		}
	}

	public static class Entities
	{
		public static final ITag.INamedTag<EntityType<?>> PIGS = tag("pigs");
		public static final ITag.INamedTag<EntityType<?>> NO_SKYROOT_DOUBLE_DROPS = tag("no_skyroot_double_drops");
		public static final ITag.INamedTag<EntityType<?>> DEFLECTABLE_PROJECTILES = tag("deflectable_projectiles");

		private static ITag.INamedTag<EntityType<?>> tag(String name) {
			return EntityTypeTags.bind(new ResourceLocation(Aether.MODID, name).toString());
		}
	}

	public static class Fluids
	{
		public static final ITag.INamedTag<Fluid> FREEZABLE_TO_AEROGEL = tag("freezable_to_aerogel");

		private static ITag.INamedTag<Fluid> tag(String name) {
			return FluidTags.bind(new ResourceLocation(Aether.MODID, name).toString());
		}
	}
}
