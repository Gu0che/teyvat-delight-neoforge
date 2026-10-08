package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;

public abstract class TeyvatCropBlock extends CropBlock {
    private final TagKey<Block> preferredFieldTag;
    private final Supplier<? extends ItemLike> cropItem;
    private final Supplier<? extends ItemLike> seedItem;
    private final int maxAge;
    private final int harvestCount;
    private final int preferredFieldHarvestBonus;

    protected TeyvatCropBlock(
            BlockBehaviour.Properties properties,
            TagKey<Block> preferredFieldTag,
            Supplier<? extends ItemLike> cropItem,
            Supplier<? extends ItemLike> seedItem
    ) {
        this(properties, preferredFieldTag, cropItem, seedItem, 7, 1, 1);
    }

    protected TeyvatCropBlock(
            BlockBehaviour.Properties properties,
            TagKey<Block> preferredFieldTag,
            Supplier<? extends ItemLike> cropItem,
            Supplier<? extends ItemLike> seedItem,
            int maxAge,
            int harvestCount,
            int preferredFieldHarvestBonus
    ) {
        super(properties);
        this.preferredFieldTag = preferredFieldTag;
        this.cropItem = cropItem;
        this.seedItem = seedItem;
        this.maxAge = maxAge;
        this.harvestCount = harvestCount;
        this.preferredFieldHarvestBonus = preferredFieldHarvestBonus;
    }

    @Override
    public int getMaxAge() {
        return this.maxAge;
    }

    /** Implementation metadata used by the read-only crop API; does not calculate drops. */
    public final net.minecraft.world.item.Item getProduceItem() {
        return this.cropItem.get().asItem();
    }

    public final net.minecraft.world.item.Item getPlantingItem() {
        return this.seedItem.get().asItem();
    }

    public final TagKey<Block> getPreferredFieldTag() {
        return this.preferredFieldTag;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND) || state.is(this.preferredFieldTag);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedItem.get();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }

        if (level.getRawBrightness(pos, 0) < 9) {
            return;
        }

        int age = this.getAge(state);
        if (age >= this.getMaxAge()) {
            return;
        }

        float growthSpeed = this.isOnPreferredField(level, pos)
                ? this.getPreferredFieldGrowthSpeed(state, level, pos)
                : getGrowthSpeed(state, level, pos) / 5.0F;
        int chance = Math.max(1, (int)(25.0F / growthSpeed) + 1);
        if (CommonHooks.canCropGrow(level, pos, state, random.nextInt(chance) == 0)) {
            level.setBlock(pos, this.getStateForAge(age + 1), 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return !this.isMaxAge(state) && this.isOnPreferredField(level, pos);
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
        if (this.isHarvestable(state)) {
            this.harvestAndReplant(level, pos, state, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.isHarvestable(state)) {
            this.harvestAndReplant(level, pos, state, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            TeyvatCropDropTracker.skipNextDrop(level, pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
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
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        TeyvatCropDropTracker.consumeSkipDrop(level, pos);
        if (!level.isClientSide && !player.isCreative()) {
            this.dropForBreak(level, pos, state, player, tool);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide
                && state.getBlock() != newState.getBlock()
                && !TeyvatCropDropTracker.consumeSkipDrop(level, pos)) {
            this.dropForBreak(level, pos, state, null, ItemStack.EMPTY);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    public void harvestAndReplant(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            int bonus = this.getFieldHarvestBonus(level, pos);
            var drops = HarvestDrops.create(level, pos, state, player, player.getMainHandItem(), Method.RIGHT_CLICK)
                    .base(this.cropItem.get(), this.getHarvestCount(level, pos, state) - bonus)
                    .field(this.cropItem.get(), bonus).seed(this.getBaseSeedId());
            if (this.isMaxAge(state)) drops.cropMora();
            drops.drop();
            level.setBlock(pos, this.getStateForAge(this.getAgeAfterHarvest()), 2);
            level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    protected int getAgeAfterHarvest() {
        return 0;
    }

    protected boolean isHarvestable(BlockState state) {
        return this.isMaxAge(state);
    }

    protected int getSeedCountOnBreak(BlockState state) {
        return 1;
    }

    protected void dropForBreak(Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack tool) {
        var drops = HarvestDrops.create(level, pos, state, player, tool, Method.BREAK)
                .base(this.seedItem.get(), this.getSeedCountOnBreak(state));
        if (this.isHarvestable(state)) {
            int bonus = this.getFieldHarvestBonus(level, pos);
            drops.base(this.cropItem.get(), this.getHarvestCount(level, pos, state) - bonus)
                    .field(this.cropItem.get(), bonus).seed(this.getBaseSeedId());
            if (player != null && this.isMaxAge(state)) drops.cropMora();
        }
        drops.drop();
    }

    protected int getHarvestCount(LevelReader level, BlockPos pos) {
        return this.harvestCount + (this.isOnPreferredField(level, pos) ? this.preferredFieldHarvestBonus : 0);
    }

    protected int getFieldHarvestBonus(LevelReader level, BlockPos pos) {
        return this.isOnPreferredField(level, pos) ? this.preferredFieldHarvestBonus : 0;
    }

    protected int getHarvestCount(LevelReader level, BlockPos pos, BlockState state) {
        return this.getHarvestCount(level, pos);
    }

    protected boolean isOnPreferredField(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(this.preferredFieldTag);
    }

    private float getPreferredFieldGrowthSpeed(BlockState cropState, BlockGetter level, BlockPos pos) {
        Block cropBlock = cropState.getBlock();
        float speed = 1.0F;
        BlockPos below = pos.below();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                float soilSpeed = 0.0F;
                BlockPos soilPos = below.offset(x, 0, z);
                BlockState soilState = level.getBlockState(soilPos);
                if (soilState.is(this.preferredFieldTag)) {
                    soilSpeed = 3.0F;
                } else {
                    TriState soilDecision = soilState.canSustainPlant(level, soilPos, Direction.UP, cropState);
                    if (soilDecision.isDefault()
                            ? soilState.getBlock() instanceof net.minecraft.world.level.block.FarmBlock
                            : soilDecision.isTrue()) {
                        soilSpeed = soilState.isFertile(level, pos.offset(x, 0, z)) ? 3.0F : 1.0F;
                    }
                }

                if (x != 0 || z != 0) {
                    soilSpeed /= 4.0F;
                }

                speed += soilSpeed;
            }
        }

        boolean horizontalCrop = level.getBlockState(pos.west()).is(cropBlock) || level.getBlockState(pos.east()).is(cropBlock);
        boolean verticalCrop = level.getBlockState(pos.north()).is(cropBlock) || level.getBlockState(pos.south()).is(cropBlock);
        if (horizontalCrop && verticalCrop) {
            speed /= 2.0F;
        } else {
            boolean diagonalCrop = level.getBlockState(pos.west().north()).is(cropBlock)
                    || level.getBlockState(pos.east().north()).is(cropBlock)
                    || level.getBlockState(pos.east().south()).is(cropBlock)
                    || level.getBlockState(pos.west().south()).is(cropBlock);
            if (diagonalCrop) {
                speed /= 2.0F;
            }
        }

        return speed;
    }
}
