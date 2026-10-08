package com.guoche.teyvatdelight.registry;

import com.guoche.teyvatdelight.BlazingJinxinFlowerBlock;
import com.guoche.teyvatdelight.block.KatheryneFigurineBlock;
import com.guoche.teyvatdelight.BurntOutJinxinFlowerBlock;
import com.guoche.teyvatdelight.CallaLilyCropBlock;
import com.guoche.teyvatdelight.CeciliaCropBlock;
import com.guoche.teyvatdelight.ChenyuTeaBrewBlock;
import com.guoche.teyvatdelight.DandelionCropBlock;
import com.guoche.teyvatdelight.DendrobiumCropBlock;
import com.guoche.teyvatdelight.FluorescentFungusCropBlock;
import com.guoche.teyvatdelight.FrostlampFlowerCropBlock;
import com.guoche.teyvatdelight.GlazeLilyCropBlock;
import com.guoche.teyvatdelight.GrainfruitCropBlock;
import com.guoche.teyvatdelight.GrowingCoralShellBlock;
import com.guoche.teyvatdelight.HorsetailBottomBlock;
import com.guoche.teyvatdelight.HorsetailTopBlock;
import com.guoche.teyvatdelight.JueyunChiliCropBlock;
import com.guoche.teyvatdelight.MintCropBlock;
import com.guoche.teyvatdelight.MufengMushroomCropBlock;
import com.guoche.teyvatdelight.NaturalCoralPearlBlock;
import com.guoche.teyvatdelight.NaturalJinghuagusuiBlock;
import com.guoche.teyvatdelight.NaturalNoctilucousJadeBlock;
import com.guoche.teyvatdelight.NaturalShipoBlock;
import com.guoche.teyvatdelight.PearlBearingCoralShellBlock;
import com.guoche.teyvatdelight.RaspberryCropBlock;
import com.guoche.teyvatdelight.SeaGanodermaCropBlock;
import com.guoche.teyvatdelight.ShipoCropBlock;
import com.guoche.teyvatdelight.SmallLampGrassCropBlock;
import com.guoche.teyvatdelight.SnapdragonCropBlock;
import com.guoche.teyvatdelight.SumeruRoseCropBlock;
import com.guoche.teyvatdelight.SweetFlowerCropBlock;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.TeyvatFieldBlock;
import com.guoche.teyvatdelight.TeyvatWaterFieldBlock;
import com.guoche.teyvatdelight.ThirstingJinxinFlowerBlock;
import com.guoche.teyvatdelight.ValberryCropBlock;
import com.guoche.teyvatdelight.WildCallaLilyBlock;
import com.guoche.teyvatdelight.WildCeciliaBlock;
import com.guoche.teyvatdelight.WildDandelionBlock;
import com.guoche.teyvatdelight.WildDendrobiumBlock;
import com.guoche.teyvatdelight.WildFluorescentFungusBlock;
import com.guoche.teyvatdelight.WildFrostlampFlowerBlock;
import com.guoche.teyvatdelight.WildGlazeLilyBlock;
import com.guoche.teyvatdelight.WildGrainfruitBlock;
import com.guoche.teyvatdelight.WildHorsetailBlock;
import com.guoche.teyvatdelight.WildJinxinFlowerBlock;
import com.guoche.teyvatdelight.WildJueyunChiliBlock;
import com.guoche.teyvatdelight.WildMintBlock;
import com.guoche.teyvatdelight.WildMufengMushroomBlock;
import com.guoche.teyvatdelight.WildRaspberryBlock;
import com.guoche.teyvatdelight.WildSeaGanodermaBlock;
import com.guoche.teyvatdelight.WildSmallLampGrassBlock;
import com.guoche.teyvatdelight.WildSnapdragonBlock;
import com.guoche.teyvatdelight.WildSumeruRoseBlock;
import com.guoche.teyvatdelight.WildSweetFlowerBlock;
import com.guoche.teyvatdelight.WildValberryBlock;
import com.guoche.teyvatdelight.WildWindwheelAsterBlock;
import com.guoche.teyvatdelight.WildWolfhookBlock;
import com.guoche.teyvatdelight.WildYunyanCrackleafBlock;
import com.guoche.teyvatdelight.WindblumeFlowerBlock;
import com.guoche.teyvatdelight.WindwheelAsterCropBlock;
import com.guoche.teyvatdelight.WolfhookCropBlock;
import com.guoche.teyvatdelight.YunyanCrackleafCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Owns block registrations; holders are resolved by deferred suppliers. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TeyvatDelight.MODID);

    public static final DeferredBlock<Block> XUAN_CI_JADE_FIELD = BLOCKS.register(
            "xuan_ci_jade_field",
            () -> new TeyvatFieldBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND))
    );

    public static final DeferredBlock<Block> NI_CI_ZHI_FIELD = BLOCKS.register(
            "ni_ci_zhi_field",
            () -> new TeyvatFieldBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND))
    );

    public static final DeferredBlock<Block> CHU_CI_ZHU_FIELD = BLOCKS.register(
            "chu_ci_zhu_field",
            () -> new TeyvatWaterFieldBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_COBBLESTONE).noOcclusion())
    );

    public static final DeferredBlock<Block> XUAN_CI_PU_FIELD = BLOCKS.register(
            "xuan_ci_pu_field",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(state -> 7))
    );

    public static final DeferredBlock<Block> PRIMOGEM_BLOCK = BLOCKS.register(
            "primogem_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).lightLevel(state -> 15))
    );

    public static final DeferredBlock<Block> MORA_BLOCK = BLOCKS.register(
            "mora_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK))
    );

    public static final DeferredBlock<ChenyuTeaBrewBlock> CHENYU_TEA_BREW_BLOCK = BLOCKS.register(
            "chenyu_tea_brew",
            () -> new ChenyuTeaBrewBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).noOcclusion())
    );

    public static final DeferredBlock<ShipoCropBlock> BURIED_SHIPO_FRAGMENT = BLOCKS.register(
            "buried_shipo_fragment",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    0,
                    () -> ModBlocks.REPAIRING_SHIPO.get(),
                    5.0
            )
    );

    public static final DeferredBlock<ShipoCropBlock> REPAIRING_SHIPO = BLOCKS.register(
            "repairing_shipo",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    1,
                    () -> ModBlocks.INTACT_SHIPO.get(),
                    6.0
            )
    );

    public static final DeferredBlock<ShipoCropBlock> INTACT_SHIPO = BLOCKS.register(
            "intact_shipo",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    2,
                    null,
                    8.0
            )
    );

    public static final DeferredBlock<NaturalShipoBlock> NATURAL_SHIPO = BLOCKS.register(
            "natural_shipo",
            () -> new NaturalShipoBlock(shipoBlockProperties())
    );

    public static final DeferredBlock<ShipoCropBlock> BURIED_JINGHUAGUSUI_FRAGMENT = BLOCKS.register(
            "buried_jinghuagusui_fragment",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    0,
                    () -> ModBlocks.REPAIRING_JINGHUAGUSUI.get(),
                    ModItems.JINGHUAGUSUI,
                    5.0
            )
    );

    public static final DeferredBlock<ShipoCropBlock> REPAIRING_JINGHUAGUSUI = BLOCKS.register(
            "repairing_jinghuagusui",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    1,
                    () -> ModBlocks.INTACT_JINGHUAGUSUI.get(),
                    ModItems.JINGHUAGUSUI,
                    6.0
            )
    );

    public static final DeferredBlock<ShipoCropBlock> INTACT_JINGHUAGUSUI = BLOCKS.register(
            "intact_jinghuagusui",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    2,
                    null,
                    ModItems.JINGHUAGUSUI,
                    8.0
            )
    );

    public static final DeferredBlock<NaturalJinghuagusuiBlock> NATURAL_JINGHUAGUSUI = BLOCKS.register(
            "natural_jinghuagusui",
            () -> new NaturalJinghuagusuiBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).noOcclusion().lightLevel(state -> 7))
    );

    public static final DeferredBlock<ShipoCropBlock> BURIED_YEBOSHI_FRAGMENT = BLOCKS.register(
            "buried_yeboshi_fragment",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    0,
                    () -> ModBlocks.REPAIRING_YEBOSHI.get(),
                    ModItems.YEBOSHI,
                    8.0
            )
    );

    public static final DeferredBlock<ShipoCropBlock> REPAIRING_YEBOSHI = BLOCKS.register(
            "repairing_yeboshi",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    1,
                    () -> ModBlocks.INTACT_YEBOSHI.get(),
                    ModItems.YEBOSHI,
                    6.0
            )
    );

    public static final DeferredBlock<ShipoCropBlock> INTACT_YEBOSHI = BLOCKS.register(
            "intact_yeboshi",
            () -> new ShipoCropBlock(
                    shipoCropProperties(),
                    2,
                    null,
                    ModItems.YEBOSHI,
                    9.0
            )
    );

    public static final DeferredBlock<NaturalNoctilucousJadeBlock> NATURAL_YEBOSHI = BLOCKS.register(
            "natural_yeboshi",
            () -> new NaturalNoctilucousJadeBlock(shipoBlockProperties(), ModItems.YEBOSHI, false)
    );

    public static final DeferredBlock<NaturalNoctilucousJadeBlock> NATURAL_DEEPSLATE_YEBOSHI = BLOCKS.register(
            "natural_deepslate_yeboshi",
            () -> new NaturalNoctilucousJadeBlock(shipoBlockProperties(), ModItems.YEBOSHI, true)
    );

    public static final DeferredBlock<SmallLampGrassCropBlock> SMALL_LAMP_GRASS_CROP = BLOCKS.register(
            "small_lamp_grass_crop",
            () -> new SmallLampGrassCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)
                    .lightLevel(SmallLampGrassCropBlock::getLightEmission))
    );

    public static final DeferredBlock<WindwheelAsterCropBlock> WINDWHEEL_ASTER_CROP = BLOCKS.register(
            "windwheel_aster_crop",
            () -> new WindwheelAsterCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<CallaLilyCropBlock> CALLA_LILY_CROP = BLOCKS.register(
            "calla_lily_crop",
            () -> new CallaLilyCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<MintCropBlock> MINT_CROP = BLOCKS.register(
            "mint_crop",
            () -> new MintCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<WolfhookCropBlock> WOLFHOOK_CROP = BLOCKS.register(
            "wolfhook_crop",
            () -> new WolfhookCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<SnapdragonCropBlock> SNAPDRAGON_CROP = BLOCKS.register(
            "snapdragon_crop",
            () -> new SnapdragonCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<ValberryCropBlock> VALBERRY_CROP = BLOCKS.register(
            "valberry_crop",
            () -> new ValberryCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<DandelionCropBlock> DANDELION_CROP = BLOCKS.register(
            "dandelion_crop",
            () -> new DandelionCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<CeciliaCropBlock> CECILIA_CROP = BLOCKS.register(
            "cecilia_crop",
            () -> new CeciliaCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<RaspberryCropBlock> RASPBERRY_CROP = BLOCKS.register(
            "raspberry_crop",
            () -> new RaspberryCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<SweetFlowerCropBlock> SWEET_FLOWER_CROP = BLOCKS.register(
            "sweet_flower_crop",
            () -> new SweetFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<WindblumeFlowerBlock> PINK_WINDBLUME = BLOCKS.register(
            "pink_windblume",
            () -> new WindblumeFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WindblumeFlowerBlock> YELLOW_WINDBLUME = BLOCKS.register(
            "yellow_windblume",
            () -> new WindblumeFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WindblumeFlowerBlock> PURPLE_WINDBLUME = BLOCKS.register(
            "purple_windblume",
            () -> new WindblumeFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<JueyunChiliCropBlock> JUEYUN_CHILI_CROP = BLOCKS.register(
            "jueyun_chili_crop",
            () -> new JueyunChiliCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<GlazeLilyCropBlock> GLAZE_LILY_CROP = BLOCKS.register(
            "glaze_lily_crop",
            () -> new GlazeLilyCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<MufengMushroomCropBlock> MUFENG_MUSHROOM_CROP = BLOCKS.register(
            "mufeng_mushroom_crop",
            () -> new MufengMushroomCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<SumeruRoseCropBlock> SUMERU_ROSE_CROP = BLOCKS.register(
            "sumeru_rose_crop",
            () -> new SumeruRoseCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<GrainfruitCropBlock> GRAINFRUIT_CROP = BLOCKS.register(
            "grainfruit_crop",
            () -> new GrainfruitCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<FluorescentFungusCropBlock> FLUORESCENT_FUNGUS_CROP = BLOCKS.register(
            "fluorescent_fungus_crop",
            () -> new FluorescentFungusCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)
                    .lightLevel(FluorescentFungusCropBlock::getLightEmission))
    );

    public static final DeferredBlock<SeaGanodermaCropBlock> SEA_GANODERMA_CROP = BLOCKS.register(
            "sea_ganoderma_crop",
            () -> new SeaGanodermaCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)
                    .lightLevel(SeaGanodermaCropBlock::getLightEmission))
    );

    public static final DeferredBlock<DendrobiumCropBlock> DENDROBIUM_CROP = BLOCKS.register(
            "dendrobium_crop",
            () -> new DendrobiumCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<FrostlampFlowerCropBlock> FROSTLAMP_FLOWER_CROP = BLOCKS.register(
            "frostlamp_flower_crop",
            () -> new FrostlampFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<GrowingCoralShellBlock> GROWING_CORAL_SHELL = BLOCKS.register(
            "growing_coral_shell",
            () -> new GrowingCoralShellBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).randomTicks().sound(SoundType.BONE_BLOCK))
    );

    public static final DeferredBlock<PearlBearingCoralShellBlock> PEARL_BEARING_CORAL_SHELL = BLOCKS.register(
            "pearl_bearing_coral_shell",
            () -> new PearlBearingCoralShellBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).lightLevel(state -> 7).sound(SoundType.BONE_BLOCK))
    );

    public static final DeferredBlock<NaturalCoralPearlBlock> NATURAL_CORAL_PEARL = BLOCKS.register(
            "natural_coral_pearl",
            () -> new NaturalCoralPearlBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SEAGRASS).lightLevel(state -> 7).sound(SoundType.BONE_BLOCK))
    );

    public static final DeferredBlock<HorsetailBottomBlock> HORSETAIL_BOTTOM = BLOCKS.register(
            "horsetail_bottom",
            () -> new HorsetailBottomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).randomTicks())
    );

    public static final DeferredBlock<HorsetailTopBlock> HORSETAIL_TOP = BLOCKS.register(
            "horsetail_top",
            () -> new HorsetailTopBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).randomTicks())
    );

    public static final DeferredBlock<ThirstingJinxinFlowerBlock> THIRSTING_JINXIN_FLOWER = BLOCKS.register(
            "thirsting_jinxin_flower",
            () -> new ThirstingJinxinFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<BlazingJinxinFlowerBlock> BLAZING_JINXIN_FLOWER = BLOCKS.register(
            "blazing_jinxin_flower",
            () -> new BlazingJinxinFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).randomTicks().lightLevel(state -> 7))
    );

    public static final DeferredBlock<BurntOutJinxinFlowerBlock> BURNT_OUT_JINXIN_FLOWER = BLOCKS.register(
            "burnt_out_jinxin_flower",
            () -> new BurntOutJinxinFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT))
    );

    public static final DeferredBlock<WildSmallLampGrassBlock> WILD_SMALL_LAMP_GRASS = BLOCKS.register(
            "wild_small_lamp_grass",
            () -> new WildSmallLampGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).lightLevel(state -> 7))
    );

    public static final DeferredBlock<WildWindwheelAsterBlock> WILD_WINDWHEEL_ASTER = BLOCKS.register(
            "wild_windwheel_aster",
            () -> new WildWindwheelAsterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildCallaLilyBlock> WILD_CALLA_LILY = BLOCKS.register(
            "wild_calla_lily",
            () -> new WildCallaLilyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildMintBlock> WILD_MINT = BLOCKS.register(
            "wild_mint",
            () -> new WildMintBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildWolfhookBlock> WILD_WOLFHOOK = BLOCKS.register(
            "wild_wolfhook",
            () -> new WildWolfhookBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildSnapdragonBlock> WILD_SNAPDRAGON = BLOCKS.register(
            "wild_snapdragon",
            () -> new WildSnapdragonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildValberryBlock> WILD_VALBERRY = BLOCKS.register(
            "wild_valberry",
            () -> new WildValberryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildDandelionBlock> WILD_DANDELION = BLOCKS.register(
            "wild_dandelion",
            () -> new WildDandelionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildCeciliaBlock> WILD_CECILIA = BLOCKS.register(
            "wild_cecilia",
            () -> new WildCeciliaBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildRaspberryBlock> WILD_RASPBERRY = BLOCKS.register(
            "wild_raspberry",
            () -> new WildRaspberryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildSweetFlowerBlock> WILD_SWEET_FLOWER = BLOCKS.register(
            "wild_sweet_flower",
            () -> new WildSweetFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildJueyunChiliBlock> WILD_JUEYUN_CHILI = BLOCKS.register(
            "wild_jueyun_chili",
            () -> new WildJueyunChiliBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildGlazeLilyBlock> WILD_GLAZE_LILY = BLOCKS.register(
            "wild_glaze_lily",
            () -> new WildGlazeLilyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildMufengMushroomBlock> WILD_MUFENG_MUSHROOM = BLOCKS.register(
            "wild_mufeng_mushroom",
            () -> new WildMufengMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildSumeruRoseBlock> WILD_SUMERU_ROSE = BLOCKS.register(
            "wild_sumeru_rose",
            () -> new WildSumeruRoseBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildGrainfruitBlock> WILD_GRAINFRUIT = BLOCKS.register(
            "wild_grainfruit",
            () -> new WildGrainfruitBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildHorsetailBlock> WILD_HORSETAIL = BLOCKS.register(
            "wild_horsetail",
            () -> new WildHorsetailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SEAGRASS))
    );

    public static final DeferredBlock<WildJinxinFlowerBlock> WILD_JINXIN_FLOWER = BLOCKS.register(
            "wild_jinxin_flower",
            () -> new WildJinxinFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).lightLevel(state -> 7))
    );

    public static final DeferredBlock<WildFluorescentFungusBlock> WILD_FLUORESCENT_FUNGUS = BLOCKS.register(
            "wild_fluorescent_fungus",
            () -> new WildFluorescentFungusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).lightLevel(state -> 7))
    );

    public static final DeferredBlock<WildSeaGanodermaBlock> WILD_SEA_GANODERMA = BLOCKS.register(
            "wild_sea_ganoderma",
            () -> new WildSeaGanodermaBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SEAGRASS)
                    .lightLevel(state -> 7))
    );

    public static final DeferredBlock<WildDendrobiumBlock> WILD_DENDROBIUM = BLOCKS.register(
            "wild_dendrobium",
            () -> new WildDendrobiumBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<WildFrostlampFlowerBlock> WILD_FROSTLAMP_FLOWER = BLOCKS.register(
            "wild_frostlamp_flower",
            () -> new WildFrostlampFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))
    );

    public static final DeferredBlock<YunyanCrackleafCropBlock> YUNYAN_LIEYE_CROP = BLOCKS.register(
            "yunyan_lieye_crop",
            () -> new YunyanCrackleafCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noCollission().randomTicks())
    );

    public static final DeferredBlock<WildYunyanCrackleafBlock> WILD_YUNYAN_LIEYE = BLOCKS.register(
            "wild_yunyan_lieye",
            () -> new WildYunyanCrackleafBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ROSE_BUSH).noCollission().instabreak().sound(net.minecraft.world.level.block.SoundType.GRASS))
    );

    private static BlockBehaviour.Properties shipoCropProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).randomTicks().noOcclusion().lightLevel(state -> 7);
    }

    private static BlockBehaviour.Properties shipoBlockProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).noOcclusion().lightLevel(state -> 7);
    }

    public static final DeferredBlock<KatheryneFigurineBlock> KATHERYNE_FIGURINE = BLOCKS.register(
            "katheryne_figurine", () -> new KatheryneFigurineBlock(
                    BlockBehaviour.Properties.of().strength(15.0F).sound(SoundType.METAL).noOcclusion())
    );

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
