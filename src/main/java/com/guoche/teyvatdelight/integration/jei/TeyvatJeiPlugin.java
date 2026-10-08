package com.guoche.teyvatdelight.integration.jei;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.TeyvatMerchantCatalog;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.guoche.teyvatdelight.api.TeyvatCropApi;
import com.guoche.teyvatdelight.client.katheryne.KatheryneScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.vanilla.IJeiIngredientInfoRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public final class TeyvatJeiPlugin implements IModPlugin {
    private static final Map<String, List<String>> NATURAL_BIOMES = Map.ofEntries(
            Map.entry("small_lamp_grass", List.of("forest", "flower_forest", "dark_forest")),
            Map.entry("windwheel_aster", List.of("plains", "flower_forest", "sunflower_plains")),
            Map.entry("calla_lily", List.of("plains", "forest", "beach", "river")),
            Map.entry("mint", List.of()),
            Map.entry("jueyun_chili", List.of("savanna", "savanna_plateau", "windswept_savanna", "windswept_hills", "windswept_gravelly_hills", "windswept_forest", "stony_peaks")),
            Map.entry("glaze_lily", List.of("flower_forest", "jungle", "sparse_jungle", "bamboo_jungle", "swamp", "mangrove_swamp")),
            Map.entry("mufeng_mushroom", List.of("plains", "meadow")),
            Map.entry("sumeru_rose", List.of("flower_forest", "jungle", "sparse_jungle", "bamboo_jungle")),
            Map.entry("grainfruit", List.of("savanna", "savanna_plateau", "desert", "warped_forest")),
            Map.entry("horsetail", List.of("river", "swamp", "mangrove_swamp")),
            Map.entry("jinxin_flower", List.of("badlands", "eroded_badlands", "wooded_badlands", "nether_wastes", "crimson_forest")),
            Map.entry("fluorescent_fungus", List.of("dark_forest", "deep_dark", "soul_sand_valley")),
            Map.entry("sea_ganoderma", List.of("ocean", "lukewarm_ocean", "warm_ocean")),
            Map.entry("dendrobium", List.of("soul_sand_valley", "crimson_forest", "nether_wastes")),
            Map.entry("frostlamp_flower", List.of("beach", "frozen_river", "snowy_beach")),
            Map.entry("yunyan_lieye", List.of("stony_peaks", "windswept_hills", "windswept_gravelly_hills", "windswept_forest", "eroded_badlands", "badlands", "wooded_badlands")),
            Map.entry("windblume", List.of("meadow")),
            Map.entry("pink_windblume", List.of("meadow")),
            Map.entry("yellow_windblume", List.of("meadow")),
            Map.entry("purple_windblume", List.of("meadow")),
            Map.entry("wolfhook", List.of("taiga", "old_growth_pine_taiga", "old_growth_spruce_taiga")),
            Map.entry("snapdragon", List.of("plains", "forest", "beach", "river")),
            Map.entry("valberry", List.of("forest", "flower_forest", "birch_forest")),
            Map.entry("dandelion_seeds", List.of("plains", "windswept_hills", "meadow")),
            Map.entry("cecilia", List.of("meadow")),
            Map.entry("raspberry", List.of("birch_forest", "forest", "dark_forest")),
            Map.entry("sweet_flower", List.of()),
            Map.entry("shipo", List.of("windswept_hills", "windswept_gravelly_hills", "windswept_forest", "stony_shore", "stony_peaks")),
            Map.entry("yeboshi", List.of("deep_dark", "dripstone_caves", "stony_peaks")),
            Map.entry("jinghuagusui", List.of()),
            Map.entry("starconch", List.of("beach")),
            Map.entry("coral_pearl", List.of("beach", "stony_shore", "warm_ocean")),
            Map.entry("empty_feather_moth", List.of("meadow", "jagged_peaks", "stony_peaks"))
    );

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(KatheryneScreen.class, new KatheryneGuiHandler());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<IJeiIngredientInfoRecipe> infoRecipes = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : NATURAL_BIOMES.entrySet()) {
            String id = entry.getKey();
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, id));
            if (item == Items.AIR) continue;

            MutableComponent source = Component.translatable("jei.teyvatdelight.natural_biomes").append(" ");
            List<String> biomes = entry.getValue();
            if (biomes.isEmpty()) {
                source.append(Component.translatable("jei.teyvatdelight.source." + id));
            } else {
                for (int i = 0; i < biomes.size(); i++) {
                    if (i > 0) source.append(Component.translatable("jei.teyvatdelight.separator"));
                    source.append(Component.translatable("biome.minecraft." + biomes.get(i)));
                }
            }

            List<FormattedText> description = new ArrayList<>();
            description.add(source);
            String noteKey = "jei.teyvatdelight.note." + id;
            if (Language.getInstance().has(noteKey)) {
                description.add(id.equals("windblume")
                        ? Component.translatable(noteKey, Component.translatable("block.teyvatdelight.ni_ci_zhi_field"))
                        : Component.translatable(noteKey));
            }
            if (TeyvatCropApi.findDefinitions(new ItemStack(item)).stream()
                    .anyMatch(crop -> crop.produce() == item && crop.plantingItem() == item)) {
                description.add(Component.translatable("jei.teyvatdelight.plantable"));
            }
            addInformation(registration, infoRecipes, item, description);
        }

        List<? extends Item> merchantIngredients = TeyvatMerchantCatalog.INGREDIENTS.stream()
                .map(supplier -> supplier.get()).toList();
        for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(TeyvatTags.Items.TEYVAT_FOOD_ITEMS)) {
            Item item = holder.value();
            boolean soldByMerchant = merchantIngredients.contains(item);
            Component source;
            if (soldByMerchant) {
                source = Component.translatable("jei.teyvatdelight.ingredient_merchant");
            } else {
                String key = "jei.teyvatdelight.ingredient_source." + BuiltInRegistries.ITEM.getKey(item).getPath();
                // Recipe acquisition is already displayed by JEI; only register additional source notes.
                if (!Language.getInstance().has(key)) {
                    continue;
                }
                source = Component.translatable(key);
            }
            addInformation(registration, infoRecipes, item,
                    List.of(Component.translatable("jei.teyvatdelight.acquisition"), source));
        }
        addInformation(registration, infoRecipes, com.guoche.teyvatdelight.registry.ModItems.PRIMOGEM.get(),
                List.of(Component.translatable("jei.teyvatdelight.source.primogem")));
        addInformation(registration, infoRecipes, com.guoche.teyvatdelight.registry.ModItems.MORA.get(),
                List.of(Component.translatable("jei.teyvatdelight.source.mora")));
        registration.addRecipes(RecipeTypes.INFORMATION, infoRecipes);
    }

    private static void addInformation(IRecipeRegistration registration, List<IJeiIngredientInfoRecipe> recipes,
            Item item, List<FormattedText> description) {
        registration.getIngredientManager().createTypedIngredient(VanillaTypes.ITEM_STACK, new ItemStack(item), false)
                .ifPresent(ingredient -> recipes.add(new SpecialtyInfoRecipe(ingredient, description)));
    }

    private record SpecialtyInfoRecipe(ITypedIngredient<ItemStack> ingredient, List<FormattedText> description)
            implements IJeiIngredientInfoRecipe {
        private SpecialtyInfoRecipe {
            description = List.copyOf(description);
        }

        @Override
        public List<ITypedIngredient<?>> getIngredients() {
            return List.of(ingredient);
        }

        @Override
        public List<FormattedText> getDescription() {
            return description;
        }
    }
}
