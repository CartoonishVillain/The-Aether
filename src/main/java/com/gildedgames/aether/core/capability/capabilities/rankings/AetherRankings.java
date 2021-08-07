package com.gildedgames.aether.core.capability.capabilities.rankings;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.core.capability.interfaces.IAetherRankings;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.*;

public class AetherRankings implements IAetherRankings
{
    private final PlayerEntity player;

    private Map<UUID, Set<PlayerRanking>> ranks;

    public AetherRankings(PlayerEntity player) {
        this.player = player;
        this.registerRankings();
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public void registerRankings() {

			/* * * * * * * * * * * * * *\
			 * THIS LIST IS INCOMPLETE
			\* * * * * * * * * * * * * */


//        event.addRank("c3e6871e-8e60-490a-8a8d-2bbe35ad1604", AetherRank.DEVELOPER, AetherRank.GILDED_GAMES); // Raptor
//        event.addRank("58a5d694-a8a6-4605-ab33-d6904107ad5f", AetherRank.DEVELOPER, AetherRank.GILDED_GAMES); // bconlon
//        event.addRank("353a859b-ba16-4e6a-8f63-9a8c79ab0071", AetherRank.DEVELOPER, AetherRank.GILDED_GAMES); // quek
//        event.addRank("3c0e4411-3421-40bd-b092-056d3e99b98a", AetherRank.DEVELOPER, AetherRank.GILDED_GAMES); // Oscar Payne
//
//        event.addRank("6e8be0ba-e4bb-46af-aea8-2c1f5eec5bc2", AetherRank.RETIRED_DEVELOPER); // Brendan Freeman
//        event.addRank("5f112332-0993-4f52-a5ab-9a55dc3173cb", AetherRank.RETIRED_DEVELOPER); // JorgeQ
//        event.addRank("6a0e8505-1556-4ee9-bec0-6af32f05888d", AetherRank.RETIRED_DEVELOPER); // 115kino
//        event.addRank("1d680bb6-2a9a-4f25-bf2f-a1af74361d69", AetherRank.RETIRED_DEVELOPER); // Phygie
//
//        event.addRank("13655ac1-584d-4785-b227-650308195121", AetherRank.GILDED_GAMES, AetherRank.MOJANG); // Brandon Pearce
//        event.addRank("6fb2f965-6b57-46de-9ef3-0ef4c9b9bdc6", AetherRank.GILDED_GAMES); // Hugo Payn
//        event.addRank("dc4cf9b2-f601-4eb4-9436-2924836b9f42", AetherRank.GILDED_GAMES); // Jaryt Bustard
//        event.addRank("c0643897-c500-4f61-a62a-8051801562a9", AetherRank.GILDED_GAMES); // Christian Peterson
//
//        event.addRank("4bfb28a3-005d-4fc9-9238-a55c6c17b575", AetherRank.RETIRED_GILDED_GAMES); // Jon Lachney
//        event.addRank("2afd6a1d-1531-4985-a104-399c0c19351d", AetherRank.RETIRED_GILDED_GAMES); // Brandon Potts
//        event.addRank("b5ee3d5d-2ad7-4642-b9d4-6b041ad600a4", AetherRank.RETIRED_GILDED_GAMES); // Emile van Krieken
//        event.addRank("ffb94179-dd54-400d-9ece-834720cd7be9", AetherRank.RETIRED_GILDED_GAMES); // Collin Soares
//
//        event.addRank("853c80ef-3c37-49fd-aa49-938b674adae6", AetherRank.MOJANG); // Jens Bergensten
//        // https://namemc.com/cape/cb5dd34bee340182  for a list of mojang employees' game accounts, I don't wanna do all of them right now
//        // There has to be some way to automatically register these ^ because there are 184 profiles with the mojang cape.
//
//        event.addRank("5f820c39-5883-4392-b174-3125ac05e38c", AetherRank.CELEBRITY); // CaptainSparklez
//        event.addRank("0c063bfd-3521-413d-a766-50be1d71f00e", AetherRank.CELEBRITY); // AntVenom
//        event.addRank("c7da90d5-6a05-4217-b94a-7d427cbbcad8", AetherRank.CELEBRITY); // Mumbo Jumbo
//        event.addRank("5f8eb73b-25be-4c5a-a50f-d27d65e30ca0", AetherRank.CELEBRITY); // Grian
//
//        event.addRank("6f8be24f-03f3-4288-9218-16c9ecc08c8f", AetherRank.CONTRIBUTOR); // Jonathing
//        event.addRank("c15c4d6d-9a80-4d6b-9eda-770859b5ed91", AetherRank.CONTRIBUTOR); // Everett1999
//        event.addRank("4f0e8dd5-caf4-4d88-bfa4-1b0f1e13779f", AetherRank.CONTRIBUTOR); // Indianajaune
//        event.addRank("6c249311-f939-4e66-9f31-49b753bfb14b", AetherRank.CONTRIBUTOR); // InsomniaKitten
//        event.addRank("2b5187c9-dc5d-480e-ab6f-e884e92fce45", AetherRank.CONTRIBUTOR); // ItzDennisz
//			event.addRank("????????-????-????-????-????????????", CONTRIBUTOR); // Overspace
    }

    @Override
    public void onUpdate() {
        if (this.getPlayer() instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity) this.getPlayer();
            Aether.LOGGER.info(clientPlayerEntity.getCloakTextureLocation());
            //minecraft:skins/ea963f1b7d7c510da28800a770882d0c4b0aee6d = mojang cape. this is client only though.........
        }
    }

    public Set<PlayerRanking> getRanksOf(UUID uuid) {
        return this.ranks.getOrDefault(uuid, Collections.emptySet());
    }

    public PlayerRanking getPrimaryRankOf(UUID uuid) {
        return getRanksOf(uuid).stream().max(Comparator.naturalOrder()).orElse(PlayerRanking.NONE);
    }

    public boolean addRank(String uuidString, PlayerRanking rank) {
        return this.addRank(UUID.fromString(uuidString), rank);
    }

    public boolean addRank(UUID uuid, PlayerRanking rank) {
        return this.ranks.computeIfAbsent(uuid, l -> EnumSet.noneOf(PlayerRanking.class)).add(rank);
    }

    public boolean addRank(String uuidString, PlayerRanking... ranksIn) {
        return this.addRank(UUID.fromString(uuidString), ranksIn);
    }

    public boolean addRank(UUID uuid, PlayerRanking... rank) {
        return this.ranks.computeIfAbsent(uuid, l -> EnumSet.noneOf(PlayerRanking.class)).addAll(Arrays.asList(rank));
    }

    public boolean removeRank(String uuidString, PlayerRanking rank) {
        return this.removeRank(UUID.fromString(uuidString), rank);
    }

    public boolean removeRank(UUID uuid, PlayerRanking rank) {
        Set<PlayerRanking> ranks = this.ranks.get(uuid);
        return ranks != null && ranks.remove(rank);
    }

    public boolean removeRank(String uuidString, PlayerRanking... ranksIn) {
        return this.removeRank(UUID.fromString(uuidString), ranksIn);
    }

    public boolean removeRank(UUID uuid, PlayerRanking... rank) {
        Set<PlayerRanking> ranks = this.ranks.get(uuid);
        return ranks != null && ranks.removeAll(Arrays.asList(rank));
    }
}
