package com.guoche.teyvatdelight.client.bedrock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

/** Scan with the reload's resource manager, before atlas stitching and model baking finish. */
public final class BedrockSpriteSource implements SpriteSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DIRECTORY = "bedrock/blocks/";
    private static final String SUFFIX = ".geo.json";
    public static final BedrockSpriteSource INSTANCE = new BedrockSpriteSource();
    public static final MapCodec<BedrockSpriteSource> CODEC = MapCodec.unit(INSTANCE);
    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);
    record Entry(ResourceLocation source, BedrockModelSettings settings, BedrockGeometry.Mesh mesh) {}
    // Publish one immutable snapshot, including the empty snapshot when all overrides are removed.
    private static final AtomicReference<List<Entry>> ENTRIES = new AtomicReference<>(List.of());

    private BedrockSpriteSource() {}

    static List<Entry> takeEntries() { return ENTRIES.getAndSet(List.of()); }

    @Override
    public void run(ResourceManager resources, Output output) {
        List<Entry> loaded = new ArrayList<>();
        Map<ResourceLocation, Resource> files = new TreeMap<>(resources.listResources("bedrock/blocks",
                id -> id.getPath().endsWith(SUFFIX)));
        for (Map.Entry<ResourceLocation, Resource> file : files.entrySet()) {
            ResourceLocation source = file.getKey();
            try {
                String path = source.getPath().substring(DIRECTORY.length(), source.getPath().length() - SUFFIX.length());
                ResourceLocation id = source.withPath(path);
                ResourceLocation settingsFile = source.withPath(DIRECTORY + path + ".model.json");
                JsonObject settingsJson = resources.getResource(settingsFile).isPresent()
                        ? read(resources.getResource(settingsFile).orElseThrow()) : new JsonObject();
                BedrockModelSettings settings = BedrockModelSettings.read(id, settingsJson);
                BedrockGeometry.Mesh mesh = BedrockGeometry.read(read(file.getValue()), settings.geometry());
                Resource texture = resources.getResource(TEXTURE_ID_CONVERTER.idToFile(settings.texture()))
                        .orElseThrow(() -> new IllegalArgumentException("Missing texture: " + settings.texture()));
                output.add(settings.texture(), texture);
                loaded.add(new Entry(source, settings, mesh));
            } catch (Exception exception) {
                LOGGER.warn("Skipping Bedrock block model {} from pack {}: {}", source,
                        file.getValue().sourcePackId(), exception.toString());
            }
        }
        ENTRIES.set(List.copyOf(loaded));
        LOGGER.info("Scanned {} Bedrock block model(s), accepted {}", files.size(), loaded.size());
    }

    private static JsonObject read(Resource resource) throws java.io.IOException {
        try (Reader reader = resource.openAsReader()) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    @Override
    public SpriteSourceType type() { return TYPE; }
}
