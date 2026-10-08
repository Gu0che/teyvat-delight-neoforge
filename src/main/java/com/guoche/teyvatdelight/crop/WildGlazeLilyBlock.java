package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class WildGlazeLilyBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildGlazeLilyBlock> CODEC = simpleCodec(WildGlazeLilyBlock::new);

    public WildGlazeLilyBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                TeyvatDelight.GLAZE_LILY,
                TeyvatDelight.GLAZE_LILY_SEEDS,
                Surface.GRASS_DIRT_OR_MUD
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(GlazeLilyBlooming.BLOOMING, false));
    }

    @Override
    protected MapCodec<? extends WildGlazeLilyBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(GlazeLilyBlooming.BLOOMING, GlazeLilyBlooming.shouldBloom(context.getLevel()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GlazeLilyBlooming.BLOOMING);
    }
}
