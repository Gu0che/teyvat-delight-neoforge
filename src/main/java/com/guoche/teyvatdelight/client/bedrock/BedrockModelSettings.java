package com.guoche.teyvatdelight.client.bedrock;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

record BedrockModelSettings(ResourceLocation block, ResourceLocation texture, String geometry,
                            Map<String, String> states, BedrockGeometry.Point rotation,
                            BedrockGeometry.Point translation, BedrockGeometry.Point scale,
                            boolean autoRotate, boolean ambientOcclusion, String renderType) {
    static BedrockModelSettings read(ResourceLocation id, JsonObject json) {
        BedrockGeometry.Point zero = new BedrockGeometry.Point(0, 0, 0);
        BedrockGeometry.Point scale = BedrockGeometry.point(json, "scale", new BedrockGeometry.Point(1, 1, 1));
        if (scale.x() <= 0 || scale.y() <= 0 || scale.z() <= 0) {
            throw new IllegalArgumentException("scale must be positive on every axis");
        }
        String renderType = json.has("render_type") ? json.get("render_type").getAsString() : "cutout";
        if (!renderType.equals("solid") && !renderType.equals("cutout") && !renderType.equals("translucent")) {
            throw new IllegalArgumentException("render_type must be solid, cutout or translucent");
        }
        Map<String, String> states = new LinkedHashMap<>();
        if (json.has("states")) json.getAsJsonObject("states").entrySet()
                .forEach(entry -> states.put(entry.getKey(), entry.getValue().getAsString()));
        return new BedrockModelSettings(
                location(json, "block", id),
                location(json, "texture", id.withPath("bedrock/blocks/" + id.getPath())),
                json.has("geometry") ? json.get("geometry").getAsString() : null,
                Map.copyOf(states), BedrockGeometry.point(json, "rotation", zero),
                BedrockGeometry.point(json, "translation", zero), scale,
                BedrockGeometry.bool(json, "auto_rotate", true),
                BedrockGeometry.bool(json, "ambient_occlusion", true), renderType);
    }

    private static ResourceLocation location(JsonObject json, String field, ResourceLocation fallback) {
        return json.has(field) ? ResourceLocation.parse(json.get(field).getAsString()) : fallback;
    }
}
