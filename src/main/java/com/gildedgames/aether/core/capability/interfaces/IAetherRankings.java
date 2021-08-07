package com.gildedgames.aether.core.capability.interfaces;

import com.gildedgames.aether.core.capability.AetherCapabilities;
import com.gildedgames.aether.core.capability.capabilities.rankings.AetherRankings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;
import java.util.stream.Collectors;

public interface IAetherRankings extends INBTSerializable<CompoundNBT>
{
    PlayerEntity getPlayer();

    static LazyOptional<IAetherRankings> get(PlayerEntity player) {
        return player.getCapability(AetherCapabilities.AETHER_RANKINGS_CAPABILITY);
    }

    void registerRankings();

    void onUpdate();

    Set<AetherRankings.PlayerRanking> getRanksOf(UUID uuid);

    AetherRankings.PlayerRanking getPrimaryRankOf(UUID uuid);

    boolean addRank(String uuidString, PlayerRanking rank);

    boolean addRank(UUID uuid, PlayerRanking rank);

    boolean addRank(String uuidString, PlayerRanking... ranksIn);

    boolean addRank(UUID uuid, PlayerRanking... rank);

    boolean removeRank(String uuidString, PlayerRanking rank);

    boolean removeRank(UUID uuid, PlayerRanking rank);

    boolean removeRank(String uuidString, PlayerRanking... ranksIn);

    boolean removeRank(UUID uuid, PlayerRanking... rank);

    enum PlayerRanking
    {
        NONE(0, "none", false, false);


    /*

	NONE(false, false),
	CONTRIBUTOR(true, false),
	TESTER(true, false),
	CELEBRITY(true, false),
	PATRON(true, false),
	MOJANG(true, true),
	RETIRED_GILDED_GAMES(true, false),
	RETIRED_DEVELOPER(true, false),
	GILDED_GAMES(true, true),
	DEVELOPER(true, true);
     */

        private static final PlayerRanking[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(PlayerRanking::getId)).toArray(PlayerRanking[]::new);
        private static final Map<String, PlayerRanking> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(PlayerRanking::getName, (playerRanking) -> playerRanking));

        private final int id;
        private final String name;
        public final boolean hasDevGlow, hasHalo;

        PlayerRanking(int id, String name, boolean hasDevGlow, boolean hasHalo) {
            this.id = id;
            this.name = name;
            this.hasHalo = hasHalo;
            this.hasDevGlow = hasDevGlow;
        }

        public static PlayerRanking byName(String name) {
            return BY_NAME.getOrDefault(name, NONE);
        }

        public static PlayerRanking byId(int id) {
            if (id < 0 || id > BY_ID.length) {
                id = 0;
            }
            return BY_ID[id];
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public boolean hasHalo() {
            return this.hasHalo;
        }

        public boolean hasDevGlow() {
            return this.hasDevGlow;
        }
    }
}
