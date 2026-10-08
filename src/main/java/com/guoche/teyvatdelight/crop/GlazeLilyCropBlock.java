package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class GlazeLilyCropBlock extends TeyvatCropBlock {
    public static final MapCodec<GlazeLilyCropBlock> CODEC = simpleCodec(GlazeLilyCropBlock::new);

    public GlazeLilyCropBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                TeyvatDelight.GLAZE_LILY,
                TeyvatDelight.GLAZE_LILY_SEEDS,
                7,
                1,
                1
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(GlazeLilyBlooming.BLOOMING, false));
    }

    @Override
    public MapCodec<? extends GlazeLilyCropBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForAge(int age) {
        return this.defaultBlockState()
                .setValue(AGE, Math.min(age, this.getMaxAge()))
                .setValue(GlazeLilyBlooming.BLOOMING, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GlazeLilyBlooming.BLOOMING);
    }
}
