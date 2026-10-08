package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WolfhookCropBlock extends TeyvatCropBlock {
    public static final MapCodec<WolfhookCropBlock> CODEC = simpleCodec(WolfhookCropBlock::new);

    public WolfhookCropBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS,
                TeyvatDelight.WOLFHOOK, TeyvatDelight.WOLFHOOK, 3, 2, 1);
    }

    @Override
    protected boolean isHarvestable(BlockState state) {
        return this.isMaxAge(state);
    }

    @Override
    public void harvestAndReplant(Level level, BlockPos pos, BlockState state,
            Player player) {
        if (state.getBlock() == this && this.isHarvestable(state)) {
            super.harvestAndReplant(level, pos, state, player);
        }
    }

    @Override
    protected int getAgeAfterHarvest() {
        return 1;
    }

    @Override
    protected int getSeedCountOnBreak(BlockState state) {
        return this.isMaxAge(state) ? 0 : 1;
    }

    @Override
    protected int getHarvestCount(LevelReader level, BlockPos pos, BlockState state) {
        int base = 2;
        int fieldBonus = this.isOnPreferredField(level, pos) ? 1 : 0;
        return base + (level instanceof Level gameLevel ? gameLevel.random.nextInt(2) : 0) + fieldBonus;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.getAge(state) > 0) {
            hurtMovingEntity(state, level, entity);
        }
    }

    public static void hurtMovingEntity(BlockState state, Level level, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.8, 0.75, 0.8));
        if (entity instanceof LivingEntity
                && (Math.abs(entity.getX() - entity.xOld) >= 0.003
                || Math.abs(entity.getZ() - entity.zOld) >= 0.003)) {
            entity.hurt(level.damageSources().sweetBerryBush(), 1.0F);
        }
    }

    @Override
    public MapCodec<? extends WolfhookCropBlock> codec() {
        return CODEC;
    }
}
