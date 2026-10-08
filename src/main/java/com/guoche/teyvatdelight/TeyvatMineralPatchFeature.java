package com.guoche.teyvatdelight;

import com.mojang.serialization.Codec;

/** @deprecated Use {@link com.guoche.teyvatdelight.worldgen.TeyvatMineralPatchFeature} for new integrations. */
@Deprecated
public class TeyvatMineralPatchFeature extends com.guoche.teyvatdelight.worldgen.TeyvatMineralPatchFeature {
    public TeyvatMineralPatchFeature(Codec<TeyvatMineralPatchConfiguration> codec) {
        super(codec);
    }
}
