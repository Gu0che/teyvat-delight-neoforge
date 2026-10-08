package com.guoche.teyvatdelight.api.harvest;

import com.guoche.teyvatdelight.api.TeyvatCropApi;
import com.guoche.teyvatdelight.api.TeyvatMineralApi;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Immutable pre-harvest metadata. World/player access is restricted to the server thread. */
public record HarvestContext(ServerLevel level, BlockPos pos, BlockState state, BlockState soil,
        @Nullable Player player, ItemStack tool, Method method, ResourceLocation specialty,
        boolean wild, boolean mature, boolean preferredField) {
    public enum Method { BREAK, RIGHT_CLICK, COOLING, KNIFE, BONEMEAL }
    public HarvestContext {
        pos = pos.immutable();
        tool = tool.copy();
    }
    @Override public ItemStack tool() { return tool.copy(); }

    public static HarvestContext capture(ServerLevel level, BlockPos pos, BlockState state,
            @Nullable Player player, ItemStack tool, Method method) {
        if (!level.getServer().isSameThread()) throw new IllegalStateException("Harvest requires server thread");
        var soil = level.getBlockState(pos.below());
        var crop = TeyvatCropApi.inspect(state);
        if (crop.isPresent()) {
            var value = crop.get();
            if (value.part() == TeyvatCropApi.Part.UPPER) soil = level.getBlockState(pos.below(2));
            return new HarvestContext(level, pos, state, soil, player, tool, method,
                    value.definition().id(), value.form() == TeyvatCropApi.Form.WILD, value.isMature(),
                    soil.is(value.definition().preferredField()));
        }
        var mineral = TeyvatMineralApi.inspect(state);
        if (mineral.isPresent()) {
            var value = mineral.get();
            return new HarvestContext(level, pos, state, soil, player, tool, method,
                    value.definition().id(), value.stage() == TeyvatMineralApi.Stage.NATURAL,
                    value.isMature(), soil.is(value.definition().preferredField()));
        }
        return new HarvestContext(level, pos, state, soil, player, tool, method,
                BuiltInRegistries.BLOCK.getKey(state.getBlock()), false, false, false);
    }

    public HarvestContext withMaturity(boolean value) {
        return new HarvestContext(level, pos, state, soil, player, tool, method, specialty, wild, value, preferredField);
    }
}
