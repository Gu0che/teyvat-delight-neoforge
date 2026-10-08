package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.DandelionHarvestRules;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildTeyvatCropBlock;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WildDandelionBlock extends WildTeyvatCropBlock {
    public static final MapCodec<WildDandelionBlock> CODEC = simpleCodec(WildDandelionBlock::new);

    public WildDandelionBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatDelight.DANDELION_SEEDS,
                TeyvatDelight.DANDELION_SEEDS, Surface.GRASS_OR_DIRT);
    }

    @Override
    protected void addHarvestBonuses(com.guoche.teyvatdelight.harvest.HarvestDrops drops, Player player, ItemStack tool) {
        if (DandelionHarvestRules.canHarvest(player, tool))
            drops.base(TeyvatDelight.DANDELION_SEEDS.get(), 1).cropMora();
    }

    @Override
    protected boolean shouldDropExtraSeed(Player player) {
        return false;
    }

    @Override
    protected boolean shouldDropMora(Player player, ItemStack tool) {
        return DandelionHarvestRules.canHarvest(player, tool);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide && !player.isCreative()) {
            if (!DandelionHarvestRules.canHarvest(player, tool)) {
                player.displayClientMessage(Component.translatable("message.teyvatdelight.dandelion_scattered"), true);
            }
        }
    }

    @Override
    protected MapCodec<? extends WildDandelionBlock> codec() {
        return CODEC;
    }
}
