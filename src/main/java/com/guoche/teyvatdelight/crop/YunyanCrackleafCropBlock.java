package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class YunyanCrackleafCropBlock extends TeyvatCropBlock {
    public static final MapCodec<YunyanCrackleafCropBlock> CODEC = simpleCodec(YunyanCrackleafCropBlock::new);

    public YunyanCrackleafCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                com.guoche.teyvatdelight.registry.ModItems.YUNYAN_LIEYE,
                com.guoche.teyvatdelight.registry.ModItems.YUNYAN_LIEYE_SEEDS,
                7,
                1,
                1
        );
    }

    @Override
    public MapCodec<? extends YunyanCrackleafCropBlock> codec() {
        return CODEC;
    }
}
