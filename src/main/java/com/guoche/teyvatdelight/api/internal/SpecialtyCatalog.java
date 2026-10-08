package com.guoche.teyvatdelight.api.internal;

import com.guoche.teyvatdelight.api.TeyvatCropApi;
import com.guoche.teyvatdelight.api.TeyvatCropApi.*;
import com.guoche.teyvatdelight.api.TeyvatMineralApi;
import com.guoche.teyvatdelight.api.TeyvatTags;
import com.guoche.teyvatdelight.crop.BlazingJinxinFlowerBlock;
import com.guoche.teyvatdelight.crop.HorsetailBottomBlock;
import com.guoche.teyvatdelight.crop.HorsetailTopBlock;
import com.guoche.teyvatdelight.crop.TeyvatCropBlock;
import com.guoche.teyvatdelight.registry.ModBlocks;
import com.guoche.teyvatdelight.registry.ModItems;
import java.util.*;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/** Implementation only. External integrations must use the public facades, not this catalog. */
public final class SpecialtyCatalog {
    private static volatile SpecialtyCatalog instance;
    private final List<Definition> crops;
    private final List<TeyvatMineralApi.Definition> minerals;
    private final Map<ResourceLocation, Definition> cropIds = new LinkedHashMap<>();
    private final Map<Item, List<Definition>> cropItems = new HashMap<>();
    private final Map<Block, Function<BlockState, State>> cropStates = new HashMap<>();
    private final Map<ResourceLocation, TeyvatMineralApi.Definition> mineralIds = new LinkedHashMap<>();
    private final Map<Item, TeyvatMineralApi.Definition> mineralItems = new HashMap<>();
    private final Map<Block, TeyvatMineralApi.State> mineralStates = new HashMap<>();

    /** Publish only a complete catalog; premature calls can be retried after registration. */
    public static SpecialtyCatalog get() {
        var value = instance;
        if (value == null) {
            synchronized (SpecialtyCatalog.class) {
                value = instance;
                if (value == null) instance = value = new SpecialtyCatalog();
            }
        }
        return value;
    }

    private SpecialtyCatalog() {
        ModBlocks.WILD_YUNYAN_LIEYE.get();
        for (Block block : BuiltInRegistries.BLOCK) {
            var key = BuiltInRegistries.BLOCK.getKey(block);
            if (!key.getNamespace().equals("teyvatdelight") || !(block instanceof TeyvatCropBlock crop)) continue;
            String name = key.getPath();
            if (name.endsWith("_crop")) name = name.substring(0, name.length() - 5);
            var wildId = id("wild_" + name);
            List<Block> wild = BuiltInRegistries.BLOCK.containsKey(wildId)
                    ? List.of(BuiltInRegistries.BLOCK.get(wildId)) : List.of();
            HarvestMethod method = name.equals("dandelion") ? HarvestMethod.SNEAKING : HarvestMethod.RIGHT_CLICK;
            var info = addCrop(name, crop.getProduceItem(), crop.getPlantingItem(),
                    crop.getPreferredFieldTag(), List.of(block), wild, method);
            cropStates.put(block, state -> new State(info, Form.CULTIVATED, Part.SINGLE,
                    crop.getAge(state), crop.getMaxAge(),
                    crop.isMaxAge(state) ? Maturity.MATURE : Maturity.IMMATURE, method));
        }
        var horsetail = addCrop("horsetail", ModItems.HORSETAIL.get(), ModItems.HORSETAIL_SEEDS.get(),
                TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS,
                List.of(ModBlocks.HORSETAIL_BOTTOM.get(), ModBlocks.HORSETAIL_TOP.get()),
                List.of(ModBlocks.WILD_HORSETAIL.get()), HarvestMethod.RIGHT_CLICK);
        cropStates.put(ModBlocks.HORSETAIL_BOTTOM.get(), state -> new State(horsetail, Form.CULTIVATED,
                Part.LOWER, state.getValue(HorsetailBottomBlock.AGE), 3,
                state.getValue(HorsetailBottomBlock.AGE) == 3 ? Maturity.UNKNOWN : Maturity.IMMATURE,
                HarvestMethod.RIGHT_CLICK));
        cropStates.put(ModBlocks.HORSETAIL_TOP.get(), state -> new State(horsetail, Form.CULTIVATED,
                Part.UPPER, state.getValue(HorsetailTopBlock.AGE), 2,
                state.getValue(HorsetailTopBlock.AGE) == 2 ? Maturity.UNKNOWN : Maturity.IMMATURE,
                HarvestMethod.RIGHT_CLICK));
        cropStates.put(ModBlocks.WILD_HORSETAIL.get(), state -> new State(horsetail, Form.WILD,
                state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                        ? Part.LOWER : Part.UPPER, 0, 0, Maturity.UNKNOWN, HarvestMethod.BREAK));

        var jinxin = addCrop("jinxin_flower", ModItems.JINXIN_FLOWER.get(), ModItems.JINXIN_FLOWER_BUD.get(),
                TeyvatTags.Blocks.XUAN_CI_JADE_FIELDS, List.of(ModBlocks.THIRSTING_JINXIN_FLOWER.get(),
                        ModBlocks.BLAZING_JINXIN_FLOWER.get(), ModBlocks.BURNT_OUT_JINXIN_FLOWER.get()),
                List.of(ModBlocks.WILD_JINXIN_FLOWER.get()), HarvestMethod.COOLING);
        fixed(ModBlocks.THIRSTING_JINXIN_FLOWER.get(), jinxin, false);
        cropStates.put(ModBlocks.BLAZING_JINXIN_FLOWER.get(), state -> new State(jinxin, Form.CULTIVATED,
                Part.SINGLE, state.getValue(BlazingJinxinFlowerBlock.AGE),
                BlazingJinxinFlowerBlock.MAX_AGE - 1, Maturity.IMMATURE, HarvestMethod.COOLING));
        fixed(ModBlocks.BURNT_OUT_JINXIN_FLOWER.get(), jinxin, true);

        var pearl = addCrop("coral_pearl", ModItems.CORAL_PEARL.get(), ModItems.CORAL_SHELL.get(),
                TeyvatTags.Blocks.CHU_CI_ZHU_FIELDS,
                List.of(ModBlocks.GROWING_CORAL_SHELL.get(), ModBlocks.PEARL_BEARING_CORAL_SHELL.get()),
                List.of(ModBlocks.NATURAL_CORAL_PEARL.get()), HarvestMethod.KNIFE);
        fixed(ModBlocks.GROWING_CORAL_SHELL.get(), pearl, false);
        fixed(ModBlocks.PEARL_BEARING_CORAL_SHELL.get(), pearl, true);
        windblume("pink_windblume", ModBlocks.PINK_WINDBLUME.get());
        windblume("yellow_windblume", ModBlocks.YELLOW_WINDBLUME.get());
        windblume("purple_windblume", ModBlocks.PURPLE_WINDBLUME.get());
        cropItems.replaceAll((item, definitions) -> List.copyOf(definitions));
        crops = List.copyOf(cropIds.values());

        addMineral("shipo", ModItems.SHIPO.get(), List.of(ModBlocks.BURIED_SHIPO_FRAGMENT.get(),
                ModBlocks.REPAIRING_SHIPO.get(), ModBlocks.INTACT_SHIPO.get()), List.of(ModBlocks.NATURAL_SHIPO.get()));
        addMineral("jinghuagusui", ModItems.JINGHUAGUSUI.get(), List.of(ModBlocks.BURIED_JINGHUAGUSUI_FRAGMENT.get(),
                ModBlocks.REPAIRING_JINGHUAGUSUI.get(), ModBlocks.INTACT_JINGHUAGUSUI.get()),
                List.of(ModBlocks.NATURAL_JINGHUAGUSUI.get()));
        addMineral("yeboshi", ModItems.YEBOSHI.get(), List.of(ModBlocks.BURIED_YEBOSHI_FRAGMENT.get(),
                ModBlocks.REPAIRING_YEBOSHI.get(), ModBlocks.INTACT_YEBOSHI.get()),
                List.of(ModBlocks.NATURAL_YEBOSHI.get(), ModBlocks.NATURAL_DEEPSLATE_YEBOSHI.get()));
        minerals = List.copyOf(mineralIds.values());
    }

    private Definition addCrop(String name, Item produce, Item planting, TagKey<Block> field,
            List<Block> cultivated, List<Block> wild, HarvestMethod method) {
        var info = new Definition(id(name), produce, planting, field, cultivated, wild, method);
        if (cropIds.putIfAbsent(info.id(), info) != null)
            throw new IllegalStateException("Duplicate crop definition " + info.id());
        index(produce, info);
        index(planting, info);
        for (Block block : cultivated) index(block.asItem(), info);
        for (Block block : wild) {
            index(block.asItem(), info);
            HarvestMethod wildMethod = method == HarvestMethod.COOLING || method == HarvestMethod.SNEAKING
                    ? method : HarvestMethod.BREAK;
            cropStates.put(block, state -> new State(info, Form.WILD, Part.SINGLE,
                    0, 0, Maturity.MATURE, wildMethod));
        }
        return info;
    }

    private void index(Item item, Definition info) {
        if (item == Items.AIR) return;
        var list = cropItems.computeIfAbsent(item, ignored -> new ArrayList<>());
        if (!list.contains(info)) list.add(info);
    }

    private void fixed(Block block, Definition info, boolean mature) {
        cropStates.put(block, state -> new State(info, Form.CULTIVATED, Part.SINGLE, 0, 0,
                mature ? Maturity.MATURE : Maturity.IMMATURE, info.cultivatedHarvestMethod()));
    }

    private void windblume(String name, Block block) {
        var info = addCrop(name, ModItems.WINDBLUME.get(), block.asItem(),
                TeyvatTags.Blocks.NI_CI_ZHI_FIELDS, List.of(block), List.of(block), HarvestMethod.BONEMEAL);
        cropStates.put(block, state -> new State(info, Form.SHARED, Part.SINGLE, 0, 0,
                Maturity.MATURE, HarvestMethod.BONEMEAL));
    }

    private void addMineral(String name, Item produce, List<Block> cultivated, List<Block> natural) {
        var info = new TeyvatMineralApi.Definition(id(name), produce, produce,
                TeyvatTags.Blocks.XUAN_CI_PU_FIELDS, ItemTags.PICKAXES, cultivated, natural);
        mineralIds.put(info.id(), info);
        mineralItems.put(produce, info);
        var stages = List.of(TeyvatMineralApi.Stage.BURIED, TeyvatMineralApi.Stage.REPAIRING,
                TeyvatMineralApi.Stage.INTACT);
        for (int i = 0; i < cultivated.size(); i++)
            mineralStates.put(cultivated.get(i), new TeyvatMineralApi.State(info, stages.get(i), false));
        for (Block block : natural) {
            mineralItems.put(block.asItem(), info);
            mineralStates.put(block, new TeyvatMineralApi.State(info, TeyvatMineralApi.Stage.NATURAL,
                    block == ModBlocks.NATURAL_DEEPSLATE_YEBOSHI.get()));
        }
    }

    public List<Definition> crops() { return crops; }
    public List<TeyvatMineralApi.Definition> minerals() { return minerals; }
    public Optional<Definition> crop(ResourceLocation id) { return Optional.ofNullable(cropIds.get(id)); }
    public List<Definition> crops(Item item) { return cropItems.getOrDefault(item, List.of()); }
    public Optional<TeyvatMineralApi.Definition> mineral(ResourceLocation id) {
        return Optional.ofNullable(mineralIds.get(id));
    }
    public Optional<TeyvatMineralApi.Definition> mineral(Item item) {
        return Optional.ofNullable(mineralItems.get(item));
    }
    public Optional<TeyvatMineralApi.State> inspectMineral(BlockState state) {
        return Optional.ofNullable(mineralStates.get(state.getBlock()));
    }
    public Optional<State> inspectCrop(BlockState state) {
        var resolver = cropStates.get(state.getBlock());
        return resolver == null ? Optional.empty() : Optional.of(resolver.apply(state));
    }

    public Optional<State> inspectCrop(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        return inspectCrop(state).map(view -> {
            if (view.maturity() != Maturity.UNKNOWN) return view;
            var otherPos = view.part() == Part.LOWER ? pos.above() : pos.below();
            if (!level.isOutsideBuildHeight(otherPos) && !level.hasChunkAt(otherPos)) return view;
            boolean mature = false;
            if (!level.isOutsideBuildHeight(otherPos)) {
                var other = level.getBlockState(otherPos);
                if (view.form() == Form.WILD) {
                    mature = other.is(state.getBlock())
                            && other.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)
                                    != state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
                } else if (view.part() == Part.LOWER) {
                    mature = other.is(ModBlocks.HORSETAIL_TOP.get()) && other.getValue(HorsetailTopBlock.AGE) == 2;
                } else {
                    mature = other.is(ModBlocks.HORSETAIL_BOTTOM.get())
                            && other.getValue(HorsetailBottomBlock.AGE) == 3;
                }
            }
            return new State(view.definition(), view.form(), view.part(), view.age(), view.maxAge(),
                    mature ? Maturity.MATURE : Maturity.IMMATURE, view.harvestMethod());
        });
    }

    private static ResourceLocation id(String path) {
        return Objects.requireNonNull(ResourceLocation.tryParse("teyvatdelight:" + path));
    }
}
