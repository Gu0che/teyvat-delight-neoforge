package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.KatheryneEntity;
import com.guoche.teyvatdelight.TeyvatDelight;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

public class KatheryneModel extends HierarchicalModel<KatheryneEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(TeyvatDelight.MODID, "katheryne"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart questionMark;
    private final ModelPart questIndicator;

    public KatheryneModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.head = root.getChild("Waist").getChild("hexin").getChild("Head");
        ModelPart effects = root.getChild("Waist").getChild("texiao");
        this.questionMark = effects.getChild("wenhao");
        this.questIndicator = effects.getChild("light");
        this.questionMark.visible = false;
        this.questIndicator.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition part_0 = root.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offset(0F, 12F, 0F));
            PartDefinition part_1 = part_0.addOrReplaceChild("hexin", CubeListBuilder.create(), PartPose.offset(0F, -12.2F, 0F));
                PartDefinition part_2 = part_1.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));
                    PartDefinition part_3 = part_2.addOrReplaceChild("Head2", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));
                        part_3.addOrReplaceChild("cube_7a8e0e98_4baf_4f1f_a267_32ba38692d52", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -38.7F, -3.5F, 7F, 7F, 7F), PartPose.offset(0F, 31.95F, 0F));
                        PartDefinition part_5 = part_3.addOrReplaceChild("Eyes", CubeListBuilder.create(), PartPose.offset(0F, -2F, 0F));
                            PartDefinition part_6 = part_5.addOrReplaceChild("Eyelid", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));
                                PartDefinition part_7 = part_6.addOrReplaceChild("RightEyelid", CubeListBuilder.create(), PartPose.offset(-2.5F, 1F, -3.025F));
                                    part_7.addOrReplaceChild("cube_7a773b61_94b2_e83a_593f_08a4f4ebafe9", CubeListBuilder.create().texOffs(82, 59).addBox(-3.4F, -34.7F, -3.51F, 2F, 2F, 1F), PartPose.offset(2.6F, 32.95F, 3.025F));
                                    part_7.addOrReplaceChild("cube_41c057ef_b5ab_fafc_4e8a_2b4484cb6e64", CubeListBuilder.create().texOffs(74, 48).addBox(-2.925F, -34.7F, -3.55F, 1.4F, 2F, 1F), PartPose.offset(6.8F, 32.95F, 3.025F));
                                PartDefinition part_10 = part_6.addOrReplaceChild("LeftEyelid", CubeListBuilder.create(), PartPose.offset(2.5F, 1F, -3.025F));
                                    part_10.addOrReplaceChild("cube_99fa07a3_e5e7_02d9_1170_66e7dae57efb", CubeListBuilder.create().texOffs(80, 82).addBox(1.4F, -34.7F, -3.51F, 2F, 2F, 1F), PartPose.offset(-2.6F, 32.95F, 3.025F));
                                    PartDefinition part_12 = part_10.addOrReplaceChild("LeftEyelidBase", CubeListBuilder.create(), PartPose.offset(-0.175F, 0F, 0F));
                                        PartDefinition part_13 = part_12.addOrReplaceChild("LeftEyePublic", CubeListBuilder.create(), PartPose.offset(-0.1F, -1F, -0.0375F));
                                        PartDefinition part_14 = part_12.addOrReplaceChild("LeftEyeDot", CubeListBuilder.create(), PartPose.offset(-3.55F, 17.35F, -0.525F));
                                part_6.addOrReplaceChild("cube_74a4a31a_7e50_3481_b878_9bc471e75fba", CubeListBuilder.create().texOffs(20, 31).addBox(1.275F, -35.55F, -3.825F, 2F, 1F, 1F), PartPose.offset(-0.1F, 33.95F, 0F));
                                part_6.addOrReplaceChild("cube_7170d6d1_c146_cfed_41bf_2a65f01eb793", CubeListBuilder.create().texOffs(14, 31).addBox(-3.275F, -35.55F, -3.825F, 2F, 1F, 1F), PartPose.offset(0.1F, 33.95F, 0F));
                                PartDefinition part_17 = part_6.addOrReplaceChild("RightEyelidBase", CubeListBuilder.create(), PartPose.offset(-2.325F, 1F, -3.025F));
                                    part_17.addOrReplaceChild("cube_d0af475d_e50b_faaa_5c2b_860a6e64af1a", CubeListBuilder.create().texOffs(74, 48).addBox(-2.925F, -34.7F, -3.55F, 1.4F, 2F, 1F), PartPose.offset(2.425F, 32.95F, 3.025F));
                                    PartDefinition part_19 = part_17.addOrReplaceChild("RightEyePublic", CubeListBuilder.create(), PartPose.offset(0.8F, 18F, -0.525F));
                                    PartDefinition part_20 = part_17.addOrReplaceChild("RightEyeDot", CubeListBuilder.create(), PartPose.offset(-3.625F, 17.35F, -0.525F));
                        PartDefinition part_21 = part_3.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0F, 17F, 0F));
                            PartDefinition part_22 = part_21.addOrReplaceChild("BaseHair", CubeListBuilder.create(), PartPose.offset(0F, -21.05F, 4.7F));
                                part_22.addOrReplaceChild("cube_820b2ea9_cf0b_66de_8fd2_e545127f52ed", CubeListBuilder.create().texOffs(0, 14).addBox(-4F, -39.95F, -3.8F, 8F, 1F, 8F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_f26c150f_aa14_c014_ac9f_8a260fdbe316", CubeListBuilder.create().texOffs(0, 23).addBox(-2.925F, -40.65F, -3.6F, 6F, 1F, 7F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_74cd8969_3709_50a0_0119_0e6e23ae47aa", CubeListBuilder.create().texOffs(84, 67).addBox(-0.89117F, 0.10843F, -0.80671F, 1.025F, 3F, 1F), PartPose.offsetAndRotation(4.25368F, 0.86417F, -7.16186F, 0.07672F, -0.01776F, 0.04332F));
                                part_22.addOrReplaceChild("cube_b7293a2d_97e4_c4d5_e93a_4af21c9346b8", CubeListBuilder.create().texOffs(18, 86).addBox(-0.9275F, -1.8235F, -0.83171F, 1.025F, 2F, 1F), PartPose.offsetAndRotation(4.25368F, 0.86417F, -7.16186F, 0.07837F, -0.0076F, -0.0872F));
                                part_22.addOrReplaceChild("cube_17776b9c_594e_2e77_169a_934ee0e32acc", CubeListBuilder.create().texOffs(48, 59).addBox(4.475F, -38.95F, 0.19F, 1F, 5F, 1F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_7311432f_274d_68a0_b486_6a3b866c6bff", CubeListBuilder.create().texOffs(56, 72).addBox(4.475F, -38.95F, 1.19F, 1F, 6F, 2F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_033af6ad_5858_1477_a78c_3f1b2a7cb19c", CubeListBuilder.create().texOffs(28, 77).addBox(-1F, -3F, -0.5F, 2F, 6F, 1F), PartPose.offsetAndRotation(5.68396F, 1.64979F, -2.40182F, 1.24011F, 1.39252F, 1.25033F));
                                part_22.addOrReplaceChild("cube_36ae9a03_5f12_727e_2fd7_b4ebc9f438f4", CubeListBuilder.create().texOffs(6, 77).addBox(-1.425F, -3F, -0.775F, 2F, 6F, 1F), PartPose.offsetAndRotation(5.24592F, 1.49989F, -1.09813F, 0.19691F, 0.87544F, 0.12712F));
                                part_22.addOrReplaceChild("cube_49fb5251_8c8d_7713_fc71_343fd21a40aa", CubeListBuilder.create().texOffs(60, 58).addBox(3.475F, -33F, -1.75F, 1F, 1F, 5F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_6b071149_697e_c31f_927b_e4de77281a52", CubeListBuilder.create().texOffs(14, 33).addBox(3.975F, -39.949F, -2.8F, 1F, 7F, 6F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_74f16488_c449_6897_21d7_73a8aba4a8a3", CubeListBuilder.create().texOffs(34, 77).addBox(-1F, -3F, -0.5F, 2F, 6F, 1F), PartPose.offsetAndRotation(-5.68396F, 1.64979F, -2.40182F, 1.19648F, -1.39252F, -1.25033F));
                                part_22.addOrReplaceChild("cube_55e1aee4_067d_6ad5_1a73_dee2927a24e2", CubeListBuilder.create().texOffs(12, 77).addBox(-0.575F, -3F, -0.775F, 2F, 6F, 1F), PartPose.offsetAndRotation(-5.24592F, 1.49989F, -1.09813F, 0.19691F, -0.87544F, -0.12712F));
                                part_22.addOrReplaceChild("cube_e2d1adb7_2a9a_b8c8_177d_5e82cbd994e6", CubeListBuilder.create().texOffs(62, 45).addBox(-4.475F, -33F, -1.75F, 1F, 1F, 5F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_1981a708_fd33_9dc9_765b_dca7527dd158", CubeListBuilder.create().texOffs(0, 85).addBox(-0.13383F, 0.10843F, -0.80671F, 1.025F, 3F, 1F), PartPose.offsetAndRotation(-4.25368F, 0.86417F, -7.16186F, 0.07672F, 0.01776F, -0.04332F));
                                part_22.addOrReplaceChild("cube_f7421cad_fc6c_50f0_c430_110c27605d0d", CubeListBuilder.create().texOffs(86, 20).addBox(-0.0975F, -1.8235F, -0.83171F, 1.025F, 2F, 1F), PartPose.offsetAndRotation(-4.25368F, 0.86417F, -7.16186F, 0.07837F, 0.0076F, 0.0872F));
                                part_22.addOrReplaceChild("cube_187eced0_13d6_3288_70fc_1d3ea47a77d2", CubeListBuilder.create().texOffs(6, 70).addBox(-5.475F, -38.95F, 0.19F, 1F, 5F, 1F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_4397652a_515c_7e15_24b1_2eb1d9c213a5", CubeListBuilder.create().texOffs(62, 72).addBox(-5.475F, -38.95F, 1.19F, 1F, 6F, 2F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_3e7ae189_fd09_dd77_0b38_66ea38482615", CubeListBuilder.create().texOffs(28, 33).addBox(-4.975F, -39.949F, -2.8F, 1F, 7F, 6F), PartPose.offset(0F, 36.25F, -4.7F));
                                part_22.addOrReplaceChild("cube_2374733c_fc3f_398c_e1b8_db9c7046dd08", CubeListBuilder.create().texOffs(16, 46).addBox(-3F, -3F, -0.3F, 6F, 6F, 1F), PartPose.offsetAndRotation(0F, 0.25F, 0F, 0.1309F, 0F, 0F));
                                part_22.addOrReplaceChild("cube_55f0bc73_5567_7b15_8513_376761cf00a0", CubeListBuilder.create().texOffs(0, 46).addBox(-3.5F, -3F, -0.5F, 7F, 6F, 1F), PartPose.offset(0F, 3.24321F, 0.23372F));
                                part_22.addOrReplaceChild("cube_631f8443_b63a_2724_5044_b5b4a92931a5", CubeListBuilder.create().texOffs(48, 70).addBox(-4.825F, -23.26527F, 3.79696F, 3F, 7F, 1F), PartPose.offsetAndRotation(0.325F, 22.08134F, -0.91711F, 0.17453F, 0F, 0F));
                                part_22.addOrReplaceChild("cube_f7b95448_3f65_a014_33a9_e66097689f13", CubeListBuilder.create().texOffs(70, 64).addBox(1.175F, -23.26527F, 3.79696F, 3F, 7F, 1F), PartPose.offsetAndRotation(0.325F, 22.08134F, -0.91711F, 0.17453F, 0F, 0F));
                                PartDefinition part_45 = part_22.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(0F, 21.3F, -4.7F));
                                    part_45.addOrReplaceChild("cube_076995a0_150c_40de_d21d_d6f1efeb0b38", CubeListBuilder.create().texOffs(54, 85).addBox(-1.10001F, -1.20001F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(-2.2341F, -22.0679F, -4.425F, 0F, 0F, 0.19199F));
                                    part_45.addOrReplaceChild("cube_82d565be_2cb0_075a_7349_60f586c98e94", CubeListBuilder.create().texOffs(58, 85).addBox(-0.1F, -1.8F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(-2.2341F, -22.0679F, -4.425F, 0F, 0F, 0.19199F));
                                    part_45.addOrReplaceChild("cube_903e81b2_228e_d169_7280_c022d2a0e48f", CubeListBuilder.create().texOffs(62, 85).addBox(-1F, -1.75F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(2.21668F, -22.09031F, -4.375F, 0F, 0F, -0.3098F));
                                    part_45.addOrReplaceChild("cube_1c234dfb_0045_8fc4_c600_14d2956b6a24", CubeListBuilder.create().texOffs(62, 6).addBox(0F, -1.75F, -0.5F, 1F, 4F, 1F), PartPose.offsetAndRotation(2.21668F, -22.09031F, -4.375F, 0F, 0F, -0.3098F));
                                    part_45.addOrReplaceChild("cube_d5d2e2e8_ad38_20a7_97fa_b32bd282a818", CubeListBuilder.create().texOffs(66, 85).addBox(-3.5F, -39.325F, -4.375F, 1F, 3F, 1F), PartPose.offset(0F, 14.95F, 0F));
                                    part_45.addOrReplaceChild("cube_9e3108d5_7e96_aff9_860a_0f4e0f955c2b", CubeListBuilder.create().texOffs(48, 65).addBox(-0.375F, -1.875F, -0.35F, 1F, 4F, 1F), PartPose.offsetAndRotation(3F, -22.375F, -4F, 0F, 0F, -0.1309F));
                                    part_45.addOrReplaceChild("cube_eebcaef9_7ba7_505b_7c15_0ed9e3d6c302", CubeListBuilder.create().texOffs(62, 51).addBox(-0.5F, -2F, -1.5F, 1F, 4F, 3F), PartPose.offsetAndRotation(3.8875F, -22.275F, -2.5F, 0F, 0F, -0.10472F));
                                    part_45.addOrReplaceChild("cube_b781fd0a_b15f_6277_9d0b_86e15957c114", CubeListBuilder.create().texOffs(70, 51).addBox(-4.3375F, -39.225F, -3.95F, 1F, 4F, 3F), PartPose.offset(0F, 14.95F, 0F));
                                    part_45.addOrReplaceChild("cube_398cfa45_a1e8_5623_9c69_2f9fa9d52849", CubeListBuilder.create().texOffs(70, 85).addBox(-2.495F, -39.425F, -4.55F, 1F, 3F, 1F), PartPose.offset(0F, 14.95F, 0F));
                                    part_45.addOrReplaceChild("cube_323e65d4_be20_03e5_2a62_2e431c910c90", CubeListBuilder.create().texOffs(80, 44).addBox(-1.5F, -39.425F, -4.55F, 3F, 2F, 1F), PartPose.offset(0F, 14.95F, 0F));
                                    part_45.addOrReplaceChild("cube_e85fa91b_3764_0c11_bf56_2c4f625060d3", CubeListBuilder.create().texOffs(80, 85).addBox(1.499F, -39.425F, -4.55F, 1F, 3F, 1F), PartPose.offset(0F, 14.95F, 0F));
                            part_21.addOrReplaceChild("cube_14216e5a_23f4_22b7_59a0_18faf5cadf5c", CubeListBuilder.create().texOffs(0, 81).addBox(-0.63809F, -0.50879F, -0.76025F, 1.025F, 2F, 2F), PartPose.offsetAndRotation(-4.57891F, -16.92682F, -2.09271F, -0.02903F, -0.01336F, -0.13081F));
                            part_21.addOrReplaceChild("cube_5f7d4721_80be_3041_a238_92b23a088dea", CubeListBuilder.create().texOffs(86, 11).addBox(-0.0975F, -1.8235F, -0.83171F, 1.025F, 2F, 1F), PartPose.offsetAndRotation(-4.34992F, -19.17766F, -3.06992F, -0.11764F, -0.00227F, 0.08754F));
                            part_21.addOrReplaceChild("cube_961e2c45_5295_c000_b507_3e0b8202dff3", CubeListBuilder.create().texOffs(74, 72).addBox(-0.29318F, -5.37782F, -0.76486F, 1.025F, 5F, 2F), PartPose.offsetAndRotation(4.6113F, -15.96135F, -2.48592F, -0.08282F, 0.00124F, -0.0439F));
                            part_21.addOrReplaceChild("cube_f26ec84e_8d99_6b20_66d3_91088b374c6e", CubeListBuilder.create().texOffs(86, 17).addBox(-0.9275F, -1.8235F, -0.83171F, 1.025F, 2F, 1F), PartPose.offsetAndRotation(4.34992F, -19.17766F, -3.06992F, -0.11764F, 0.00227F, -0.08754F));
                            part_21.addOrReplaceChild("cube_7d3936d0_7850_0942_ecf2_2defbb30ff02", CubeListBuilder.create().texOffs(86, 14).addBox(-0.42731F, -2.18438F, -0.5125F, 1.025F, 2F, 1F), PartPose.offsetAndRotation(3.70265F, -15.25573F, -2.93771F, -0.12431F, 0.00356F, 0.03567F));
                            part_21.addOrReplaceChild("cube_be4254cc_1db3_3b29_5131_9bc029552662", CubeListBuilder.create().texOffs(84, 52).addBox(-0.89118F, 0.10844F, -0.80671F, 1.025F, 3F, 1F), PartPose.offsetAndRotation(4.34992F, -19.17766F, -3.06992F, -0.11634F, 0.01758F, 0.04247F));
                            part_21.addOrReplaceChild("cube_e7dd5ec2_592f_6013_bcf4_691be6d18651", CubeListBuilder.create().texOffs(80, 14).addBox(-0.73182F, -4.37781F, -0.76487F, 1.025F, 4F, 2F), PartPose.offsetAndRotation(-4.57891F, -16.92682F, -2.09271F, -0.03091F, -0.00811F, 0.04365F));
                            part_21.addOrReplaceChild("cube_90bf7bbe_16be_064d_74c0_175874bea0a7", CubeListBuilder.create().texOffs(58, 45).addBox(-0.13382F, 0.10844F, -0.80671F, 1.025F, 3F, 1F), PartPose.offsetAndRotation(-4.34992F, -19.17766F, -3.06992F, -0.11634F, -0.01758F, -0.04247F));
                            part_21.addOrReplaceChild("cube_1afea166_b9df_8218_0537_30edab3dac4a", CubeListBuilder.create().texOffs(86, 8).addBox(-0.59769F, -2.18437F, -0.5125F, 1.025F, 2F, 1F), PartPose.offsetAndRotation(-3.66828F, -16.19871F, -2.50292F, -0.07202F, -0.00629F, -0.03592F));
                            part_21.addOrReplaceChild("cube_6766b6eb_804e_b01c_8e07_d1a54d9e0a98", CubeListBuilder.create().texOffs(86, 5).addBox(-0.5F, -1.025F, -0.55F, 1F, 2.05F, 1.05F), PartPose.offsetAndRotation(1.13177F, -21.98656F, -4.43683F, -0.12807F, 0.02714F, 0F));
                            part_21.addOrReplaceChild("cube_dda38d8a_aaab_a74a_e649_712cd192610f", CubeListBuilder.create().texOffs(82, 56).addBox(-1.5F, -1.025F, -0.55F, 2F, 2.05F, 1.05F), PartPose.offsetAndRotation(0.13177F, -21.98656F, -4.43683F, -0.12807F, 0.02714F, 0F));
                            part_21.addOrReplaceChild("cube_4c7b7e4e_4552_9505_e2ec_319029778a1b", CubeListBuilder.create().texOffs(42, 0).addBox(-4F, -39F, 3.2F, 8F, 7F, 1F), PartPose.offset(0F, 15.2F, 0F));
                            part_21.addOrReplaceChild("cube_e61b1670_c0dd_fc33_7bfe_adfd8330c4de", CubeListBuilder.create().texOffs(60, 0).addBox(3.075F, -40.65F, -2.6F, 1F, 1F, 5F), PartPose.offset(0F, 15.2F, 0F));
                            part_21.addOrReplaceChild("cube_ea2cc52f_c532_6ba8_4831_5207e4980136", CubeListBuilder.create().texOffs(60, 23).addBox(-3.925F, -40.65F, -2.6F, 1F, 1F, 5F), PartPose.offset(0F, 15.2F, 0F));
                            PartDefinition part_71 = part_21.addOrReplaceChild("toushi", CubeListBuilder.create(), PartPose.offset(-7.075F, -23.95F, 0.15F));
                                part_71.addOrReplaceChild("cube_452b022e_7c57_d52d_4181_b5c0efbbe58b", CubeListBuilder.create().texOffs(86, 26).addBox(0.5F, -0.5F, -1F, 1F, 2F, 1F), PartPose.offset(0F, 0F, 0F));
                                part_71.addOrReplaceChild("cube_d8d19c3f_4fae_14c1_d80f_6273ce473b13", CubeListBuilder.create().texOffs(12, 84).addBox(-0.5F, -0.5F, 0F, 2F, 2F, 1F), PartPose.offsetAndRotation(1.4F, -1F, -1F, 0F, 0F, -1.1781F));
                                part_71.addOrReplaceChild("cube_196f8f74_b131_cbe6_ac68_6be7fc1fca8d", CubeListBuilder.create().texOffs(6, 44).addBox(-1.5F, -0.5F, -0.5F, 2F, 1F, 1F), PartPose.offsetAndRotation(3.60711F, -2.70711F, -0.5F, 0F, 0F, -0.30543F));
                                part_71.addOrReplaceChild("cube_80272138_11af_ba6e_1692_cc38ed4b6c78", CubeListBuilder.create().texOffs(38, 21).addBox(-0.925F, -41.65F, -0.6F, 2F, 1F, 1F), PartPose.offset(4.825F, 38.15F, -0.4F));
                                part_71.addOrReplaceChild("cube_e18877bc_b496_e8fb_aa2b_e9516e470acf", CubeListBuilder.create().texOffs(32, 21).addBox(-0.925F, -41.65F, -0.6F, 2F, 1F, 1F), PartPose.offset(7.075F, 38.15F, -0.4F));
                                part_71.addOrReplaceChild("cube_73895d94_b911_6d34_9c73_fad142cdb382", CubeListBuilder.create().texOffs(86, 23).addBox(-1.5F, -0.5F, -1F, 1F, 2F, 1F), PartPose.offset(14.15F, 0F, 0F));
                                part_71.addOrReplaceChild("cube_bdf5bfc1_604f_8b42_d126_49f46614669d", CubeListBuilder.create().texOffs(80, 20).addBox(-1.5F, -0.5F, -1F, 1F, 4F, 2F), PartPose.offset(13.65F, 0.25F, -0.5F));
                                part_71.addOrReplaceChild("cube_41d6ddb2_85b7_e66f_b418_0a70809d4da2", CubeListBuilder.create().texOffs(6, 84).addBox(-1.5F, -0.5F, 0F, 2F, 2F, 1F), PartPose.offsetAndRotation(12.75F, -1F, -1F, 0F, 0F, 1.1781F));
                                part_71.addOrReplaceChild("cube_9aa55181_6fdd_8135_7ea6_6b454ac391b8", CubeListBuilder.create().texOffs(86, 0).addBox(-0.5F, -0.5F, -0.5F, 2F, 1F, 1F), PartPose.offsetAndRotation(10.54289F, -2.70711F, -0.5F, 0F, 0F, 0.30543F));
                                part_71.addOrReplaceChild("cube_dde9aa2e_804c_035b_711b_32fe4e7f6044", CubeListBuilder.create().texOffs(42, 11).addBox(-0.925F, -41.65F, -0.6F, 2F, 1F, 1F), PartPose.offset(9.325F, 38.15F, -0.4F));
                                part_71.addOrReplaceChild("cube_cb743cd7_1069_5582_ef5d_5dbf3a23cfaf", CubeListBuilder.create().texOffs(72, 61).addBox(-1.5F, -0.5F, -1F, 3F, 1F, 2F), PartPose.offsetAndRotation(11.75F, -1F, -0.5F, 0F, 0F, 0.7854F));
                                part_71.addOrReplaceChild("cube_b6ad24ba_8aad_304c_a58e_4faa4b317ac3", CubeListBuilder.create().texOffs(72, 58).addBox(-1.5F, -0.5F, -1F, 3F, 1F, 2F), PartPose.offsetAndRotation(2.4F, -1F, -0.5F, 0F, 0F, -0.7854F));
                                part_71.addOrReplaceChild("cube_55347998_dc8c_6ce7_dfd1_2ccd70cf3163", CubeListBuilder.create().texOffs(42, 8).addBox(-3.925F, -41.65F, -0.6F, 8F, 1F, 2F), PartPose.offset(7.075F, 39.15F, -0.9F));
                                part_71.addOrReplaceChild("cube_52027576_f599_2321_acb4_ef1fc4feddc1", CubeListBuilder.create().texOffs(80, 26).addBox(0.5F, -0.5F, -1F, 1F, 4F, 2F), PartPose.offset(0.5F, 0.25F, -0.5F));
                PartDefinition part_86 = part_1.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0F, -0.8F, 0F));
                    PartDefinition part_87 = part_86.addOrReplaceChild("BackClothe", CubeListBuilder.create(), PartPose.offset(0F, 7.5356F, -1.75F));
                        part_87.addOrReplaceChild("cube_588ba335_c8bc_347b_944d_42d891380385", CubeListBuilder.create().texOffs(26, 54).addBox(-1.39939F, -3.26923F, -0.43314F, 2.5F, 11F, 0.9F), PartPose.offsetAndRotation(0.14939F, 3.49872F, 4.20837F, 0.3098F, 0F, 0F));
                        part_87.addOrReplaceChild("cube_52e6a216_f4f9_e8ef_dadb_ec6a774aabd9", CubeListBuilder.create().texOffs(34, 54).addBox(-3.43951F, -3.16724F, -0.4169F, 2.5F, 11F, 0.9F), PartPose.offsetAndRotation(0.14939F, 3.49872F, 4.20837F, 0.30894F, -0.04736F, 0.0765F));
                        part_87.addOrReplaceChild("cube_5335db31_6088_1cc8_71d1_fc5ec517549d", CubeListBuilder.create().texOffs(28, 66).addBox(0F, -5.67F, -0.9F, 2F, 10F, 0.9F), PartPose.offsetAndRotation(-5.46555F, 5.92764F, 5.22033F, 0.28466F, -0.14237F, 0.25769F));
                        part_87.addOrReplaceChild("cube_8cd056a7_ae9a_be03_f954_d57932660854", CubeListBuilder.create().texOffs(66, 29).addBox(-2F, -5.67F, -0.9F, 2F, 10F, 0.9F), PartPose.offsetAndRotation(5.46555F, 5.92764F, 5.22033F, 0.28466F, 0.14237F, -0.25769F));
                        part_87.addOrReplaceChild("cube_26024e60_ab34_f532_6481_782c04cd6975", CubeListBuilder.create().texOffs(10, 53).addBox(0.74194F, -3.21671F, -0.4234F, 2.5F, 11F, 0.9F), PartPose.offsetAndRotation(0.14939F, 3.49872F, 4.20837F, 0.30894F, 0.04736F, -0.0765F));
                    PartDefinition part_93 = part_86.addOrReplaceChild("RightClothe", CubeListBuilder.create(), PartPose.offset(-3.5F, 7.5356F, 0.1F));
                        part_93.addOrReplaceChild("cube_fdf54189_3397_864c_d543_b0bca9902e69", CubeListBuilder.create().texOffs(10, 65).addBox(0F, -5.67F, -1.71F, 0.9F, 10F, 1.71F), PartPose.offsetAndRotation(-2.06044F, 6.32217F, -1.18529F, -0.17453F, -0.04363F, 0.30543F));
                        part_93.addOrReplaceChild("cube_a96d8450_5e4f_b3ec_1157_cde8bc9a0556", CubeListBuilder.create().texOffs(42, 59).addBox(-0.4275F, -2.34F, -0.945F, 0.8775F, 10F, 2.115F), PartPose.offsetAndRotation(-0.62992F, 3.28161F, -0.28529F, 0F, 0F, 0.30543F));
                        part_93.addOrReplaceChild("cube_4eaf15df_992f_225c_a354_8ea9c6c02500", CubeListBuilder.create().texOffs(16, 65).addBox(0F, -5.67F, 0F, 0.9F, 10F, 1.71F), PartPose.offsetAndRotation(-2.06044F, 6.32217F, 0.79471F, 0.1309F, 0.04363F, 0.30543F));
                    PartDefinition part_97 = part_86.addOrReplaceChild("LeftClothe", CubeListBuilder.create(), PartPose.offset(3.5F, 7.5356F, 0.1F));
                        part_97.addOrReplaceChild("cube_fda438b7_0391_33bf_2029_8eee14a82b8e", CubeListBuilder.create().texOffs(66, 6).addBox(-0.9F, -5.67F, -1.71F, 0.9F, 10F, 1.71F), PartPose.offsetAndRotation(2.06044F, 6.32217F, -1.18529F, -0.17453F, 0.04363F, -0.30543F));
                        part_97.addOrReplaceChild("cube_d24f623c_7a0d_ad3a_ea44_4daecafb6d89", CubeListBuilder.create().texOffs(24, 78).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(2.38314F, 9.01341F, -0.85517F, 0F, 0.04363F, -0.30543F));
                        part_97.addOrReplaceChild("cube_ee62b0d7_e2e3_2333_7c5b_6093e9dacd1c", CubeListBuilder.create().texOffs(70, 80).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(2.38314F, 9.01341F, 1.89483F, 0F, 0.04363F, -0.30543F));
                        part_97.addOrReplaceChild("cube_d575a62f_61e0_582d_9193_19ab2a3b84d4", CubeListBuilder.create().texOffs(24, 83).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(2.13314F, 8.51341F, 3.64483F, 0.30543F, 0.04363F, -0.30543F));
                        part_97.addOrReplaceChild("cube_ee3a59fd_902b_85ce_ac1c_3e21777f961a", CubeListBuilder.create().texOffs(28, 84).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(2.13314F, 9.01341F, -2.85517F, -0.34907F, 0.04363F, -0.30543F));
                        part_97.addOrReplaceChild("cube_ce2b443f_4695_d8a7_5418_f0f2d30ea102", CubeListBuilder.create().texOffs(84, 62).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(-9.13314F, 8.51341F, 3.64483F, 0.30543F, -0.04363F, 0.30543F));
                        part_97.addOrReplaceChild("cube_8aade4c5_bcc4_4c54_0cbc_e2cad421f15b", CubeListBuilder.create().texOffs(84, 47).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(-9.38314F, 9.01341F, 1.89483F, 0F, -0.04363F, 0.30543F));
                        part_97.addOrReplaceChild("cube_ba0afd95_6dbf_0daa_c03a_404f4457a4a0", CubeListBuilder.create().texOffs(36, 84).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(-9.38314F, 9.01341F, -0.85517F, 0F, -0.04363F, 0.30543F));
                        part_97.addOrReplaceChild("cube_2974c989_d321_acd4_3ff4_e947992db602", CubeListBuilder.create().texOffs(32, 84).addBox(-0.45F, -2.5F, -1F, 0.9F, 4F, 1F), PartPose.offsetAndRotation(-9.13314F, 9.01341F, -2.85517F, -0.34907F, -0.04363F, 0.30543F));
                        part_97.addOrReplaceChild("cube_c1b5894c_5f39_be86_94d1_b5b5576fd9ed", CubeListBuilder.create().texOffs(60, 11).addBox(-0.45F, -2.34F, -0.945F, 0.8775F, 10F, 2.115F), PartPose.offsetAndRotation(0.62992F, 3.28161F, -0.28529F, 0F, 0F, -0.30543F));
                        part_97.addOrReplaceChild("cube_cc3be7ec_3ba5_ea95_6c5a_74bad326eab7", CubeListBuilder.create().texOffs(22, 66).addBox(-0.9F, -5.67F, 0F, 0.9F, 10F, 1.71F), PartPose.offsetAndRotation(2.06044F, 6.32217F, 0.79471F, 0.1309F, -0.04363F, -0.30543F));
                    PartDefinition part_109 = part_86.addOrReplaceChild("FrontClothe", CubeListBuilder.create(), PartPose.offset(0F, 7.5356F, 1.75F));
                        part_109.addOrReplaceChild("cube_889bfd71_157c_a11d_0de8_a684d23b87c5", CubeListBuilder.create().texOffs(58, 33).addBox(-1.39939F, -3.26923F, -0.46686F, 2.5F, 11F, 0.9F), PartPose.offsetAndRotation(0.14939F, 3.49872F, -4.20837F, -0.3098F, 0F, 0F));
                        part_109.addOrReplaceChild("cube_93009097_5168_5507_669f_e5e74db630a4", CubeListBuilder.create().texOffs(52, 58).addBox(-3.43951F, -3.16724F, -0.4831F, 2.5F, 11F, 0.9F), PartPose.offsetAndRotation(0.14939F, 3.49872F, -4.20837F, -0.30894F, 0.04736F, 0.0765F));
                        part_109.addOrReplaceChild("cube_40ea2c5c_8a2d_b6c5_1dc1_4c252f7157c1", CubeListBuilder.create().texOffs(54, 80).addBox(-3.43951F, 3.83276F, -0.4831F, 2.5F, 4F, 0.9F), PartPose.offsetAndRotation(1.14939F, 3.49872F, -4.20837F, -0.30894F, 0.04736F, 0.0765F));
                        part_109.addOrReplaceChild("cube_e62338e8_cbb6_bafd_4b36_003414438b45", CubeListBuilder.create().texOffs(34, 66).addBox(0F, -5.67F, 0F, 2F, 10F, 0.9F), PartPose.offsetAndRotation(-5.46555F, 5.92764F, -5.22033F, -0.28466F, 0.14237F, 0.25769F));
                        part_109.addOrReplaceChild("cube_747a495e_76ea_393d_43bf_a223fa493f4b", CubeListBuilder.create().texOffs(80, 32).addBox(0F, -0.67F, 0F, 2F, 5F, 0.9F), PartPose.offsetAndRotation(-4.46555F, 5.92764F, -5.22033F, -0.28466F, 0.14237F, 0.25769F));
                        part_109.addOrReplaceChild("cube_944db8bd_25a3_ca66_95fd_7906528a1d02", CubeListBuilder.create().texOffs(0, 70).addBox(-2F, -5.67F, 0F, 2F, 10F, 0.9F), PartPose.offsetAndRotation(5.46555F, 5.92764F, -5.22033F, -0.28466F, -0.14237F, -0.25769F));
                        part_109.addOrReplaceChild("cube_02ac4b2a_2cbe_79ed_9e33_686f7306568b", CubeListBuilder.create().texOffs(66, 18).addBox(-2F, 0.33F, 0F, 2F, 4F, 0.9F), PartPose.offsetAndRotation(4.46555F, 5.92764F, -5.22033F, -0.28466F, -0.14237F, -0.25769F));
                        part_109.addOrReplaceChild("cube_b2fab1f0_eb59_501e_865c_5ea00397f6a5", CubeListBuilder.create().texOffs(80, 38).addBox(0F, -0.67F, -0.9F, 2F, 5F, 0.9F), PartPose.offsetAndRotation(-4.46555F, 5.92764F, 1.72033F, 0.28466F, -0.14237F, 0.25769F));
                        part_109.addOrReplaceChild("cube_e24a0743_4aa4_ef33_f646_87d7a5f68ad4", CubeListBuilder.create().texOffs(80, 72).addBox(-3.43951F, 3.83276F, -0.4169F, 2.5F, 4F, 0.9F), PartPose.offsetAndRotation(1.14939F, 3.49872F, 0.70837F, 0.30894F, -0.04736F, 0.0765F));
                        part_109.addOrReplaceChild("cube_de1f4092_e2e9_ae58_debd_af2826487f17", CubeListBuilder.create().texOffs(62, 80).addBox(0.74194F, 3.78329F, -0.4234F, 2.5F, 4F, 0.9F), PartPose.offsetAndRotation(-0.85061F, 3.49872F, 0.70837F, 0.30894F, 0.04736F, -0.0765F));
                        part_109.addOrReplaceChild("cube_a5673cf5_4fe4_b8a0_c7dd_3e67f0cb77d5", CubeListBuilder.create().texOffs(80, 77).addBox(-2F, 0.33F, -0.9F, 2F, 4F, 0.9F), PartPose.offsetAndRotation(4.46555F, 5.92764F, 1.72033F, 0.28466F, 0.14237F, -0.25769F));
                        part_109.addOrReplaceChild("cube_2dd01ef0_a587_7862_322e_f16050a14dbb", CubeListBuilder.create().texOffs(18, 53).addBox(0.74194F, -3.21671F, -0.4766F, 2.5F, 11F, 0.9F), PartPose.offsetAndRotation(0.14939F, 3.49872F, -4.20837F, -0.30894F, -0.04736F, -0.0765F));
                        part_109.addOrReplaceChild("cube_753037e4_a8b0_45a4_2389_e8508571e744", CubeListBuilder.create().texOffs(66, 40).addBox(0.74194F, 3.78329F, -0.4766F, 2.5F, 4F, 0.9F), PartPose.offsetAndRotation(-0.85061F, 3.49872F, -4.20837F, -0.30894F, -0.04736F, -0.0765F));
                    part_86.addOrReplaceChild("cube_8448f2f8_bb25_897b_bcc5_1f3007b3c2cb", CubeListBuilder.create().texOffs(52, 49).addBox(-0.5F, -2F, -2F, 1F, 5F, 4F), PartPose.offsetAndRotation(2.95F, 5.725F, 0.1F, 0F, 0F, -0.2618F));
                    part_86.addOrReplaceChild("cube_67d28b6f_cbe5_68dd_991e_12afefa75534", CubeListBuilder.create().texOffs(0, 53).addBox(-0.5F, -2F, -2F, 1F, 5F, 4F), PartPose.offsetAndRotation(-2.95F, 5.725F, 0.1F, 0F, 0F, 0.2618F));
                    PartDefinition part_125 = part_86.addOrReplaceChild("bow", CubeListBuilder.create(), PartPose.offset(3.98105F, 13.43775F, -4.19743F));
                        part_125.addOrReplaceChild("cube_c69e219d_52e9_ce7f_f935_a9f3ad7664b9", CubeListBuilder.create().texOffs(40, 86).addBox(-0.5625F, -1.17904F, -0.30724F, 1.125F, 1.125F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, 0F, 0F, -0.17453F));
                        part_125.addOrReplaceChild("cube_b7b209e6_cb6a_34c0_fb1e_ec8252a4bf43", CubeListBuilder.create().texOffs(86, 39).addBox(-0.00388F, -0.20592F, -0.25915F, 0.75F, 1.875F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, -0.36825F, -0.12573F, -0.40497F));
                        part_125.addOrReplaceChild("cube_50d1b533_ac06_61f1_ba5b_68c524ab8d6f", CubeListBuilder.create().texOffs(86, 42).addBox(-1.39972F, -1.32129F, -0.21036F, 1F, 1.125F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, 0F, -0.08727F, -0.35779F));
                        part_125.addOrReplaceChild("cube_d84a114a_3779_7f09_1123_cae19940d3a9", CubeListBuilder.create().texOffs(86, 35).addBox(-0.76792F, -0.84495F, -0.2619F, 0.75F, 2.625F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, -0.32208F, 0.19749F, -0.01442F));
                        part_125.addOrReplaceChild("cube_a8f4d080_2f65_567e_4221_824206a65285", CubeListBuilder.create().texOffs(44, 86).addBox(0.39972F, -1.3213F, -0.21036F, 1F, 1.125F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, 0F, 0.08727F, 0.00873F));
                        part_125.addOrReplaceChild("cube_a03c7e1f_721e_e5f2_3b2f_5723a431152d", CubeListBuilder.create().texOffs(74, 86).addBox(0.11485F, -1.17243F, -0.19161F, 1F, 0.9F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, 0.03696F, 0.07907F, 0.44652F));
                        part_125.addOrReplaceChild("cube_abf7519b_6c9e_6a74_25ca_30bfe578da9d", CubeListBuilder.create().texOffs(48, 86).addBox(-1.11485F, -1.17243F, -0.19161F, 1F, 0.9F, 0.75F), PartPose.offsetAndRotation(0.01895F, 0.1718F, -0.05481F, 0.03696F, -0.07907F, -0.79559F));
                    PartDefinition part_133 = part_86.addOrReplaceChild("RightClothe2", CubeListBuilder.create(), PartPose.offset(4.5F, 8.5356F, 0.1F));
                        part_133.addOrReplaceChild("cube_97fb64ee_536d_70df_341c_b3735a80a625", CubeListBuilder.create().texOffs(18, 78).addBox(-0.9F, -5.67F, -1.71F, 0.9F, 5.76797F, 1.71F), PartPose.offsetAndRotation(1.81044F, 5.32217F, -1.18529F, -0.17453F, 0.04363F, -0.30543F));
                        part_133.addOrReplaceChild("cube_cb22f350_a759_6f84_fdf4_9d126a760a75", CubeListBuilder.create().texOffs(68, 72).addBox(-0.45F, -2.34F, -0.945F, 0.8775F, 5.76797F, 2.115F), PartPose.offsetAndRotation(0.37992F, 2.28161F, -0.28529F, 0F, 0F, -0.30543F));
                        part_133.addOrReplaceChild("cube_c0d944a0_d384_36d9_30c0_394e2b44a2cf", CubeListBuilder.create().texOffs(48, 78).addBox(-0.9F, -5.67F, 0F, 0.9F, 5.76797F, 1.71F), PartPose.offsetAndRotation(1.81044F, 5.32217F, 0.79471F, 0.1309F, -0.04363F, -0.30543F));
                        PartDefinition part_137 = part_133.addOrReplaceChild("LeftClothe2", CubeListBuilder.create(), PartPose.offset(0.5F, 0F, 0F));
                            part_137.addOrReplaceChild("cube_eaf93222_e51e_85b4_e6e6_0043a2e28e8c", CubeListBuilder.create().texOffs(78, 48).addBox(0F, -5.67F, -1.71F, 0.9F, 5.76797F, 1.71F), PartPose.offsetAndRotation(-11.06044F, 5.32217F, -1.18529F, -0.17453F, -0.04363F, 0.30543F));
                            part_137.addOrReplaceChild("cube_634b8b7d_9b46_458e_aee5_86632aee11a1", CubeListBuilder.create().texOffs(74, 40).addBox(-0.4275F, -2.34F, -0.945F, 0.8775F, 5.76797F, 2.115F), PartPose.offsetAndRotation(-9.62992F, 2.28161F, -0.28529F, 0F, 0F, 0.30543F));
                            part_137.addOrReplaceChild("cube_5a8b95a3_9746_ccb9_22e9_04704c7aa120", CubeListBuilder.create().texOffs(78, 64).addBox(0F, -5.67F, 0F, 0.9F, 5.76797F, 1.71F), PartPose.offsetAndRotation(-11.06044F, 5.32217F, 0.79471F, 0.1309F, 0.04363F, 0.30543F));
                    PartDefinition part_141 = part_86.addOrReplaceChild("FrontClothe2", CubeListBuilder.create(), PartPose.offset(0F, 7.7856F, 1.25F));
                        part_141.addOrReplaceChild("cube_69374c5a_b3f7_b327_c34b_41cb172e3892", CubeListBuilder.create().texOffs(72, 8).addBox(-1.39939F, -3.26923F, -0.46686F, 2.5F, 6.66F, 0.9F), PartPose.offsetAndRotation(0.14939F, 2.49872F, -4.20837F, -0.3098F, 0F, 0F));
                        part_141.addOrReplaceChild("cube_f40e7994_9efe_70d1_993b_c62df3dec426", CubeListBuilder.create().texOffs(72, 16).addBox(-3.43951F, -3.16724F, -0.4831F, 2.5F, 6.66F, 0.9F), PartPose.offsetAndRotation(0.14939F, 2.49872F, -4.20837F, -0.30894F, 0.04736F, 0.0765F));
                        part_141.addOrReplaceChild("cube_cced5f08_fef9_68f8_07b8_c4fee77c8004", CubeListBuilder.create().texOffs(40, 79).addBox(0F, -5.67F, 0F, 2F, 5.67F, 0.9F), PartPose.offsetAndRotation(-5.46555F, 4.92764F, -5.22033F, -0.28466F, 0.14237F, 0.25769F));
                        part_141.addOrReplaceChild("cube_0e637db3_b523_70f9_bb51_c42641ae88b0", CubeListBuilder.create().texOffs(74, 79).addBox(-2F, -5.67F, 0F, 2F, 5.67F, 0.9F), PartPose.offsetAndRotation(5.46555F, 4.92764F, -5.22033F, -0.28466F, -0.14237F, -0.25769F));
                        part_141.addOrReplaceChild("cube_45c60488_33b3_d720_1226_b5490914e74f", CubeListBuilder.create().texOffs(40, 71).addBox(0.74194F, -3.21671F, -0.4766F, 2.5F, 6.66F, 0.9F), PartPose.offsetAndRotation(0.14939F, 2.49872F, -4.20837F, -0.30894F, -0.04736F, -0.0765F));
                        PartDefinition part_147 = part_141.addOrReplaceChild("BackClothe2", CubeListBuilder.create(), PartPose.offset(0F, 1.5F, -2.25F));
                            part_147.addOrReplaceChild("cube_7b443995_0f35_19c1_915b_d1e013dd5a9c", CubeListBuilder.create().texOffs(72, 24).addBox(-1.39939F, -3.26923F, -0.43314F, 2.5F, 6.66F, 0.9F), PartPose.offsetAndRotation(0.14939F, 2.49872F, 4.20837F, 0.3098F, 0F, 0F));
                            part_147.addOrReplaceChild("cube_59d525ce_c045_5aa8_8597_33f68e503277", CubeListBuilder.create().texOffs(72, 32).addBox(-3.43951F, -3.16724F, -0.4169F, 2.5F, 6.66F, 0.9F), PartPose.offsetAndRotation(0.14939F, 2.49872F, 4.20837F, 0.30894F, -0.04736F, 0.0765F));
                            part_147.addOrReplaceChild("cube_99d12e4b_2368_6885_4b70_616d2a4f21b3", CubeListBuilder.create().texOffs(80, 0).addBox(0F, -5.67F, -0.9F, 2F, 5.67F, 0.9F), PartPose.offsetAndRotation(-5.46555F, 4.92764F, 5.22033F, 0.28466F, -0.14237F, 0.25769F));
                            part_147.addOrReplaceChild("cube_aa1bbf22_06d0_1185_d0c5_ee78b4f933fc", CubeListBuilder.create().texOffs(80, 7).addBox(-2F, -5.67F, -0.9F, 2F, 5.67F, 0.9F), PartPose.offsetAndRotation(5.46555F, 4.92764F, 5.22033F, 0.28466F, 0.14237F, -0.25769F));
                            part_147.addOrReplaceChild("cube_20efd642_4980_9618_0642_142a13700efb", CubeListBuilder.create().texOffs(72, 0).addBox(0.74194F, -3.21671F, -0.4234F, 2.5F, 6.66F, 0.9F), PartPose.offsetAndRotation(0.14939F, 2.49872F, 4.20837F, 0.30894F, 0.04736F, -0.0765F));
                    part_86.addOrReplaceChild("cube_b884aa91_1e74_df8e_bef0_b5de860a2e4e", CubeListBuilder.create().texOffs(30, 46).addBox(-1.4F, -3.3F, -2.075F, 2F, 4F, 4.2F), PartPose.offsetAndRotation(3.42613F, 10.41711F, -0.025F, 0F, 0F, -0.14835F));
                    part_86.addOrReplaceChild("cube_8566998d_7600_b5cf_5df2_01769fbefee4", CubeListBuilder.create().texOffs(32, 13).addBox(-2.5F, -24.8F, -2.125F, 5F, 4.1F, 4.25F), PartPose.offset(0F, 32F, 0F));
                    part_86.addOrReplaceChild("cube_57860e16_1b0d_498c_8284_afe73505b709", CubeListBuilder.create().texOffs(48, 21).addBox(-0.6F, -3.3F, -2.075F, 2F, 4F, 4.2F), PartPose.offsetAndRotation(-3.42613F, 10.41711F, -0.025F, 0F, 0F, 0.14835F));
                    part_86.addOrReplaceChild("cube_8437e4b3_db01_e404_55c8_7dc865e2cbd9", CubeListBuilder.create().texOffs(26, 23).addBox(-3.55F, -30.775F, -2.15F, 7.1F, 6F, 4.275F), PartPose.offset(0F, 32F, 0F));
                    part_86.addOrReplaceChild("cube_5bacd1c3_3628_198f_8252_41c08c3c2f57", CubeListBuilder.create().texOffs(48, 29).addBox(-3.55F, -29.775F, 1.125F, 7.1F, 3F, 1F), PartPose.offset(0F, 32F, -4.25F));
            PartDefinition part_158 = part_0.addOrReplaceChild("Arm", CubeListBuilder.create(), PartPose.offset(-5F, -10F, 0F));
                PartDefinition part_159 = part_158.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 3.75F, -1.5F, -0.93645F, -0.91741F, 0.62987F));
                    part_159.addOrReplaceChild("cube_4cac9914_3c46_60fd_2ab1_9b43efdfdfa0", CubeListBuilder.create().texOffs(0, 62).addBox(-1F, -4.5811F, -0.68432F, 2F, 5F, 3F, new CubeDeformation(0.3F)), PartPose.offset(0F, -1.1689F, -0.81568F));
                    part_159.addOrReplaceChild("cube_418f7562_d3a6_488c_474c_d8125b081a63", CubeListBuilder.create().texOffs(42, 49).addBox(-1F, -1.49264F, -0.51672F, 2F, 7F, 3F), PartPose.offsetAndRotation(0F, -1.1689F, -0.81568F, -0.82903F, 0F, 0F));
                PartDefinition part_162 = part_158.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offsetAndRotation(9.25F, 2.5811F, -1.81568F, -1.21368F, 0.80848F, -0.94781F));
                    part_162.addOrReplaceChild("cube_a6c57cc1_c368_51e1_7849_005913da3964", CubeListBuilder.create().texOffs(60, 64).addBox(-1F, -4.5811F, -0.68432F, 2F, 5F, 3F, new CubeDeformation(0.3F)), PartPose.offset(0F, 0F, 0F));
                    part_162.addOrReplaceChild("cube_8d918c68_0cbf_93fa_3afb_726240f95ad4", CubeListBuilder.create().texOffs(50, 11).addBox(-1F, -1.49264F, -0.51672F, 2F, 7F, 3F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.82903F, 0F, 0F));
            PartDefinition part_165 = part_0.addOrReplaceChild("Right Leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.9F, 0F, 0F, 0F, 0F, 0.03491F));
                part_165.addOrReplaceChild("cube_f1d8c5af_6eaa_89cb_a9cc_417342bbd032", CubeListBuilder.create().texOffs(28, 0).addBox(-2.9F, -12F, -2F, 3F, 9F, 4F), PartPose.offset(1.9F, 12F, 0F));
                part_165.addOrReplaceChild("cube_10778669_7f6a_cf8c_7608_fc0aa0b57814", CubeListBuilder.create().texOffs(42, 33).addBox(-2.9F, -12F, -3F, 3F, 3F, 5F), PartPose.offset(1.9F, 21F, 0F));
            PartDefinition part_168 = part_0.addOrReplaceChild("Left Leg", CubeListBuilder.create(), PartPose.offset(1.9F, 0F, 0F));
                part_168.addOrReplaceChild("cube_f4188ee6_c0e0_8e00_a28a_20d49837c39f", CubeListBuilder.create().texOffs(0, 31).addBox(-0.1F, -12F, -2F, 3F, 9F, 4F), PartPose.offset(-1.9F, 12F, 0F));
                part_168.addOrReplaceChild("cube_94ac6656_a6dc_52de_83db_8415eac63420", CubeListBuilder.create().texOffs(42, 41).addBox(-0.1F, -12F, -3F, 3F, 3F, 5F), PartPose.offset(-1.9F, 21F, 0F));
            PartDefinition part_171 = part_0.addOrReplaceChild("texiao", CubeListBuilder.create(), PartPose.offset(1.15F, -13.7F, -0.5F));
                PartDefinition part_172 = part_171.addOrReplaceChild("wenhao", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));
                    part_172.addOrReplaceChild("cube_dede272d_c7c9_0b4b_edea_a2f8ee79f397", CubeListBuilder.create().texOffs(44, 21).addBox(0F, -1F, -1F, 1F, 1F, 1F), PartPose.offset(-0.3F, 3.7F, 0.5F));
                    part_172.addOrReplaceChild("cube_60780f30_5075_98af_31fe_5ce25cd9d212", CubeListBuilder.create().texOffs(86, 29).addBox(0F, -1F, -1F, 1F, 2F, 1F), PartPose.offset(-0.3F, 1.2F, 0.5F));
                    part_172.addOrReplaceChild("cube_877f8610_e147_7c98_f81e_298ea5996b0d", CubeListBuilder.create().texOffs(86, 32).addBox(0F, -1F, -1F, 1F, 2F, 1F), PartPose.offset(0.7F, -0.8F, 0.5F));
                    part_172.addOrReplaceChild("cube_20b8040b_e50c_8ea0_2d87_59badd322da4", CubeListBuilder.create().texOffs(0, 44).addBox(0F, -1F, -1F, 2F, 1F, 1F), PartPose.offset(-1.05F, -1.8F, 0.5F));
                    part_172.addOrReplaceChild("cube_60133555_0c85_ec76_77d6_09944e7e0171", CubeListBuilder.create().texOffs(56, 70).addBox(0F, -1F, -1F, 1F, 1F, 1F), PartPose.offset(-2.05F, -0.8F, 0.5F));
                PartDefinition part_178 = part_171.addOrReplaceChild("light", CubeListBuilder.create(), PartPose.offset(-1.15F, 0.25F, 0.5F));
                    part_178.addOrReplaceChild("cube_99153412_443b_56f8_a095_c1c924df7046", CubeListBuilder.create().texOffs(78, 56).addBox(-1F, -1F, -1F, 1F, 1F, 1F), PartPose.offset(0.5F, -0.25F, 0.5F));
                    part_178.addOrReplaceChild("cube_26d145c1_98bd_997c_0ef9_8da59c6c4acc", CubeListBuilder.create().texOffs(84, 85).addBox(-1F, -3F, -1F, 1F, 3F, 1F), PartPose.offset(0.5F, -1.75F, 0.5F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(KatheryneEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.whyAnimation, KatheryneAnimations.WHY, ageInTicks);
        this.animate(entity.thanksAnimation, KatheryneAnimations.THANKS, ageInTicks);
        this.animate(entity.happyAnimation, KatheryneAnimations.HAPPY, ageInTicks);
        this.animate(entity.questionAnimation, KatheryneAnimations.QUESTION, ageInTicks);
        updateEffectVisibility(entity.whyAnimation, entity.questionAnimation);
        if (this.head != null) {
            this.head.yRot += netHeadYaw * ((float) Math.PI / 180.0F);
            this.head.xRot += headPitch * ((float) Math.PI / 180.0F);
        }
    }

    void updateEffectVisibility(AnimationState whyAnimation, AnimationState questionAnimation) {
        // One-shot animation states stay started after their last keyframe.
        this.questionMark.visible = whyAnimation.isStarted()
                && whyAnimation.getAccumulatedTime() < KatheryneAnimations.WHY.lengthInSeconds() * 1000.0F;
        this.questIndicator.visible = questionAnimation.isStarted();
    }
}
