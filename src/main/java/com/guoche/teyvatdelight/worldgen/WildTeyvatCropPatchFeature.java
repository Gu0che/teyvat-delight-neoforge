package com.guoche.teyvatdelight.worldgen;

import com.guoche.teyvatdelight.NaturalCoralPearlBlock;
import com.guoche.teyvatdelight.mineral.NaturalMineralPlacement;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.WildCallaLilyBlock;
import com.guoche.teyvatdelight.WildDendrobiumBlock;
import com.guoche.teyvatdelight.WildFluorescentFungusBlock;
import com.guoche.teyvatdelight.WildFrostlampFlowerBlock;
import com.guoche.teyvatdelight.WildGrainfruitBlock;
import com.guoche.teyvatdelight.WildHorsetailBlock;
import com.guoche.teyvatdelight.WildJinxinFlowerBlock;
import com.guoche.teyvatdelight.WildMufengMushroomBlock;
import com.guoche.teyvatdelight.WildRaspberryBlock;
import com.guoche.teyvatdelight.WildSeaGanodermaBlock;
import com.guoche.teyvatdelight.WildSnapdragonBlock;
import com.guoche.teyvatdelight.WildSumeruRoseBlock;
import com.guoche.teyvatdelight.WildTeyvatCropPatchConfiguration;
import com.guoche.teyvatdelight.WildValberryBlock;
import com.guoche.teyvatdelight.WildWolfhookBlock;
import com.guoche.teyvatdelight.WindblumeFlowerBlock;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class WildTeyvatCropPatchFeature extends Feature<WildTeyvatCropPatchConfiguration> {
    private static final NormalNoise MEADOW_WINDBLUME_COLOR_NOISE = NormalNoise.create(
            new WorldgenRandom(new LegacyRandomSource(2345L)), new NormalNoise.NoiseParameters(-7, 1.0D));

    public WildTeyvatCropPatchFeature(Codec<WildTeyvatCropPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WildTeyvatCropPatchConfiguration> context) {
        WildTeyvatCropPatchConfiguration config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockState state = config.state();

        if (state.getBlock() instanceof WindblumeFlowerBlock) {
            int color = config.meadowWindblume() ? meadowWindblumeColor(origin) : random.nextInt(3);
            state = switch (color) {
                case 0 -> TeyvatDelight.PINK_WINDBLUME.get().defaultBlockState();
                case 1 -> TeyvatDelight.YELLOW_WINDBLUME.get().defaultBlockState();
                default -> TeyvatDelight.PURPLE_WINDBLUME.get().defaultBlockState();
            };
            if (config.meadowWindblume()) {
                return placeMeadowWindblumePatch(config, level, random, origin, state);
            }
        }

        if (state.getBlock() instanceof WildMufengMushroomBlock) {
            if (config.mufengTopOnlyStripped()) {
                return placeTopOnlyStrippedMufengPatch(config, level, random, origin, state);
            }
            return placeClingingPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildCallaLilyBlock || state.getBlock() instanceof WildSnapdragonBlock) {
            return placeWildCallaLilyPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildHorsetailBlock) {
            return placeWildHorsetailPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildSeaGanodermaBlock) {
            return placeWildSeaGanodermaPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof NaturalCoralPearlBlock) {
            return placeWildCoralPearlPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildJinxinFlowerBlock) {
            return placeDeepScanningPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildFluorescentFungusBlock) {
            return placeDeepScanningPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildDendrobiumBlock) {
            return placeDeepScanningPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildFrostlampFlowerBlock) {
            return placeFrostlampPatch(config, level, random, origin, state);
        }

        if (state.getBlock() instanceof WildGrainfruitBlock
                || state.getBlock() instanceof WildSumeruRoseBlock
                || state.getBlock() instanceof WildWolfhookBlock
                || state.getBlock() instanceof WildValberryBlock
                || state.getBlock() instanceof WildRaspberryBlock) {
            return placeSurfaceScanningPatch(config, level, random, origin, state);
        }

        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 4, 20);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (level.ensureCanWrite(pos) && level.isEmptyBlock(pos)
                    && NaturalMineralPlacement.canGenerateAt(state, level, pos)) {
                level.setBlock(pos, state, 2);
                placed++;
            }
        }

        return placed > 0;
    }

    private static int meadowWindblumeColor(BlockPos origin) {
        double noise = MEADOW_WINDBLUME_COLOR_NOISE.getValue(origin.getX(), 0, origin.getZ());
        return noise < -0.12D ? 0 : noise > 0.12D ? 2 : 1;
    }

    private static boolean placeMeadowWindblumePatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int placed = 0;
        int xzRange = config.xzSpread() + 1;
        int yRange = config.ySpread() + 1;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int attempt = 0; attempt < config.tries(); attempt++) {
            pos.setWithOffset(origin,
                    random.nextInt(xzRange) - random.nextInt(xzRange),
                    random.nextInt(yRange) - random.nextInt(yRange),
                    random.nextInt(xzRange) - random.nextInt(xzRange));
            if (level.ensureCanWrite(pos) && level.isEmptyBlock(pos) && state.canSurvive(level, pos)) {
                level.setBlock(pos, state, 2);
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean placeWildCallaLilyPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 8, 32);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            if (tryPlaceWildCallaLily(level, state, pos)) {
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean tryPlaceWildCallaLily(WorldGenLevel level, BlockState state, BlockPos pos) {
        BlockPos groundPos = pos.below();
        if (!level.ensureCanWrite(pos)
                || !level.isEmptyBlock(pos)
                || !isCallaLilySurface(level.getBlockState(groundPos))
                || !hasAdjacentWater(level, groundPos)
                || !state.canSurvive(level, pos)) {
            return false;
        }

        level.setBlock(pos, state, 2);
        return true;
    }

    private static boolean placeClingingPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 48, 96);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            for (int yOffset = 0; yOffset < 7 && placed < targetCount; yOffset++) {
                int supportY = topY - 1 - yOffset;
                if (supportY < level.getMinBuildHeight()) {
                    break;
                }

                BlockPos supportPos = new BlockPos(x, supportY, z);
                if (WildMufengMushroomBlock.canAttachTo(level.getBlockState(supportPos))
                        && tryPlaceFromSupport(level, random, state, supportPos, topY)) {
                    placed++;
                }
            }
        }

        return placed > 0;
    }

    private static boolean tryPlaceFromSupport(
            WorldGenLevel level,
            RandomSource random,
            BlockState baseState,
            BlockPos supportPos,
            int columnTopY
    ) {
        Direction[] outwardDirections = {
                Direction.UP,
                Direction.NORTH,
                Direction.SOUTH,
                Direction.WEST,
                Direction.EAST
        };
        int start = random.nextInt(outwardDirections.length);

        for (int i = 0; i < outwardDirections.length; i++) {
            Direction outwardDirection = outwardDirections[(start + i) % outwardDirections.length];
            BlockPos pos = getTargetPos(supportPos, outwardDirection);
            if (pos == null || !isGoodMufengTarget(level, pos)) {
                continue;
            }

            if (outwardDirection != Direction.UP
                    && (supportPos.getY() < columnTopY - 4 || !level.getBlockState(pos.below()).isAir())) {
                continue;
            }

            BlockState placedState = baseState.setValue(WildMufengMushroomBlock.FACING, outwardDirection);
            if (placedState.canSurvive(level, pos)) {
                level.setBlock(pos, placedState, 2);
                return true;
            }
        }

        return false;
    }

    private static BlockPos getTargetPos(BlockPos supportPos, Direction outwardDirection) {
        if (!WildMufengMushroomBlock.isAllowedAttachmentDirection(outwardDirection)) {
            return null;
        }

        return supportPos.relative(outwardDirection);
    }

    private static boolean isGoodMufengTarget(WorldGenLevel level, BlockPos pos) {
        return level.ensureCanWrite(pos)
                && level.isEmptyBlock(pos)
                && level.canSeeSky(pos);
    }

    private static boolean placeWildHorsetailPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        BlockPos basePos = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, origin);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int placed = 0;

        for (int attempt = 0; attempt < config.tries() && placed < targetCount; attempt++) {
            mutable.set(basePos).move(
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1),
                    random.nextInt(config.ySpread() + 1) - random.nextInt(config.ySpread() + 1),
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1)
            );

            if (tryPlaceWildHorsetail(level, state, mutable)) {
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean placeWildSeaGanodermaPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        BlockPos basePos = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, origin);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int placed = 0;

        for (int attempt = 0; attempt < config.tries() && placed < targetCount; attempt++) {
            mutable.set(basePos).move(
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1),
                    random.nextInt(config.ySpread() + 1) - random.nextInt(config.ySpread() + 1),
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1)
            );

            if (tryPlaceWildSeaGanoderma(level, state, mutable)) {
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean tryPlaceWildSeaGanoderma(WorldGenLevel level, BlockState state, BlockPos waterPos) {
        if (!level.ensureCanWrite(waterPos)
                || !level.getBlockState(waterPos).is(Blocks.WATER)) {
            return false;
        }

        BlockState placedState = state.setValue(WildSeaGanodermaBlock.WATERLOGGED, true);
        if (!placedState.canSurvive(level, waterPos)) {
            return false;
        }

        level.setBlock(waterPos, placedState, 2);
        return true;
    }

    private static boolean placeWildCoralPearlPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        BlockPos basePos = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, origin);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int placed = 0;

        for (int attempt = 0; attempt < config.tries() && placed < targetCount; attempt++) {
            mutable.set(basePos).move(
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1),
                    random.nextInt(config.ySpread() + 1) - random.nextInt(config.ySpread() + 1),
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1)
            );

            if (tryPlaceWildCoralPearl(level, state, mutable, random)) {
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean tryPlaceWildCoralPearl(WorldGenLevel level, BlockState state, BlockPos pos, RandomSource random) {
        if (!level.ensureCanWrite(pos)) {
            return false;
        }

        boolean isWater = level.getFluidState(pos).is(FluidTags.WATER);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState placedState = state
                .setValue(NaturalCoralPearlBlock.WATERLOGGED, isWater)
                .setValue(NaturalCoralPearlBlock.FACING, facing);

        if (isWater) {
            if (level.getFluidState(pos).getAmount() != 8) {
                return false;
            }
        } else if (!level.isEmptyBlock(pos)) {
            return false;
        }

        if (!placedState.canSurvive(level, pos)) {
            return false;
        }

        level.setBlock(pos, placedState, 2);
        return true;
    }

    private static boolean placeDeepScanningPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 12, 48);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            int minY = Math.max(level.getMinBuildHeight() + 1, topY - 96);

            for (int y = topY; y >= minY; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (level.ensureCanWrite(pos) && level.isEmptyBlock(pos) && state.canSurvive(level, pos)) {
                    level.setBlock(pos, state, 2);
                    placed++;
                    break;
                }
            }
        }

        return placed > 0;
    }

    private static boolean placeSurfaceScanningPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 16, 48);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            int minY = Math.max(level.getMinBuildHeight() + 1, topY - 48);

            for (int y = topY; y >= minY; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (level.ensureCanWrite(pos) && level.isEmptyBlock(pos) && state.canSurvive(level, pos)) {
                    level.setBlock(pos, state, 2);
                    placed++;
                    break;
                }
            }
        }

        return placed > 0;
    }

    private static boolean placeFrostlampPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = minCount + random.nextInt(maxCount - minCount + 1);
        int attempts = Math.max(targetCount * 16, 48);
        int placed = 0;

        for (int attempt = 0; attempt < attempts && placed < targetCount; attempt++) {
            int x = origin.getX() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int z = origin.getZ() + random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread();
            int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos airPos = new BlockPos(x, topY, z);
            BlockPos surfacePos = airPos.below();
            BlockState surfaceState = level.getBlockState(surfacePos);

            if (surfaceState.is(Blocks.SNOW)) {
                // 雪片：穿透雪层，把雪片那格替换成霜盏花，脚下是支撑方块
                if (level.ensureCanWrite(surfacePos) && state.canSurvive(level, surfacePos)) {
                    level.setBlock(surfacePos, state, 2);
                    placed++;
                }
            } else if (level.ensureCanWrite(airPos) && level.isEmptyBlock(airPos) && state.canSurvive(level, airPos)) {
                // 空气：正常种在支撑方块上方
                level.setBlock(airPos, state, 2);
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean tryPlaceWildHorsetail(WorldGenLevel level, BlockState state, BlockPos waterPos) {
        BlockPos topPos = waterPos.above();
        if (!level.ensureCanWrite(waterPos)
                || !level.ensureCanWrite(topPos)
                || !level.getBlockState(waterPos).is(Blocks.WATER)
                || !level.isEmptyBlock(topPos)) {
            return false;
        }

        BlockState lowerState = state
                .setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(WildHorsetailBlock.WATERLOGGED, true);

        if (!lowerState.canSurvive(level, waterPos)) {
            return false;
        }

        DoublePlantBlock.placeAt(level, lowerState, waterPos, 2);
        return true;
    }

    private static boolean placeTopOnlyStrippedMufengPatch(
            WildTeyvatCropPatchConfiguration config,
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            BlockState state
    ) {
        List<BlockPos> candidates = new ArrayList<>();
        int spread = config.xzSpread();

        for (int xOffset = -spread; xOffset <= spread; xOffset++) {
            for (int zOffset = -spread; zOffset <= spread; zOffset++) {
                int x = origin.getX() + xOffset;
                int z = origin.getZ() + zOffset;
                int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockPos supportPos = new BlockPos(x, topY - 1, z);
                BlockPos pos = supportPos.above();

                if (WildMufengMushroomBlock.isStrippedWoodAttachment(level.getBlockState(supportPos))
                        && isGoodMufengTarget(level, pos)) {
                    candidates.add(pos);
                }
            }
        }

        int minCount = Math.min(config.minCount(), config.maxCount());
        int maxCount = Math.max(config.minCount(), config.maxCount());
        int targetCount = Math.min(candidates.size(), minCount + random.nextInt(maxCount - minCount + 1));
        int placed = 0;

        while (placed < targetCount && !candidates.isEmpty()) {
            BlockPos pos = candidates.remove(random.nextInt(candidates.size()));
            BlockState placedState = state.setValue(WildMufengMushroomBlock.FACING, Direction.UP);
            if (placedState.canSurvive(level, pos)) {
                level.setBlock(pos, placedState, 2);
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean hasAdjacentWater(WorldGenLevel level, BlockPos groundPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = groundPos.relative(direction);
            if (level.getFluidState(neighborPos).is(FluidTags.WATER)
                    || level.getBlockState(neighborPos).is(Blocks.FROSTED_ICE)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isCallaLilySurface(BlockState state) {
        return state.is(BlockTags.SAND) || state.is(Blocks.DIRT);
    }
}
