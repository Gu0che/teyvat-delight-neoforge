package com.guoche.teyvatdelight.client;

import com.guoche.teyvatdelight.EmptyFeatherMothEntity;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EmptyFeatherMothModel extends HierarchicalModel<EmptyFeatherMothEntity> {
    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0D);
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "empty_feather_moth"), "main");

    private final ModelPart root;
    private final ModelPart moth;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public EmptyFeatherMothModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.moth = root.getChild("root");
        this.leftWing = this.moth.getChild("left_wing");
        this.rightWing = this.moth.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        // Keep the mirrored X coordinates and rotations from Blockbench's Java-entity export.
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition moth = root.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offsetAndRotation(-0.5F, 19.5F, -0.5F, 52.5F * DEG_TO_RAD, 0.0F, 0.0F));
        moth.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(11, 11).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);

        PartDefinition leftWing = moth.addOrReplaceChild("left_wing", CubeListBuilder.create(),
                PartPose.offset(0.5F, 0.0F, 0.0F));
        leftWing.addOrReplaceChild("upper", CubeListBuilder.create()
                .texOffs(0, 0).addBox(0.0F, -4.5F, -0.5F, 4.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 12.5F * DEG_TO_RAD));
        leftWing.addOrReplaceChild("lower", CubeListBuilder.create()
                .texOffs(10, 0).addBox(0.0F, -0.5F, -0.5F, 3.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, -10.0F * DEG_TO_RAD));

        PartDefinition rightWing = moth.addOrReplaceChild("right_wing", CubeListBuilder.create(),
                PartPose.offset(-0.5F, 0.0F, 0.0F));
        rightWing.addOrReplaceChild("upper", CubeListBuilder.create()
                .texOffs(0, 6).addBox(-4.0F, -4.5F, -0.5F, 4.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -12.5F * DEG_TO_RAD));
        rightWing.addOrReplaceChild("lower", CubeListBuilder.create()
                .texOffs(10, 5).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 10.0F * DEG_TO_RAD));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(EmptyFeatherMothEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        float phase = (ageInTicks * 0.05F) % 1.0F;
        float wingRotation;
        if (phase < 0.25F) {
            wingRotation = Mth.lerp(phase / 0.25F, -67.5F, 0.5F);
        } else if (phase < 0.5F) {
            wingRotation = Mth.lerp((phase - 0.25F) / 0.25F, 0.5F, 57.5F);
        } else if (phase < 0.75F) {
            wingRotation = Mth.lerp((phase - 0.5F) / 0.25F, 57.5F, -0.5F);
        } else {
            wingRotation = Mth.lerp((phase - 0.75F) / 0.25F, -0.5F, -62.5F);
        }
        this.leftWing.yRot = -wingRotation * DEG_TO_RAD;
        this.rightWing.yRot = wingRotation * DEG_TO_RAD;
        this.moth.y += phase < 0.5F ? Mth.lerp(phase * 2.0F, 0.0F, 0.15F)
                : Mth.lerp((phase - 0.5F) * 2.0F, 0.15F, 0.0F);
    }
}
