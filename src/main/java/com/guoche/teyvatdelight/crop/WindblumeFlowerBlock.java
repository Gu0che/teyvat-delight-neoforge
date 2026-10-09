package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.harvest.CollectionTools;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WindblumeFlowerBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<WindblumeFlowerBlock> CODEC = simpleCodec(WindblumeFlowerBlock::new);

    public WindblumeFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(TeyvatTags.Blocks.NI_CI_ZHI_FIELDS);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.below()).is(TeyvatTags.Blocks.NI_CI_ZHI_FIELDS);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        HarvestDrops.create(level, pos, state, null, ItemStack.EMPTY, Method.BONEMEAL)
                .base(TeyvatDelight.WINDBLUME.get(), 1).drop();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        // Native loot settlement is shared with environmental destruction.
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        var entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
        Player player = entity instanceof Player p ? p : null;
        var tool = builder.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) tool = ItemStack.EMPTY;
        var pos = BlockPos.containing(builder.getParameter(LootContextParams.ORIGIN));
        var drops = HarvestDrops.create(builder.getLevel(), pos, state, player, tool, Method.BREAK);
        if (CollectionTools.isShears(tool)) return drops.base(this, 1).resolve(false);
        drops.loot(super.getDrops(state, builder));
        if (player != null && !player.isCreative()) drops.cropMora();
        return drops.resolve(false);
    }

    @Override
    protected MapCodec<? extends WindblumeFlowerBlock> codec() {
        return CODEC;
    }
}
