package com.guoche.teyvatdelight.mineral;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
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
import net.neoforged.neoforge.common.CommonHooks;

public class ShipoCropBlock extends BushBlock {
    public static final MapCodec<ShipoCropBlock> CODEC = simpleCodec(ShipoCropBlock::new);
    private final int stage;
    private final Supplier<? extends Block> nextStage;
    private final Supplier<? extends ItemLike> mineralItem;
    private final VoxelShape shape;

    public ShipoCropBlock(BlockBehaviour.Properties properties) {
        this(properties, 2, null, TeyvatDelight.SHIPO, 8.0);
    }

    public ShipoCropBlock(
            BlockBehaviour.Properties properties,
            int stage,
            @Nullable Supplier<? extends Block> nextStage,
            double height
    ) {
        this(properties, stage, nextStage, TeyvatDelight.SHIPO, height);
    }

    public ShipoCropBlock(
            BlockBehaviour.Properties properties,
            int stage,
            @Nullable Supplier<? extends Block> nextStage,
            Supplier<? extends ItemLike> mineralItem,
            double height
    ) {
        super(properties);
        this.stage = stage;
        this.nextStage = nextStage;
        this.mineralItem = mineralItem;
        this.shape = Block.box(0.0, 0.0, 1.0, 16.0, height, 16.0);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(TeyvatTags.Blocks.XUAN_CI_PU_FIELDS);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.nextStage == null || !level.isAreaLoaded(pos, 1)) {
            return;
        }

        if (CommonHooks.canCropGrow(level, pos, state, random.nextInt(5) == 0)) {
            level.setBlock(pos, this.nextStage.get().defaultBlockState(), 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
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
        if (this.nextStage == null || !stack.is(Items.AMETHYST_SHARD)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            if (level.random.nextInt(4) == 0) {
                level.setBlock(pos, this.nextStage.get().defaultBlockState(), 2);
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_HIT, SoundSource.BLOCKS, 1.0F, 0.6F);
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.mineralItem.get());
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
        if (!level.isClientSide && !player.isCreative() && tool.is(ItemTags.PICKAXES)) {
            if (this.stage >= 2) {
                HarvestDrops.create(level, pos, state, player, tool, Method.BREAK)
                        .base(this.mineralItem.get(), 3).mineralMora().drop();
            }
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
