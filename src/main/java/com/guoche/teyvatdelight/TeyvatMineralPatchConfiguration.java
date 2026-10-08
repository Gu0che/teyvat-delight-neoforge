package com.guoche.teyvatdelight;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record TeyvatMineralPatchConfiguration(
        BlockState state,
        int minCount,
        int maxCount,
        int xzSpread,
        int ySpread
) implements FeatureConfiguration {
    public static final Codec<TeyvatMineralPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(TeyvatMineralPatchConfiguration::state),
            Codec.intRange(1, 16).fieldOf("min_count").orElse(1).forGetter(TeyvatMineralPatchConfiguration::minCount),
            Codec.intRange(1, 16).fieldOf("max_count").orElse(4).forGetter(TeyvatMineralPatchConfiguration::maxCount),
            Codec.intRange(0, 16).fieldOf("xz_spread").orElse(3).forGetter(TeyvatMineralPatchConfiguration::xzSpread),
            Codec.intRange(0, 16).fieldOf("y_spread").orElse(4).forGetter(TeyvatMineralPatchConfiguration::ySpread)
    ).apply(instance, TeyvatMineralPatchConfiguration::new));
}
