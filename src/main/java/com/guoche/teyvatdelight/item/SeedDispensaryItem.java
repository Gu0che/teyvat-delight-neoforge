package com.guoche.teyvatdelight.item;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.api.TeyvatItemData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

public class SeedDispensaryItem extends Item {
    public static final int DEFAULT_STARS = 4;

    public SeedDispensaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(
                TeyvatItemData.getStarColor(TeyvatItemData.getStars(stack, DEFAULT_STARS)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        TeyvatItemData.appendRarityTooltip(stack, tooltip, DEFAULT_STARS);
    }

    public static boolean isHeldBy(@Nullable Player player) {
        if (player == null) return false;
        if (player.getMainHandItem().is(TeyvatDelight.SEED_DISPENSARY.get())
                || player.getOffhandItem().is(TeyvatDelight.SEED_DISPENSARY.get())) {
            return true;
        }
        // Curios 联动（软依赖）：只有装了 Curios 才查询饰品栏
        if (ModList.get().isLoaded("curios")) {
            return CuriosApi.getCuriosInventory(player)
                    .map(h -> h.isEquipped(TeyvatDelight.SEED_DISPENSARY.get()))
                    .orElse(false);
        }
        return false;
    }

    public static void dropExtraSeedIfHeld(Level level, BlockPos pos, @Nullable Player player, ItemLike seedItem) {
        if (!level.isClientSide && isHeldBy(player)) {
            Block.popResource(level, pos, new ItemStack(seedItem));
        }
    }
}
