package com.guoche.teyvatdelight.client.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Parses modern Bedrock geometry without depending on Minecraft's renderer. */
final class BedrockGeometry {
    record Point(float x, float y, float z) {
        Vector3f vector() { return new Vector3f(x, y, z); }
    }

    record Vertex(Point position, float u, float v) {}
    record Face(List<Vertex> vertices) {}
    record Mesh(int textureWidth, int textureHeight, List<Face> faces) {}
    private record Bone(JsonObject json, String parent) {}
    private static final Point ZERO = new Point(0, 0, 0);

    private BedrockGeometry() {}

    static Mesh read(JsonObject file, String geometryId) {
        JsonArray geometries = required(file, "minecraft:geometry").getAsJsonArray();
        JsonObject geometry = null;
        for (JsonElement element : geometries) {
            JsonObject candidate = element.getAsJsonObject();
            String id = required(candidate, "description").getAsJsonObject().get("identifier").getAsString();
            if (geometryId == null || id.equals(geometryId)) {
                if (geometry != null) throw new IllegalArgumentException("Multiple geometries; specify geometry in .model.json");
                geometry = candidate;
            }
        }
        if (geometry == null) throw new IllegalArgumentException("Geometry not found: " + geometryId);
        JsonObject description = required(geometry, "description").getAsJsonObject();
        int width = positiveInteger(description, "texture_width");
        int height = positiveInteger(description, "texture_height");
        Map<String, Bone> bones = new LinkedHashMap<>();
        JsonArray boneList = required(geometry, "bones").getAsJsonArray();
        if (boneList.size() > 2048) throw new IllegalArgumentException("Too many bones (maximum 2048)");
        for (JsonElement element : boneList) {
            JsonObject bone = element.getAsJsonObject();
            String name = required(bone, "name").getAsString();
            if (name.isBlank() || bones.putIfAbsent(name,
                    new Bone(bone, bone.has("parent") ? bone.get("parent").getAsString() : null)) != null) {
                throw new IllegalArgumentException("Empty or duplicate bone name: " + name);
            }
            // Meshes and animated bindings cannot be represented by a static cuboid model.
            for (String unsupported : List.of("poly_mesh", "binding")) {
                if (bone.has(unsupported)) throw new IllegalArgumentException("Unsupported bone field: " + unsupported);
            }
        }
        Map<String, Matrix4f> transforms = new HashMap<>();
        List<Face> faces = new ArrayList<>();
        int cubes = 0;
        for (Map.Entry<String, Bone> entry : bones.entrySet()) {
            Matrix4f transform = boneTransform(entry.getKey(), bones, transforms, new ArrayList<>());
            JsonObject bone = entry.getValue().json;
            if (!bone.has("cubes")) continue;
            for (JsonElement element : bone.getAsJsonArray("cubes")) {
                if (++cubes > 32768) throw new IllegalArgumentException("Too many cubes (maximum 32768)");
                cubeFaces(element.getAsJsonObject(), bone, transform, faces);
            }
        }
        if (faces.isEmpty()) throw new IllegalArgumentException("Geometry has no visible cuboid faces");
        for (Face face : faces) {
            for (Vertex vertex : face.vertices) {
                if (!Float.isFinite(vertex.u) || !Float.isFinite(vertex.v)
                        || vertex.u < 0 || vertex.u > width || vertex.v < 0 || vertex.v > height) {
                    throw new IllegalArgumentException("UV outside texture dimensions; atlas textures cannot wrap");
                }
            }
        }
        return new Mesh(width, height, List.copyOf(faces));
    }

    private static Matrix4f boneTransform(String name, Map<String, Bone> bones,
                                           Map<String, Matrix4f> completed, List<String> visiting) {
        if (completed.containsKey(name)) return completed.get(name);
        Bone bone = bones.get(name);
        if (bone == null) throw new IllegalArgumentException("Missing parent bone: " + name);
        if (visiting.contains(name)) throw new IllegalArgumentException("Bone parent cycle: " + visiting + " -> " + name);
        if (visiting.size() >= 128) throw new IllegalArgumentException("Bone hierarchy exceeds 128 levels");
        visiting.add(name);
        Matrix4f transform = bone.parent == null ? new Matrix4f()
                : new Matrix4f(boneTransform(bone.parent, bones, completed, visiting));
        rotateBedrockAround(transform, point(bone.json, "pivot", ZERO), point(bone.json, "rotation", ZERO));
        visiting.removeLast();
        completed.put(name, transform);
        return transform;
    }

    static void rotateAround(Matrix4f transform, Point pivot, Point degrees) {
        float radians = (float) (Math.PI / 180);
        transform.translate(pivot.x, pivot.y, pivot.z)
                .rotateZYX(degrees.z * radians, degrees.y * radians, degrees.x * radians)
                .translate(-pivot.x, -pivot.y, -pivot.z);
    }

    private static void rotateBedrockAround(Matrix4f transform, Point pivot, Point degrees) {
        // Bedrock geometry uses the opposite X axis and X/Y rotation signs.
        // Keep settings.rotation in Java coordinates; convert only .geo.json data.
        rotateAround(transform, new Point(-pivot.x, pivot.y, pivot.z),
                new Point(-degrees.x, -degrees.y, degrees.z));
    }

    private static void cubeFaces(JsonObject cube, JsonObject bone, Matrix4f boneTransform, List<Face> faces) {
        Point origin = point(cube, "origin", null);
        Point size = point(cube, "size", null);
        float inflate = number(cube, "inflate", number(bone, "inflate", 0));
        boolean mirror = bool(cube, "mirror", bool(bone, "mirror", false));
        // Convert bounds before assigning named faces. Reflecting vertices alone
        // would swap east/west materials and reverse the face winding.
        float x0 = -(origin.x + size.x) - inflate, y0 = origin.y - inflate, z0 = origin.z - inflate;
        float x1 = -origin.x + inflate, y1 = origin.y + size.y + inflate, z1 = origin.z + size.z + inflate;
        // Blockbench can export signed sizes for inward-facing geometry. Keep the
        // endpoint order (and therefore winding/UV orientation), not absolute sizes.
        if ((size.x >= 0 ? x1 < x0 : x1 > x0)
                || (size.y >= 0 ? y1 < y0 : y1 > y0)
                || (size.z >= 0 ? z1 < z0 : z1 > z0)) {
            throw new IllegalArgumentException("Inflation inverts cube bounds");
        }
        Matrix4f transform = new Matrix4f(boneTransform);
        rotateBedrockAround(transform, point(cube, "pivot", point(bone, "pivot", ZERO)), point(cube, "rotation", ZERO));
        JsonElement uv = required(cube, "uv");
        // Clockwise UV corners (top-left, bottom-left, bottom-right, top-right), outward winding.
        face(faces, transform, uv, "north", mirror, size,
                new Point(x1, y1, z0), new Point(x1, y0, z0), new Point(x0, y0, z0), new Point(x0, y1, z0));
        face(faces, transform, uv, "south", mirror, size,
                new Point(x0, y1, z1), new Point(x0, y0, z1), new Point(x1, y0, z1), new Point(x1, y1, z1));
        face(faces, transform, uv, "west", mirror, size,
                new Point(x0, y1, z0), new Point(x0, y0, z0), new Point(x0, y0, z1), new Point(x0, y1, z1));
        face(faces, transform, uv, "east", mirror, size,
                new Point(x1, y1, z1), new Point(x1, y0, z1), new Point(x1, y0, z0), new Point(x1, y1, z0));
        face(faces, transform, uv, "up", mirror, size,
                new Point(x0, y1, z0), new Point(x0, y1, z1), new Point(x1, y1, z1), new Point(x1, y1, z0));
        face(faces, transform, uv, "down", mirror, size,
                new Point(x0, y0, z1), new Point(x0, y0, z0), new Point(x1, y0, z0), new Point(x1, y0, z1));
    }

    private static void face(List<Face> faces, Matrix4f transform, JsonElement uvElement, String side,
                             boolean mirror, Point size, Point... corners) {
        float u, v, du, dv;
        int rotation = 0;
        if (uvElement.isJsonArray()) {
            float[] uv = array(uvElement, 2, "uv");
            u = uv[0]; v = uv[1];
            // Mirroring a box also swaps its east/west texture regions.
            String uvSide = mirror && side.equals("west") ? "east" : mirror && side.equals("east") ? "west" : side;
            switch (uvSide) {
                case "east" -> { v += size.z; du = size.z; dv = size.y; }
                case "north" -> { u += size.z; v += size.z; du = size.x; dv = size.y; }
                case "west" -> { u += size.z + size.x; v += size.z; du = size.z; dv = size.y; }
                case "south" -> { u += size.z * 2 + size.x; v += size.z; du = size.x; dv = size.y; }
                case "up" -> { u += size.z + size.x; v += size.z; du = -size.x; dv = -size.z; }
                case "down" -> { u += size.z + size.x * 2; du = -size.x; dv = size.z; }
                default -> throw new IllegalArgumentException("Unknown face: " + side);
            }
        } else {
            JsonObject perFace = uvElement.getAsJsonObject();
            if (!perFace.has(side)) return;
            JsonObject definition = perFace.getAsJsonObject(side);
            if (definition.has("material_instance") && !definition.get("material_instance").getAsString().equals("*")) {
                throw new IllegalArgumentException("Multiple material instances are not supported");
            }
            float[] uv = array(required(definition, "uv"), 2, "uv");
            float[] dimensions = definition.has("uv_size") ? array(definition.get("uv_size"), 2, "uv_size")
                    : switch (side) {
                        case "up", "down" -> new float[]{size.x, size.z};
                        case "west", "east" -> new float[]{size.z, size.y};
                        default -> new float[]{size.x, size.y};
                    };
            u = uv[0]; v = uv[1]; du = dimensions[0]; dv = dimensions[1];
            // Per-face Bedrock top/bottom UV rectangles start at the opposite
            // corner to Java/Blockbench cube faces (including signed UV sizes).
            if (side.equals("up") || side.equals("down")) {
                u += du; v += dv; du = -du; dv = -dv;
            }
            // mirror affects box UV only; explicit per-face rectangles are final.
            mirror = false;
            float angle = number(definition, "uv_rotation", 0);
            if (angle % 90 != 0) throw new IllegalArgumentException("uv_rotation must be a multiple of 90");
            rotation = Math.floorMod((int) angle / 90, 4);
        }
        Vector3f normal = corners[1].vector().sub(corners[0].vector())
                .cross(corners[2].vector().sub(corners[0].vector()));
        if (normal.lengthSquared() < 1.0E-12F) return;
        float[][] uv = {{u, v}, {u, v + dv}, {u + du, v + dv}, {u + du, v}};
        List<Vertex> vertices = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            Vector3f position = transform.transformPosition(corners[i].vector());
            if (!position.isFinite()) throw new IllegalArgumentException("Non-finite transformed vertex");
            int corner = Math.floorMod(i - rotation, 4);
            float textureU = mirror ? u + du - (uv[corner][0] - u) : uv[corner][0];
            vertices.add(new Vertex(new Point(position.x, position.y, position.z), textureU, uv[corner][1]));
        }
        faces.add(new Face(List.copyOf(vertices)));
    }

    static JsonElement required(JsonObject object, String field) {
        if (!object.has(field) || object.get(field).isJsonNull()) throw new IllegalArgumentException("Missing field: " + field);
        return object.get(field);
    }

    static Point point(JsonObject object, String field, Point fallback) {
        if (!object.has(field) && fallback != null) return fallback;
        float[] values = array(required(object, field), 3, field);
        return new Point(values[0], values[1], values[2]);
    }

    private static float[] array(JsonElement element, int length, String field) {
        JsonArray array = element.getAsJsonArray();
        if (array.size() != length) throw new IllegalArgumentException(field + " must contain " + length + " numbers");
        float[] values = new float[length];
        for (int i = 0; i < length; i++) {
            values[i] = array.get(i).getAsFloat();
            if (!Float.isFinite(values[i])) throw new IllegalArgumentException("Non-finite number in " + field);
        }
        return values;
    }

    static float number(JsonObject object, String field, float fallback) {
        float value = object.has(field) ? object.get(field).getAsFloat() : fallback;
        if (!Float.isFinite(value)) throw new IllegalArgumentException("Non-finite number: " + field);
        return value;
    }

    static boolean bool(JsonObject object, String field, boolean fallback) {
        return object.has(field) ? object.get(field).getAsBoolean() : fallback;
    }

    private static int positiveInteger(JsonObject object, String field) {
        float value = number(object, field, -1);
        if (value < 1 || value > 16384 || value != Math.floor(value)) {
            throw new IllegalArgumentException(field + " must be an integer between 1 and 16384");
        }
        return (int) value;
    }
}
