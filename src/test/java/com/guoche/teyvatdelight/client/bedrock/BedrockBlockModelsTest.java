package com.guoche.teyvatdelight.client.bedrock;

import com.mojang.blaze3d.platform.NativeImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class BedrockBlockModelsTest {
    @TempDir Path directory;
    private static final OriginalModel ORIGINAL = new OriginalModel();
    private static final String BONES = "[{\"name\":\"root\",\"cubes\":[{\"origin\":[-4,0,-2],\"size\":[8,4,4],\"uv\":[0,0]}]}]";

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @AfterEach
    void clearPending() { BedrockSpriteSource.takeEntries(); }

    @Test
    void bindsAllBlockStatesAndItemAndDoesNotLeakIntoNextReload() throws Exception {
        model("stone", null);
        scan();
        Map<ModelResourceLocation, BakedModel> models = originals(Blocks.STONE, "stone");
        try (SpriteContents contents = contents()) {
            bake(models, new Sprite(contents));
            BakedModel baked = models.get(BlockModelShaper.stateToModelLocation(Blocks.STONE.defaultBlockState()));
            assertInstanceOf(BedrockBakedModel.class, baked);
            assertEquals(6, baked.getQuads(null, null, RandomSource.create()).size());
            assertFalse(baked.isCustomRenderer());
            assertInstanceOf(BedrockBakedModel.class, models.get(new ModelResourceLocation(ResourceLocation.parse("minecraft:stone"), "inventory")));
            Map<ModelResourceLocation, BakedModel> nextReload = originals(Blocks.STONE, "stone");
            bake(nextReload, new Sprite(contents));
            assertTrue(nextReload.values().stream().allMatch(model -> model == ORIGINAL));
        }
    }

    @Test
    void stateSelectorChangesOnlyMatchingStateAndPreservesDefaultItem() throws Exception {
        model("wheat", "{\"states\":{\"age\":7}}");
        scan();
        Map<ModelResourceLocation, BakedModel> models = originals(Blocks.WHEAT, "wheat");
        try (SpriteContents contents = contents()) {
            bake(models, new Sprite(contents));
            long changed = models.values().stream().filter(model -> model instanceof BedrockBakedModel).count();
            assertEquals(1, changed);
            assertSame(ORIGINAL, models.get(BlockModelShaper.stateToModelLocation(Blocks.WHEAT.defaultBlockState())));
            assertSame(ORIGINAL, models.get(new ModelResourceLocation(ResourceLocation.parse("minecraft:wheat"), "inventory")));
        }
    }

    @Test
    void malformedSelectorsAndMissingSpritesKeepOriginalModels() throws Exception {
        model("stone", "{\"states\":{\"does_not_exist\":true}}");
        scan();
        Map<ModelResourceLocation, BakedModel> models = originals(Blocks.STONE, "stone");
        try (SpriteContents contents = contents()) {
            bake(models, new Sprite(contents));
            assertTrue(models.values().stream().allMatch(model -> model == ORIGINAL));
        }
        model("stone", "{}");
        scan();
        try (SpriteContents contents = new SpriteContents(ResourceLocation.parse("minecraft:missingno"),
                new FrameSize(64, 32), new NativeImage(64, 32, false), ResourceMetadata.EMPTY)) {
            bake(models, new Sprite(contents));
            assertTrue(models.values().stream().allMatch(model -> model == ORIGINAL));
        }
    }

    @Test
    void horizontalFacingRotatesVerticesAndUvStaysWithMesh() throws Exception {
        model("furnace", null);
        scan();
        Map<ModelResourceLocation, BakedModel> models = originals(Blocks.FURNACE, "furnace");
        try (SpriteContents contents = contents()) {
            bake(models, new Sprite(contents));
            BakedModel north = models.get(BlockModelShaper.stateToModelLocation(Blocks.FURNACE.defaultBlockState()));
            BlockState eastState = Blocks.FURNACE.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
            BakedModel east = models.get(BlockModelShaper.stateToModelLocation(eastState));
            int[] northVertices = north.getQuads(null, null, RandomSource.create()).getFirst().getVertices();
            int[] eastVertices = east.getQuads(null, null, RandomSource.create()).getFirst().getVertices();
            assertEquals(0.75F, Float.intBitsToFloat(northVertices[0]), 0.00001F);
            assertEquals(0.375F, Float.intBitsToFloat(northVertices[2]), 0.00001F);
            assertEquals(0.625F, Float.intBitsToFloat(eastVertices[0]), 0.00001F);
            assertEquals(0.75F, Float.intBitsToFloat(eastVertices[2]), 0.00001F);
            assertEquals(northVertices[4], eastVertices[4]);
            assertEquals(northVertices[5], eastVertices[5]);
        }
    }

    private void model(String id, String settings) throws Exception {
        write("assets/minecraft/bedrock/blocks/" + id + ".geo.json", BedrockGeometryTest.geometry(BONES).toString());
        write("assets/minecraft/textures/bedrock/blocks/" + id + ".png", "placeholder");
        if (settings != null) write("assets/minecraft/bedrock/blocks/" + id + ".model.json", settings);
    }

    private void write(String relative, String data) throws Exception {
        Path file = directory.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, data);
    }

    private void scan() {
        try (var resources = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, List.of(new PathPackResources(
                new PackLocationInfo("test", Component.literal("test"), PackSource.DEFAULT, Optional.empty()), directory)))) {
            BedrockSpriteSource.INSTANCE.run(resources, new SpriteSource.Output() {
                @Override public void add(ResourceLocation id, SpriteSource.SpriteSupplier sprite) { }
                @Override public void removeAll(Predicate<ResourceLocation> predicate) { }
            });
        }
    }

    private static Map<ModelResourceLocation, BakedModel> originals(Block block, String itemId) {
        Map<ModelResourceLocation, BakedModel> models = new HashMap<>();
        for (BlockState state : block.getStateDefinition().getPossibleStates()) models.put(BlockModelShaper.stateToModelLocation(state), ORIGINAL);
        models.put(new ModelResourceLocation(ResourceLocation.withDefaultNamespace(itemId), "inventory"), ORIGINAL);
        return models;
    }

    private static SpriteContents contents() {
        // A test texture getter supplies the requested sprite ID for each scanned file.
        return new SpriteContents(ResourceLocation.parse("minecraft:bedrock/blocks/stone"),
                new FrameSize(64, 32), new NativeImage(64, 32, false), ResourceMetadata.EMPTY);
    }

    private static void bake(Map<ModelResourceLocation, BakedModel> models, Sprite texture) {
        BedrockBlockModels.bakeModels(new ModelEvent.ModifyBakingResult(models, material -> {
            if (texture.contents().name().getPath().equals("missingno")) return texture;
            return new Sprite(new SpriteContents(material.texture(), new FrameSize(64, 32),
                    texture.contents().getOriginalImage(), ResourceMetadata.EMPTY));
        }, null));
    }

    private static class Sprite extends TextureAtlasSprite {
        Sprite(SpriteContents contents) { super(TextureAtlas.LOCATION_BLOCKS, contents, 64, 32, 0, 0); }
    }

    private static class OriginalModel implements BakedModel {
        @Override public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource random) { return List.of(); }
        @Override public boolean useAmbientOcclusion() { return false; }
        @Override public boolean isGui3d() { return true; }
        @Override public boolean usesBlockLight() { return true; }
        @Override public boolean isCustomRenderer() { return false; }
        @Override public TextureAtlasSprite getParticleIcon() { return null; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
    }
}
