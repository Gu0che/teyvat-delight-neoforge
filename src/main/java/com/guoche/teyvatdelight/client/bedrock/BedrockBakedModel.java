package com.guoche.teyvatdelight.client.bedrock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Immutable geometry shared by blocks, their items, and optional custom renderer adapters. */
final class BedrockBakedModel implements BakedModel {
    private final List<BakedQuad> quads;
    private final TextureAtlasSprite sprite;
    private final ItemTransforms transforms;
    private final boolean ambientOcclusion;
    private final RenderType renderType;

    BedrockBakedModel(BedrockGeometry.Mesh mesh, BedrockModelSettings settings, TextureAtlasSprite sprite,
                      ItemTransforms transforms, Direction facing) {
        this.sprite = sprite;
        this.transforms = transforms;
        this.ambientOcclusion = settings.ambientOcclusion();
        this.renderType = switch (settings.renderType()) {
            case "solid" -> RenderType.solid();
            case "translucent" -> RenderType.translucent();
            default -> RenderType.cutout();
        };
        Matrix4f matrix = new Matrix4f();
        if (settings.autoRotate() && facing != null) {
            matrix.translate(0.5F, 0.5F, 0.5F);
            switch (facing) {
                case EAST -> matrix.rotateY((float) -Math.PI / 2);
                case SOUTH -> matrix.rotateY((float) Math.PI);
                case WEST -> matrix.rotateY((float) Math.PI / 2);
                case UP -> matrix.rotateX((float) Math.PI / 2);
                case DOWN -> matrix.rotateX((float) -Math.PI / 2);
                default -> { }
            }
            matrix.translate(-0.5F, -0.5F, -0.5F);
        }
        matrix.translate(0.5F, 0, 0.5F).scale(1 / 16.0F)
                .translate(settings.translation().x(), settings.translation().y(), settings.translation().z());
        BedrockGeometry.rotateAround(matrix, new BedrockGeometry.Point(0, 0, 0), settings.rotation());
        matrix.scale(settings.scale().x(), settings.scale().y(), settings.scale().z());
        List<BakedQuad> baked = new ArrayList<>(mesh.faces().size());
        for (BedrockGeometry.Face face : mesh.faces()) baked.add(bake(face, mesh, matrix, sprite));
        this.quads = List.copyOf(baked);
    }

    private static BakedQuad bake(BedrockGeometry.Face face, BedrockGeometry.Mesh mesh,
                                   Matrix4f matrix, TextureAtlasSprite sprite) {
        Vector3f[] points = new Vector3f[4];
        for (int i = 0; i < 4; i++) {
            points[i] = matrix.transformPosition(face.vertices().get(i).position().vector());
            if (!points[i].isFinite()) throw new IllegalArgumentException("Non-finite model transform");
        }
        Vector3f normal = new Vector3f(points[1]).sub(points[0])
                .cross(new Vector3f(points[2]).sub(points[0])).normalize();
        if (!normal.isFinite()) throw new IllegalArgumentException("Model transform collapses a face");
        int packedNormal = (Math.round(normal.x * 127) & 255)
                | ((Math.round(normal.y * 127) & 255) << 8) | ((Math.round(normal.z * 127) & 255) << 16);
        int[] vertices = new int[32];
        float centerU = 0, centerV = 0;
        for (BedrockGeometry.Vertex vertex : face.vertices()) {
            centerU += vertex.u() / 4;
            centerV += vertex.v() / 4;
        }
        float shrink = sprite.uvShrinkRatio();
        for (int i = 0; i < 4; i++) {
            BedrockGeometry.Vertex vertex = face.vertices().get(i);
            int offset = i * 8;
            vertices[offset] = Float.floatToRawIntBits(points[i].x);
            vertices[offset + 1] = Float.floatToRawIntBits(points[i].y);
            vertices[offset + 2] = Float.floatToRawIntBits(points[i].z);
            vertices[offset + 3] = -1;
            vertices[offset + 4] = Float.floatToRawIntBits(sprite.getU(
                    (vertex.u() + (centerU - vertex.u()) * shrink) / mesh.textureWidth()));
            vertices[offset + 5] = Float.floatToRawIntBits(sprite.getV(
                    (vertex.v() + (centerV - vertex.v()) * shrink) / mesh.textureHeight()));
            vertices[offset + 6] = 0;
            vertices[offset + 7] = packedNormal;
        }
        return new BakedQuad(vertices, -1, Direction.getNearest(normal.x, normal.y, normal.z), sprite, true);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        // Rotated cuboids are not safe to cull against a neighboring full block face.
        return side == null ? quads : List.of();
    }

    @Override public boolean useAmbientOcclusion() { return ambientOcclusion; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean usesBlockLight() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public ItemTransforms getTransforms() { return transforms; }
    @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        return ChunkRenderTypeSet.of(renderType);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
        return List.of(RenderTypeHelper.getEntityRenderType(renderType, fabulous));
    }

    void render(PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        VertexConsumer vertices = buffers.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false));
        for (BakedQuad quad : quads) vertices.putBulkData(pose.last(), quad, 1, 1, 1, 1, light, overlay);
    }
}
