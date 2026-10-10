package com.guoche.teyvatdelight.client.bedrock;

import com.guoche.teyvatdelight.TeyvatDelight;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = TeyvatDelight.MODID, value = Dist.CLIENT)
public final class BedrockBlockModels {
    private static final Logger LOGGER = LogUtils.getLogger();
    private BedrockBlockModels() {}

    @SubscribeEvent
    public static void registerSpriteSource(RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "bedrock_blocks"), BedrockSpriteSource.TYPE);
    }

    @SubscribeEvent
    public static void bakeModels(ModelEvent.ModifyBakingResult event) {
        Set<ModelResourceLocation> claimed = new HashSet<>();
        int count = 0;
        for (BedrockSpriteSource.Entry entry : BedrockSpriteSource.takeEntries()) {
            try {
                BedrockModelSettings settings = entry.settings();
                if (!BuiltInRegistries.BLOCK.containsKey(settings.block())) {
                    throw new IllegalArgumentException("Block is not registered: " + settings.block());
                }
                Block block = BuiltInRegistries.BLOCK.get(settings.block());
                validateStates(block, settings.states());
                TextureAtlasSprite sprite = event.getTextureGetter().apply(
                        new Material(TextureAtlas.LOCATION_BLOCKS, settings.texture()));
                if (!sprite.contents().name().equals(settings.texture())) {
                    throw new IllegalArgumentException("Texture failed to enter atlas: " + settings.texture());
                }
                // Build and validate the whole file before changing any registry entries.
                Map<ModelResourceLocation, BakedModel> replacements = new HashMap<>();
                Map<Direction, BakedModel> facingModels = new HashMap<>();
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    if (!matches(state, settings.states())) continue;
                    ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                    BakedModel original = event.getModels().get(location);
                    if (original == null) throw new IllegalArgumentException("No original model for " + location);
                    Direction facing = facing(state);
                    BakedModel baked = facingModels.computeIfAbsent(facing,
                            direction -> new BedrockBakedModel(entry.mesh(), settings, sprite, original.getTransforms(), direction));
                    replacements.put(location, baked);
                }
                if (replacements.isEmpty()) throw new IllegalArgumentException("No block states match states selector");
                // A state-specific override changes the item only when it also matches the default state.
                if (matches(block.defaultBlockState(), settings.states())) {
                    ResourceLocation item = BuiltInRegistries.ITEM.getKey(block.asItem());
                    if (!item.equals(ResourceLocation.withDefaultNamespace("air"))) {
                        ModelResourceLocation location = new ModelResourceLocation(item, "inventory");
                        BakedModel original = event.getModels().get(location);
                        if (original != null) replacements.put(location,
                                new BedrockBakedModel(entry.mesh(), settings, sprite, original.getTransforms(), Direction.NORTH));
                    }
                }
                for (ModelResourceLocation location : replacements.keySet()) {
                    if (claimed.contains(location)) throw new IllegalArgumentException("Overlapping model binding: " + location);
                }
                event.getModels().putAll(replacements);
                claimed.addAll(replacements.keySet());
                count++;
            } catch (Exception exception) {
                LOGGER.warn("Keeping original models for Bedrock file {}: {}", entry.source(), exception.toString());
            }
        }
        LOGGER.info("Applied {} Bedrock block model binding(s)", count);
    }

    private static void validateStates(Block block, Map<String, String> states) {
        states.forEach((name, value) -> {
            Property<?> property = block.getStateDefinition().getProperty(name);
            if (property == null || property.getValue(value).isEmpty()) {
                throw new IllegalArgumentException("Unknown block state property/value: " + name + "=" + value);
            }
        });
    }

    private static boolean matches(BlockState state, Map<String, String> selectors) {
        return selectors.entrySet().stream().allMatch(entry ->
                valueName(state, state.getBlock().getStateDefinition().getProperty(entry.getKey())).equals(entry.getValue()));
    }

    private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static Direction facing(BlockState state) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING);
        return Direction.NORTH;
    }

    /** Existing special block renderers can opt in once and retain their normal fallback. */
    public static boolean renderOverride(BlockState state, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (!(model instanceof BedrockBakedModel bedrock)) return false;
        bedrock.render(pose, buffers, light, overlay);
        return true;
    }
}
