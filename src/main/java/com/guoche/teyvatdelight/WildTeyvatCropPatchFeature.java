package com.guoche.teyvatdelight;

import com.mojang.serialization.Codec;

/** @deprecated Use {@link com.guoche.teyvatdelight.worldgen.WildTeyvatCropPatchFeature} for new integrations. */
@Deprecated
public class WildTeyvatCropPatchFeature extends com.guoche.teyvatdelight.worldgen.WildTeyvatCropPatchFeature {
    public WildTeyvatCropPatchFeature(Codec<WildTeyvatCropPatchConfiguration> codec) {
        super(codec);
    }
}
