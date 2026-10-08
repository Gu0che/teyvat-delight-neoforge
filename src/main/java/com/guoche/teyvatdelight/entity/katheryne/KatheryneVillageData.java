package com.guoche.teyvatdelight.entity.katheryne;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class KatheryneVillageData extends SavedData {
    private final Set<String> spawnedLibraries = new HashSet<>();

    public static KatheryneVillageData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new Factory<>(com.guoche.teyvatdelight.KatheryneVillageData::new, KatheryneVillageData::load), "teyvatdelight_katheryne_libraries");
    }

    private static KatheryneVillageData load(CompoundTag tag, HolderLookup.Provider registries) {
        KatheryneVillageData data = new com.guoche.teyvatdelight.KatheryneVillageData();
        ListTag list = tag.getList("Libraries", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) data.spawnedLibraries.add(list.getString(i));
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        spawnedLibraries.stream().sorted().map(StringTag::valueOf).forEach(list::add);
        tag.put("Libraries", list);
        return tag;
    }

    public boolean hasSpawned(String key) {
        return spawnedLibraries.contains(key);
    }

    public void markSpawned(String key) {
        if (spawnedLibraries.add(key)) setDirty();
    }
}
