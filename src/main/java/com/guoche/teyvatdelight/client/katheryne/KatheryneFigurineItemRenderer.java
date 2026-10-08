package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.block.KatheryneFigurineBlock;
import com.guoche.teyvatdelight.block.KatheryneFigurineBlockEntity;
import com.guoche.teyvatdelight.registry.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class KatheryneFigurineItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final KatheryneFigurineBlockEntity figurine = new KatheryneFigurineBlockEntity(
            // Keep the approved held orientation; GUI uses a separate display object.
            BlockPos.ZERO, ModBlocks.KATHERYNE_FIGURINE.get().defaultBlockState()
                    .setValue(KatheryneFigurineBlock.FACING, Direction.WEST));
    private final KatheryneFigurineBlockEntity guiFigurine = new KatheryneFigurineBlockEntity(
            BlockPos.ZERO, ModBlocks.KATHERYNE_FIGURINE.get().defaultBlockState()
                    .setValue(KatheryneFigurineBlock.FACING, Direction.NORTH));

    private KatheryneFigurineItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
                             MultiBufferSource buffers, int light, int overlay) {
        // The dispatcher replaces its renderer on resource reload, including updated textures/models.
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(
                context == ItemDisplayContext.GUI ? guiFigurine : figurine, pose, buffers, light, overlay);
    }

    public static IClientItemExtensions extensions() {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new KatheryneFigurineItemRenderer();
                return renderer;
            }
        };
    }
}
