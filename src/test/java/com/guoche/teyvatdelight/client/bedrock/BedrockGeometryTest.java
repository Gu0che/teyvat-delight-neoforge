package com.guoche.teyvatdelight.client.bedrock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BedrockGeometryTest {
    static JsonObject geometry(String bones) {
        return JsonParser.parseString("""
                {"format_version":"1.12.0","minecraft:geometry":[{
                  "description":{"identifier":"geometry.test","texture_width":64,"texture_height":32},
                  "bones":%s
                }]}
                """.formatted(bones)).getAsJsonObject();
    }

    private static BedrockGeometry.Mesh mesh(String bones) {
        return BedrockGeometry.read(geometry(bones), null);
    }

    @Test
    void boxUvUsesSixDistinctFacesAndOutwardNormals() {
        var model = mesh("""
                [{"name":"root","cubes":[{"origin":[0,0,0],"size":[4,6,2],"uv":[8,4]}]}]
                """);
        assertEquals(6, model.faces().size());
        assertEquals(64, model.textureWidth());
        assertEquals(32, model.textureHeight());
        Vector3f[] normals = {
                new Vector3f(0, 0, -1), new Vector3f(0, 0, 1), new Vector3f(-1, 0, 0),
                new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, -1, 0)};
        float[][] firstUv = {{10,6}, {16,6}, {14,6}, {8,6}, {14,6}, {18,4}};
        for (int i = 0; i < 6; i++) {
            var vertices = model.faces().get(i).vertices();
            var normal = vertices.get(1).position().vector().sub(vertices.get(0).position().vector())
                    .cross(vertices.get(2).position().vector().sub(vertices.get(0).position().vector())).normalize();
            assertTrue(normal.equals(normals[i], 0.00001F), "face " + i);
            assertEquals(firstUv[i][0], vertices.getFirst().u());
            assertEquals(firstUv[i][1], vertices.getFirst().v());
        }
    }

    @Test
    void unorderedBonesInheritParentRotationAroundAbsolutePivot() {
        var model = mesh("""
                [{"name":"child","parent":"root","cubes":[{"origin":[2,0,0],"size":[1,1,1],"uv":[0,0]}]},
                 {"name":"root","pivot":[1,0,0],"rotation":[0,0,90]}]
                """);
        // Bedrock bounds become [-3,-2]; pivot becomes (-1,0,0).
        // North corner (-2,1,0) rotates +90 Z to (-2,-1,0).
        assertPoint(model.faces().getFirst().vertices().getFirst().position(), -2, -1, 0);
    }

    @Test
    void cubeRotationIsAppliedBeforeParentRotation() {
        var model = mesh("""
                [{"name":"root","rotation":[0,0,90],"cubes":[{
                  "origin":[0,0,0],"size":[2,1,1],"pivot":[0,0,0],"rotation":[0,90,0],"uv":[0,0]}]}]
                """);
        // Cube Y rotation becomes -90, parent Z remains +90.
        assertPoint(model.faces().getFirst().vertices().getFirst().position(), -1, 0, 0);
        assertPoint(model.faces().getFirst().vertices().get(2).position(), 0, 0, -2);
    }

    @Test
    void perFaceUvSupportsMissingFacesNegativeSizesAndQuarterTurns() {
        var model = mesh("""
                [{"name":"root","cubes":[{"origin":[0,0,0],"size":[4,6,2],"uv":{
                  "north":{"uv":[12,8],"uv_size":[-4,6],"uv_rotation":90}
                }}]}]
                """);
        assertEquals(1, model.faces().size());
        var vertices = model.faces().getFirst().vertices();
        assertEquals(8, vertices.getFirst().u());
        assertEquals(8, vertices.getFirst().v());
        assertEquals(12, vertices.get(1).u());
        assertEquals(8, vertices.get(1).v());
    }

    @Test
    void boneInflationAndCubeOverridesKeepUvDimensionsUninflated() {
        var model = mesh("""
                [{"name":"root","inflate":1,"mirror":true,"cubes":[
                  {"origin":[0,0,0],"size":[4,6,2],"uv":[8,4]},
                  {"origin":[0,0,0],"size":[4,6,2],"inflate":0,"mirror":false,"uv":[8,4]}
                ]}]
                """);
        assertPoint(model.faces().getFirst().vertices().getFirst().position(), 1, 7, -1);
        assertEquals(14, model.faces().getFirst().vertices().getFirst().u());
        assertEquals(10, model.faces().get(2).vertices().getFirst().u()); // mirrored west
        assertEquals(16, model.faces().get(3).vertices().getFirst().u()); // mirrored east
        assertEquals(10, model.faces().get(4).vertices().getFirst().u()); // mirrored up
        assertPoint(model.faces().get(6).vertices().getFirst().position(), 0, 6, 0);
        assertEquals(10, model.faces().get(6).vertices().getFirst().u());
    }

    @Test
    void zeroThicknessCubeKeepsBothSidesOfPlane() {
        var model = mesh("""
                [{"name":"root","cubes":[{"origin":[0,0,0],"size":[4,6,0],"uv":[0,0]}]}]
                """);
        assertEquals(2, model.faces().size());
    }

    @Test
    void rejectsBrokenBoneHierarchiesAndDuplicateNames() {
        for (String bones : List.of(
                "[{\"name\":\"a\",\"parent\":\"missing\"}]",
                "[{\"name\":\"a\",\"parent\":\"b\"},{\"name\":\"b\",\"parent\":\"a\"}]",
                "[{\"name\":\"a\"},{\"name\":\"a\"}]")) {
            assertThrows(IllegalArgumentException.class, () -> mesh(bones));
        }
    }

    @Test
    void rejectsInvalidDimensionsUvAndEmptyMeshes() {
        for (String cube : List.of(
                "{\"origin\":[0,0,0],\"size\":[-1,1,1],\"uv\":[0,0]}",
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"uv\":[0]}",
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"inflate\":-1,\"uv\":[0,0]}",
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"uv\":{}}",
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"uv\":{\"north\":{\"uv\":[0,0],\"uv_rotation\":45}}}")) {
            assertThrows(IllegalArgumentException.class, () -> mesh("[{\"name\":\"root\",\"cubes\":[" + cube + "]}]"));
        }
        JsonObject invalid = geometry("[]");
        invalid.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject()
                .getAsJsonObject("description").addProperty("texture_width", 0);
        assertThrows(IllegalArgumentException.class, () -> BedrockGeometry.read(invalid, null));
    }

    @Test
    void geometrySelectionMustBeExplicitForMultiGeometryFiles() {
        JsonObject file = geometry("[{\"name\":\"root\",\"cubes\":[{\"origin\":[0,0,0],\"size\":[1,1,1],\"uv\":[0,0]}]}]");
        var second = file.getAsJsonArray("minecraft:geometry").get(0).deepCopy().getAsJsonObject();
        second.getAsJsonObject("description").addProperty("identifier", "geometry.second");
        file.getAsJsonArray("minecraft:geometry").add(second);
        assertThrows(IllegalArgumentException.class, () -> BedrockGeometry.read(file, null));
        assertEquals(6, BedrockGeometry.read(file, "geometry.second").faces().size());
        assertThrows(IllegalArgumentException.class, () -> BedrockGeometry.read(file, "geometry.absent"));
    }

    @Test
    void signedCubeSizesPreserveInwardWindingAndUvOrder() {
        var model = mesh("""
                [{"name":"inner","cubes":[{"origin":[4,6,4],"size":[-8,-1,-8],"inflate":-0.1,
                  "uv":{"up":{"uv":[8,8],"uv_size":[-8,-8]}}}]}]
                """);
        assertEquals(1, model.faces().size());
        var vertices = model.faces().getFirst().vertices();
        assertPoint(vertices.getFirst().position(), 4.1F, 4.9F, 4.1F);
        assertEquals(0, vertices.getFirst().u());
        assertEquals(8, vertices.get(2).u());
        assertEquals(8, vertices.get(2).v());
        Vector3f normal = vertices.get(1).position().vector().sub(vertices.get(0).position().vector())
                .cross(vertices.get(2).position().vector().sub(vertices.get(0).position().vector())).normalize();
        // The original up face lies at the lower endpoint and faces the cube interior.
        assertEquals(1, normal.y(), 0.00001F);
    }

    @Test
    void copiedUvRegionsKeepEveryCubeFace() {
        var model = mesh("""
                [{"name":"shared_uv","cubes":[
                  {"origin":[0,0,0],"size":[4,4,4],"uv":[0,0]},
                  {"origin":[8,0,0],"size":[4,4,4],"uv":[0,0]}
                ]}]
                """);
        assertEquals(12, model.faces().size());
        for (int face = 0; face < 6; face++) {
            for (int corner = 0; corner < 4; corner++) {
                var a = model.faces().get(face).vertices().get(corner);
                var b = model.faces().get(face + 6).vertices().get(corner);
                assertEquals(a.u(), b.u());
                assertEquals(a.v(), b.v());
                assertEquals(a.position().x() - 8, b.position().x(), 0.00001F);
            }
        }
    }

    @Test
    void bedrockExportKeepsDistinctInnerAndOuterWallMaterials() {
        // Blockbench Java wall: from [13.2,9.1,3.2], to [14.1,12.6,12.6].
        // Export recentres X/Z by 8 and reverses X, but keeps east/west labels.
        var model = mesh("""
                [{"name":"wall","cubes":[{"origin":[-6.1,9.1,-4.8],"size":[0.9,3.5,9.4],"uv":{
                  "east":{"uv":[9,27],"uv_size":[9,4]},
                  "west":{"uv":[29,7],"uv_size":[9,3]}
                }}]}]
                """);
        assertEquals(2, model.faces().size());
        var inner = model.faces().get(0).vertices(); // west
        var outer = model.faces().get(1).vertices(); // east
        for (var vertex : inner) assertEquals(5.2F, vertex.position().x(), 0.00001F);
        for (var vertex : outer) assertEquals(6.1F, vertex.position().x(), 0.00001F);
        assertEquals(29, inner.getFirst().u());
        assertEquals(7, inner.getFirst().v());
        assertEquals(9, outer.getFirst().u());
        assertEquals(27, outer.getFirst().v());
    }

    @Test
    void perFaceTopBottomUvUsesJavaCornerAndIgnoresBoxMirror() {
        var model = mesh("""
                [{"name":"uv","mirror":true,"cubes":[{"origin":[-6,0,2],"size":[4,6,2],"uv":{
                  "up":{"uv":[14,8],"uv_size":[-4,-2]},
                  "down":{"uv":[24,8],"uv_size":[-4,-2]},
                  "east":{"uv":[30,8],"uv_size":[2,6]}
                }}]}]
                """);
        var east = model.faces().get(0).vertices().getFirst();
        assertPoint(east.position(), 6, 6, 4);
        assertEquals(30, east.u());
        var up = model.faces().get(1).vertices();
        assertPoint(up.getFirst().position(), 2, 6, 2);
        assertEquals(10, up.getFirst().u());
        assertEquals(6, up.getFirst().v());
        assertEquals(14, up.get(2).u());
        assertEquals(8, up.get(2).v());
        var down = model.faces().get(2).vertices().getFirst();
        assertPoint(down.position(), 2, 0, 4);
        assertEquals(20, down.u());
        assertEquals(6, down.v());
    }

    @Test
    void bedrockCubeAndBoneXRotationsAndPivotsAreConverted() {
        for (String bones : List.of(
                """
                [{"name":"root","cubes":[{"origin":[-6,2,3],"size":[2,2,1],
                  "pivot":[-5,2,3],"rotation":[90,0,0],"uv":[0,0]}]}]
                """,
                """
                [{"name":"root","pivot":[-5,2,3],"rotation":[90,0,0],
                  "cubes":[{"origin":[-6,2,3],"size":[2,2,1],"uv":[0,0]}]}]
                """)) {
            // Java corner (6,4,3), pivot (5,2,3), rotation -90 X.
            assertPoint(mesh(bones).faces().getFirst().vertices().getFirst().position(), 6, 2, 1);
        }
    }

    @Test
    void modelSettingsRotationRemainsInJavaCoordinates() {
        var transform = new org.joml.Matrix4f();
        BedrockGeometry.rotateAround(transform, new BedrockGeometry.Point(1, 2, 3),
                new BedrockGeometry.Point(0, 90, 0));
        var result = transform.transformPosition(new Vector3f(2, 2, 3));
        assertTrue(result.equals(new Vector3f(1, 2, 2), 0.00001F));
    }

    @Test
    void shippedStoveGeometryAndUvTextureAreLoadable() throws Exception {
        try (var stream = getClass().getResourceAsStream(
                "/assets/teyvatdelight/bedrock/blocks/adepti_seekers_stove.geo.json")) {
            assertNotNull(stream);
            var file = JsonParser.parseReader(new java.io.InputStreamReader(stream, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            var model = BedrockGeometry.read(file, null);
            assertEquals(64, model.textureWidth());
            assertEquals(64, model.textureHeight());
            assertEquals(61 * 6, model.faces().size());
            // Regression for the actual exported right wall, not only a symmetric fixture.
            var outerWall = model.faces().get(40 * 6 + 3).vertices();
            for (var vertex : outerWall) assertEquals(6.1F, vertex.position().x(), 0.00001F);
            assertEquals(9, outerWall.getFirst().u());
            assertEquals(27, outerWall.getFirst().v());
            try (var texture = getClass().getResourceAsStream(
                    "/assets/teyvatdelight/textures/bedrock/blocks/adepti_seekers_stove.png")) {
                assertNotNull(texture);
                var image = javax.imageio.ImageIO.read(texture);
                assertEquals(model.textureWidth(), image.getWidth());
                assertEquals(model.textureHeight(), image.getHeight());
            }
        }
    }

    private static void assertPoint(BedrockGeometry.Point point, float x, float y, float z) {
        assertEquals(x, point.x(), 0.00001F);
        assertEquals(y, point.y(), 0.00001F);
        assertEquals(z, point.z(), 0.00001F);
    }
}
