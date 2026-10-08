package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.block.KatheryneFigurineBlock.Pose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

/** Reuses the approved mesh; bounds include all original cube rotations and inflation. */
final class KatheryneFigurineModel {
    private final Map<Pose, ModelPart> roots = new EnumMap<>(Pose.class);
    private final Bounds bounds = new Bounds();
    private final float scale;
    private final float centerX;
    private final float centerZ;

    KatheryneFigurineModel(Function<Pose, ModelPart> bakeRoot) {
        Bounds normal = new Bounds();
        for (Pose mode : Pose.values()) {
            ModelPart root = bakeRoot.apply(mode);
            roots.put(mode, root);
            ModelPart effects = root.getChild("Waist").getChild("texiao");
            effects.visible = false;
            Bounds original = new Bounds();
            root.render(new PoseStack(), original, 0, 0);
            // Scale the whole head group; all original cubes and UVs stay untouched.
            ModelPart head = root.getChild("Waist").getChild("hexin").getChild("Head");
            head.xScale = head.yScale = head.zScale = 1.8F;
            Bounds body = new Bounds();
            root.render(new PoseStack(), body, 0, 0);
            if (mode == Pose.NORMAL) {
                root.render(new PoseStack(), normal, 0, 0);
            }
            KatheryneModel model = new KatheryneModel(root);
            if (mode == Pose.QUESTION) {
                // Hold the last tilted pose, before WHY returns the head to neutral.
                KeyframeAnimations.animate(model, KatheryneAnimations.WHY, 1000, 1, new Vector3f());
            } else if (mode == Pose.COMMISSION) {
                KeyframeAnimations.animate(model, KatheryneAnimations.QUESTION, 0, 1, new Vector3f());
            }
            effects.visible = mode != Pose.NORMAL;
            effects.getChild("wenhao").visible = mode == Pose.QUESTION;
            effects.getChild("light").visible = mode == Pose.COMMISSION;
            // Move the original indicators up with the taller head instead of hiding them inside it.
            effects.y += (body.minY - original.minY) * 16;
            if (mode == Pose.QUESTION) {
                PoseStack measurement = new PoseStack();
                ModelPart waist = root.getChild("Waist");
                waist.translateAndRotate(measurement);
                Bounds indicator = new Bounds();
                effects.render(measurement, indicator, 0, 0);
                waist.getChild("hexin").translateAndRotate(measurement);
                Bounds tiltedHead = new Bounds();
                head.render(measurement, tiltedHead, 0, 0);
                effects.x += Math.max(0, tiltedHead.maxX + 1.0F / 16 - indicator.minX) * 16;
            }
            root.render(new PoseStack(), bounds, 0, 0);
        }
        // One shared transform prevents the body from jumping or changing size when switching poses.
        scale = 1.0F / (bounds.maxY - bounds.minY);
        centerX = (normal.minX + normal.maxX) / 2;
        centerZ = (normal.minZ + normal.maxZ) / 2;
    }

    void render(Direction facing, Pose mode, PoseStack pose, VertexConsumer vertices, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        // Match the living-entity coordinate system without creating a living entity.
        pose.scale(-scale, -scale, scale);
        pose.translate(-centerX, -bounds.maxY, -centerZ);
        roots.get(mode).render(pose, vertices, light, overlay);
        pose.popPose();
    }

    static final class Bounds implements VertexConsumer {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;
        int vertexCount;

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
            vertexCount++;
            return this;
        }

        @Override public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
        @Override public VertexConsumer setUv(float u, float v) { return this; }
        @Override public VertexConsumer setUv1(int u, int v) { return this; }
        @Override public VertexConsumer setUv2(int u, int v) { return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
    }
}
