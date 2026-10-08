package com.guoche.teyvatdelight.integration.curios;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class WindWingsCurios {
    // Caelus removes its own elytra modifier every tick when checking the chest slot.
    private static final AttributeModifier BACK_FLIGHT_MODIFIER = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "wind_wings_back"),
            1.0D, AttributeModifier.Operation.ADD_VALUE);

    private WindWingsCurios() {
    }

    public static void register(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CuriosApi.registerCurio(TeyvatDelight.WIND_WINGS.get(), new ICurioItem() {
            @Override
            public boolean canEquip(SlotContext context, ItemStack stack) {
                return "back".equals(context.identifier());
            }

            @Override
            public void onEquip(SlotContext context, ItemStack previousStack, ItemStack stack) {
                updateFlightModifier(context, stack);
            }

            @Override
            public void curioTick(SlotContext context, ItemStack stack) {
                updateFlightModifier(context, stack);
            }

            @Override
            public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
                    SlotContext context, ResourceLocation id, ItemStack stack) {
                if (!"back".equals(context.identifier()) || context.cosmetic()) {
                    return ImmutableMultimap.of();
                }
                CaelusApi caelus = CaelusApi.getInstance();
                return ImmutableMultimap.of(caelus.getFallFlyingAttribute(), BACK_FLIGHT_MODIFIER);
            }
        }));
    }

    private static void updateFlightModifier(SlotContext context, ItemStack stack) {
        if (!"back".equals(context.identifier()) || context.cosmetic() || context.entity().level().isClientSide) {
            return;
        }
        AttributeInstance flight = context.entity().getAttribute(CaelusApi.getInstance().getFallFlyingAttribute());
        if (flight == null) return;

        // Food changes do not change the equipped stack, so Curios will not recalculate its modifiers.
        if (stack.canElytraFly(context.entity())) {
            if (!flight.hasModifier(BACK_FLIGHT_MODIFIER.id())) flight.addTransientModifier(BACK_FLIGHT_MODIFIER);
        } else {
            flight.removeModifier(BACK_FLIGHT_MODIFIER.id());
        }
    }

    public static ItemStack getBackWings(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.is(TeyvatDelight.WIND_WINGS.get()), "back"))
                .map(result -> result.stack())
                .orElse(ItemStack.EMPTY);
    }
}
