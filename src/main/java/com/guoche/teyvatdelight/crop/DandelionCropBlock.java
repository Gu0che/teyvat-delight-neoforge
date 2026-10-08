package com.guoche.teyvatdelight.crop;

import com.guoche.teyvatdelight.harvest.HarvestDrops;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;

import com.guoche.teyvatdelight.DandelionHarvestRules;
import com.guoche.teyvatdelight.TeyvatCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DandelionCropBlock extends TeyvatCropBlock {
    public static final MapCodec<DandelionCropBlock> CODEC = simpleCodec(DandelionCropBlock::new);

    public DandelionCropBlock(BlockBehaviour.Properties properties) {
        super(properties, TeyvatTags.Blocks.NI_CI_ZHI_FIELDS,
                TeyvatDelight.DANDELION_SEEDS, TeyvatDelight.DANDELION_SEEDS, 7, 1, 1);
    }

    @Override
    public void harvestAndReplant(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.isClientSide) {
            return;
        }
        if (!DandelionHarvestRules.canHarvest(player, player.getMainHandItem())) {
            player.displayClientMessage(Component.translatable("message.teyvatdelight.dandelion_scattered"), true);
            return;
        }
        int bonus = this.getFieldHarvestBonus(level, pos);
        HarvestDrops.create(level, pos, state, player, player.getMainHandItem(), Method.RIGHT_CLICK)
                .base(TeyvatDelight.DANDELION_SEEDS.get(), this.getHarvestCount(level, pos) - bonus)
                .field(TeyvatDelight.DANDELION_SEEDS.get(), bonus).cropMora().drop();
        level.setBlock(pos, this.getStateForAge(0), 2);
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    protected void dropForBreak(Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack tool) {
        if (player == null) {
            return;
        }
        if (!DandelionHarvestRules.canHarvest(player, tool)) {
            player.displayClientMessage(Component.translatable("message.teyvatdelight.dandelion_scattered"), true);
            return;
        }
        int bonus = this.isMaxAge(state) ? this.getFieldHarvestBonus(level, pos) : 0;
        int count = this.isMaxAge(state) ? this.getHarvestCount(level, pos) : 1;
        var drops = HarvestDrops.create(level, pos, state, player, tool, Method.BREAK)
                .base(TeyvatDelight.DANDELION_SEEDS.get(), count - bonus)
                .field(TeyvatDelight.DANDELION_SEEDS.get(), bonus);
        if (this.isMaxAge(state)) drops.cropMora();
        drops.drop();
    }

    @Override
    public MapCodec<? extends DandelionCropBlock> codec() {
        return CODEC;
    }
}
