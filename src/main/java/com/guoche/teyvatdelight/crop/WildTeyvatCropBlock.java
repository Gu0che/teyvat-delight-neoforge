package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.harvest.CollectionTools;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import com.guoche.teyvatdelight.WildTeyvatCropBlock.Surface;
import com.guoche.teyvatdelight.api.TeyvatTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class WildTeyvatCropBlock extends BushBlock {
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    private final Supplier<? extends ItemLike> cropItem;
    private final Supplier<? extends ItemLike> seedItem;
    private final Surface surface;

    protected WildTeyvatCropBlock(
            BlockBehaviour.Properties properties,
            Supplier<? extends ItemLike> cropItem,
            Supplier<? extends ItemLike> seedItem,
            Surface surface
    ) {
        super(properties);
        this.cropItem = cropItem;
        this.seedItem = seedItem;
        this.surface = surface;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return switch (this.surface) {
            case GRASS_OR_DIRT -> state.is(TeyvatTags.Blocks.WILD_GRASS_OR_DIRT_CROP_SURFACES);
            case ROCKY -> state.is(TeyvatTags.Blocks.WILD_ROCKY_CROP_SURFACES);
            case GRASS_DIRT_OR_MUD -> state.is(TeyvatTags.Blocks.WILD_GRASS_DIRT_OR_MUD_CROP_SURFACES);
            case SAND_NEAR_WATER -> isCallaLilySurface(state) && hasAdjacentWater(level, pos);
            case MUFENG_BUILDING -> WildMufengMushroomBlock.canAttachTo(state);
            case GRAINFRUIT -> state.is(Blocks.GRASS_BLOCK)
                    || state.is(Blocks.DIRT)
                    || state.is(BlockTags.SAND)
                    || state.is(Blocks.NETHERRACK)
                    || state.is(Blocks.WARPED_NYLIUM)
                    || state.is(Blocks.WARPED_ROOTS)
                    || state.is(Blocks.WARPED_FUNGUS);
            case FLUORESCENT_FUNGUS -> state.is(TeyvatTags.Blocks.WILD_FLUORESCENT_FUNGUS_SURFACES);
            case DENDROBIUM -> state.is(TeyvatTags.Blocks.WILD_DENDROBIUM_SURFACES);
            case FROSTLAMP -> state.is(TeyvatTags.Blocks.WILD_FROSTLAMP_SURFACES);
            case STONE_OR_TERRACOTTA -> state.is(TeyvatTags.Blocks.WILD_YUNYAN_CRACKLEAF_SURFACES);
        };
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
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
        // Native block drops include base loot and both bonuses in getDrops.
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
        if (player != null && !player.isCreative()) addHarvestBonuses(drops, player, tool);
        return drops.resolve(false);
    }

    protected void addHarvestBonuses(HarvestDrops drops, Player player, ItemStack tool) {
        if (this.shouldDropExtraSeed(player)) drops.seed(this.seedItem.get());
        if (this.shouldDropMora(player, tool)) drops.cropMora();
    }

    protected boolean shouldDropExtraSeed(Player player) {
        return true;
    }

    protected boolean shouldDropMora(Player player, ItemStack tool) {
        return true;
    }

    private static boolean hasAdjacentWater(BlockGetter level, BlockPos groundPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = groundPos.relative(direction);
            if (level.getFluidState(neighborPos).is(FluidTags.WATER)
                    || level.getBlockState(neighborPos).is(Blocks.FROSTED_ICE)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isCallaLilySurface(BlockState state) {
        return state.is(BlockTags.SAND) || state.is(Blocks.DIRT);
    }


}
