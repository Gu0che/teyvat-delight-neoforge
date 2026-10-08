package com.guoche.teyvatdelight.harvest;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.harvest.HarvestContext;
import com.guoche.teyvatdelight.api.harvest.HarvestContext.Method;
import com.guoche.teyvatdelight.api.harvest.HarvestResult;
import com.guoche.teyvatdelight.api.harvest.HarvestResult.Source;
import com.guoche.teyvatdelight.api.harvest.HarvestResultEvent;
import com.guoche.teyvatdelight.crop.TeyvatBonusDrops;
import com.guoche.teyvatdelight.item.SeedDispensaryItem;
import com.guoche.teyvatdelight.registry.ModItems;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

/** Internal settlement owner. Calling resolve does not reset blocks or consume interaction items. */
public final class HarvestDrops {
    private final HarvestContext context;
    private final HarvestResult result = new HarvestResult();
    private boolean settled;

    private HarvestDrops(HarvestContext context) { this.context = context; }
    public static HarvestDrops create(Level level, BlockPos pos, BlockState state,
            @Nullable Player player, ItemStack tool, Method method) {
        if (!(level instanceof ServerLevel server)) throw new IllegalStateException("Server harvest only");
        return new HarvestDrops(HarvestContext.capture(server, pos, state, player, tool, method));
    }
    public static HarvestDrops create(HarvestContext context) { return new HarvestDrops(context); }
    public static HarvestDrops create(Level level, BlockPos pos, BlockState state,
            @Nullable Player player, ItemStack tool, Method method, boolean mature) {
        return create(HarvestContext.capture((ServerLevel) level, pos, state, player, tool, method).withMaturity(mature));
    }
    public HarvestDrops base(ItemLike item, int count) { return add(Source.BASE, item, count); }
    public HarvestDrops field(ItemLike item, int count) { return add(Source.FIELD, item, count); }
    public HarvestDrops add(Source source, ItemLike item, int count) {
        checkOpen();
        if (count > 0) result.add(source, new ItemStack(item, count));
        return this;
    }
    public HarvestDrops loot(List<ItemStack> stacks) {
        checkOpen();
        for (var stack : stacks) result.add(Source.BASE, stack);
        return this;
    }
    public HarvestDrops seed(ItemLike seed) {
        if (SeedDispensaryItem.isHeldBy(context.player())) add(Source.SEED_DISPENSARY, seed, 1);
        return this;
    }
    public HarvestDrops cropMora() {
        if (context.player() != null && TeyvatBonusDrops.rollMora(context.level(), context.player(), context.tool()))
            add(Source.MORA, ModItems.MORA.get(), 1);
        return this;
    }
    public HarvestDrops mineralMora() { return add(Source.MORA, ModItems.MORA.get(), 1); }

    public List<ItemStack> resolve(boolean emit) {
        checkOpen();
        settled = true;
        if (!context.level().getServer().isSameThread()) throw new IllegalStateException("Server thread required");
        var adjusted = result.copy();
        try {
            HarvestRules.apply(context, adjusted);
            var beforeListener = adjusted.copy();
            try {
                NeoForge.EVENT_BUS.post(new HarvestResultEvent.Modify(context, adjusted));
            } catch (RuntimeException e) {
                TeyvatDelight.LOGGER.error("Harvest listener failed for {}", context.specialty(), e);
                adjusted = beforeListener;
            }
        } catch (RuntimeException e) {
            TeyvatDelight.LOGGER.error("Harvest rules failed for {}", context.specialty(), e);
            adjusted = result.copy();
        }
        List<ItemStack> stacks;
        try { stacks = adjusted.stacks(); }
        catch (RuntimeException e) {
            TeyvatDelight.LOGGER.error("Oversized harvest result for {}, using original output", context.specialty(), e);
            adjusted = result.copy();
            stacks = adjusted.stacks();
        }
        boolean emitted = emit && context.level().getGameRules()
                .getBoolean(net.minecraft.world.level.GameRules.RULE_DOBLOCKDROPS);
        if (emitted) for (var stack : stacks) Block.popResource(context.level(), context.pos(), stack);
        try {
            NeoForge.EVENT_BUS.post(new HarvestResultEvent.Resolved(context, adjusted, emitted));
        } catch (RuntimeException e) {
            TeyvatDelight.LOGGER.error("Harvest result observer failed for {}", context.specialty(), e);
        }
        return stacks;
    }
    public void drop() { resolve(true); }
    private void checkOpen() {
        if (settled) throw new IllegalStateException("Harvest result already settled");
    }
}
