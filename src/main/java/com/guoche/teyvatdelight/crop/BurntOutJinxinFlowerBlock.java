package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.JinxinFlowerBehavior;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BurntOutJinxinFlowerBlock extends BushBlock {
    public static final MapCodec<BurntOutJinxinFlowerBlock> CODEC = simpleCodec(BurntOutJinxinFlowerBlock::new);
    private static final float HOT_TOUCH_DAMAGE = 0.5F;
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    public BurntOutJinxinFlowerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BurntOutJinxinFlowerBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return JinxinFlowerBehavior.canGrowOn(state);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (JinxinFlowerBehavior.isCoolingItem(stack) && harvestAndReset(level, pos, player)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        warnHot(level, player);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        warnHot(level, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(TeyvatDelight.JINXIN_FLOWER_BUD.get());
    }

    @Override
    public void playerDestroy(
            Level level,
            Player player,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            ItemStack tool
    ) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        TeyvatCropDropTracker.consumeSkipDrop(level, pos);
        if (!level.isClientSide && !player.isCreative()) {
            HarvestDrops.create(level, pos, state, player, tool, Method.BREAK)
                    .base(TeyvatDelight.JINXIN_FLOWER_BUD.get(), 1).drop();
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            TeyvatCropDropTracker.skipNextDrop(level, pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide
                && state.getBlock() != newState.getBlock()
                && !TeyvatCropDropTracker.consumeSkipDrop(level, pos)) {
            HarvestDrops.create(level, pos, state, null, ItemStack.EMPTY, Method.BREAK)
                    .base(TeyvatDelight.JINXIN_FLOWER_BUD.get(), 1).drop();
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    private boolean harvestAndReset(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            var state = level.getBlockState(pos);
            int bonus = JinxinFlowerBehavior.isOnPreferredField(level, pos) ? 1 : 0;
            HarvestDrops.create(level, pos, state, player, player.getMainHandItem(), Method.COOLING)
                    .base(TeyvatDelight.JINXIN_FLOWER.get(), 1).field(TeyvatDelight.JINXIN_FLOWER.get(), bonus)
                    .seed(TeyvatDelight.JINXIN_FLOWER_BUD.get()).cropMora().drop();
            TeyvatCropDropTracker.skipNextDrop(level, pos);
            level.setBlock(pos, TeyvatDelight.THIRSTING_JINXIN_FLOWER.get().defaultBlockState(), 2);
            level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            JinxinFlowerBehavior.playCoolingSound(level, pos);
        }

        return true;
    }

    private static void warnHot(Level level, Player player) {
        if (!level.isClientSide) {
            player.hurt(level.damageSources().hotFloor(), HOT_TOUCH_DAMAGE);
            player.displayClientMessage(Component.translatable("message.teyvatdelight.jinxin_flower.too_hot"), true);
        }
    }
}
