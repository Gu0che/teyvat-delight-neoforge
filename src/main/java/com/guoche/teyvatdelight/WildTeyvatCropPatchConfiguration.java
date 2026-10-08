package com.guoche.teyvatdelight;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record WildTeyvatCropPatchConfiguration(
        BlockState state,
        int minCount,
        int maxCount,
        int xzSpread,
        int tries,
        int ySpread,
        boolean mufengTopOnlyStripped,
        boolean meadowWindblume
) implements FeatureConfiguration {
    public static final Codec<WildTeyvatCropPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(WildTeyvatCropPatchConfiguration::state),
            Codec.intRange(1, 16).fieldOf("min_count").orElse(1).forGetter(WildTeyvatCropPatchConfiguration::minCount),
            Codec.intRange(1, 16).fieldOf("max_count").orElse(4).forGetter(WildTeyvatCropPatchConfiguration::maxCount),
            Codec.intRange(0, 32).fieldOf("xz_spread").orElse(3).forGetter(WildTeyvatCropPatchConfiguration::xzSpread),
            Codec.intRange(1, 256).fieldOf("tries").orElse(96).forGetter(WildTeyvatCropPatchConfiguration::tries),
            Codec.intRange(0, 16).fieldOf("y_spread").orElse(3).forGetter(WildTeyvatCropPatchConfiguration::ySpread),
            Codec.BOOL.fieldOf("mufeng_top_only_stripped").orElse(false).forGetter(WildTeyvatCropPatchConfiguration::mufengTopOnlyStripped),
            Codec.BOOL.fieldOf("meadow_windblume").orElse(false).forGetter(WildTeyvatCropPatchConfiguration::meadowWindblume)
    ).apply(instance, WildTeyvatCropPatchConfiguration::new));
}
