package com.guoche.teyvatdelight.client.bedrock;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class BedrockSpriteSourceTest {
    @TempDir Path directory;
    private static final String CUBE = "[{\"name\":\"root\",\"cubes\":[{\"origin\":[0,0,0],\"size\":[4,6,2],\"uv\":[0,0]}]}]";

    @AfterEach
    void clearPendingModels() { BedrockSpriteSource.takeEntries(); }

    @Test
    void recursivelyScansMultipleNamespacesAndIgnoresOtherJson() throws Exception {
        Path low = directory.resolve("low");
        model(low, "teyvatdelight", "one");
        model(low, "another", "folder/two");
        write(low, "assets/teyvatdelight/bedrock/blocks/unrelated.json", "{}");
        try (var resources = manager(low)) {
            Output output = new Output();
            BedrockSpriteSource.INSTANCE.run(resources, output);
            var loaded = BedrockSpriteSource.takeEntries();
            assertEquals(2, loaded.size());
            assertEquals(2, output.sprites.size());
            assertTrue(loaded.stream().anyMatch(entry -> entry.settings().block().toString().equals("another:folder/two")));
        }
    }

    @Test
    void malformedAndMissingTextureFilesDoNotBreakValidModels() throws Exception {
        Path low = directory.resolve("low");
        model(low, "test", "valid");
        write(low, "assets/test/bedrock/blocks/broken.geo.json", "not json");
        write(low, "assets/test/bedrock/blocks/no_texture.geo.json", BedrockGeometryTest.geometry(CUBE).toString());
        try (var resources = manager(low)) {
            BedrockSpriteSource.INSTANCE.run(resources, new Output());
            var loaded = BedrockSpriteSource.takeEntries();
            assertEquals(1, loaded.size());
            assertEquals("test:valid", loaded.getFirst().settings().block().toString());
        }
    }

    @Test
    void higherPriorityPackOverridesGeometryAndSettings() throws Exception {
        Path low = directory.resolve("low"), high = directory.resolve("high");
        model(low, "test", "one");
        write(high, "assets/test/bedrock/blocks/one.geo.json",
                BedrockGeometryTest.geometry(CUBE.replace("[4,6,2]", "[8,6,2]")).toString());
        write(high, "assets/test/bedrock/blocks/one.model.json", "{\"rotation\":[0,90,0],\"states\":{\"age\":7}}");
        try (var resources = manager(low, high)) {
            BedrockSpriteSource.INSTANCE.run(resources, new Output());
            var loaded = BedrockSpriteSource.takeEntries();
            assertEquals(1, loaded.size());
            var entry = loaded.getFirst();
            // The winning Bedrock cube spans Java X [-8,0], not [-4,0].
            var vertices = entry.mesh().faces().getFirst().vertices();
            assertEquals(0, vertices.getFirst().position().x());
            assertEquals(-8, vertices.get(2).position().x());
            assertEquals(90, entry.settings().rotation().y());
            assertEquals("7", entry.settings().states().get("age"));
        }
    }

    @Test
    void reloadReplacesSnapshotAndRemovingPackClearsOverrides() throws Exception {
        Path low = directory.resolve("low");
        model(low, "test", "one");
        try (var resources = manager(low)) {
            BedrockSpriteSource.INSTANCE.run(resources, new Output());
            assertEquals(1, BedrockSpriteSource.takeEntries().size());
            assertTrue(BedrockSpriteSource.takeEntries().isEmpty());
            BedrockSpriteSource.INSTANCE.run(resources, new Output());
            BedrockSpriteSource.INSTANCE.run(ResourceManager.Empty.INSTANCE, new Output());
            assertTrue(BedrockSpriteSource.takeEntries().isEmpty());
        }
    }

    @Test
    void optionalSettingsCanBindAnotherBlockAndReuseExistingTexture() throws Exception {
        Path low = directory.resolve("low");
        write(low, "assets/test/bedrock/blocks/custom.geo.json", BedrockGeometryTest.geometry(CUBE).toString());
        write(low, "assets/test/bedrock/blocks/custom.model.json", """
                {"block":"teyvatdelight:primogem_block","texture":"test:block/existing",
                 "render_type":"translucent","scale":[0.5,0.5,0.5],"auto_rotate":false}
                """);
        write(low, "assets/test/textures/block/existing.png", "texture placeholder");
        try (var resources = manager(low)) {
            Output output = new Output();
            BedrockSpriteSource.INSTANCE.run(resources, output);
            var settings = BedrockSpriteSource.takeEntries().getFirst().settings();
            assertEquals("teyvatdelight:primogem_block", settings.block().toString());
            assertEquals("translucent", settings.renderType());
            assertEquals(0.5F, settings.scale().x());
            assertFalse(settings.autoRotate());
            assertTrue(output.sprites.containsKey(ResourceLocation.parse("test:block/existing")));
        }
    }

    @Test
    void rejectsInvalidSettingsInsteadOfSilentlyMisrendering() {
        for (String settings : List.of("{\"render_type\":\"invalid\"}", "{\"scale\":[-1,1,1]}", "{\"rotation\":[90]}")) {
            assertThrows(IllegalArgumentException.class, () -> BedrockModelSettings.read(
                    ResourceLocation.parse("test:one"), JsonParser.parseString(settings).getAsJsonObject()));
        }
    }

    private static void model(Path root, String namespace, String name) throws Exception {
        write(root, "assets/" + namespace + "/bedrock/blocks/" + name + ".geo.json", BedrockGeometryTest.geometry(CUBE).toString());
        write(root, "assets/" + namespace + "/textures/bedrock/blocks/" + name + ".png", "texture placeholder");
    }

    private static void write(Path root, String relative, String content) throws Exception {
        Path file = root.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    private static MultiPackResourceManager manager(Path... roots) {
        return new MultiPackResourceManager(PackType.CLIENT_RESOURCES,
                java.util.Arrays.stream(roots).<net.minecraft.server.packs.PackResources>map(root -> new PathPackResources(
                        new PackLocationInfo(root.getFileName().toString(), Component.literal("test"), PackSource.DEFAULT, Optional.empty()), root)).toList());
    }

    private static final class Output implements SpriteSource.Output {
        final Map<ResourceLocation, SpriteSource.SpriteSupplier> sprites = new HashMap<>();
        @Override public void add(ResourceLocation id, SpriteSource.SpriteSupplier sprite) { sprites.put(id, sprite); }
        @Override public void removeAll(Predicate<ResourceLocation> predicate) { sprites.keySet().removeIf(predicate); }
    }
}
