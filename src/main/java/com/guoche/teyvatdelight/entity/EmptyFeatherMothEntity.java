package com.guoche.teyvatdelight.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class EmptyFeatherMothEntity extends Bat {
    private static final double FLIGHT_SPEED = 0.25D;
    private static final double FLIGHT_ACCELERATION = 0.12D;
    private static final int FLIGHT_RADIUS = 7;

    @Nullable
    private BlockPos targetPosition;

    public EmptyFeatherMothEntity(EntityType<? extends EmptyFeatherMothEntity> entityType, Level level) {
        super(entityType, level);
        this.setResting(false);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Bat.createAttributes().add(Attributes.MAX_HEALTH, 2.0D);
    }

    public static boolean checkSpawnRules(EntityType<? extends EmptyFeatherMothEntity> entityType, LevelAccessor level,
                                          MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                && level.getFluidState(pos).isEmpty()
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && level.getRawBrightness(pos, 0) > 8;
    }

    @Override
    public boolean isFlapping() {
        return true;
    }

    @Override
    public boolean isResting() {
        return false;
    }

    @Override
    public void setResting(boolean resting) {
        super.setResting(false);
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        super.tick();
    }

    @Override
    protected void customServerAiStep() {
        this.setNoGravity(true);
        if (this.targetPosition == null
                || this.random.nextInt(35) == 0
                || this.targetPosition.closerToCenterThan(this.position(), 1.4D)
                || !isFreeFlightTarget(this.targetPosition)) {
            chooseTargetPosition();
        }

        if (this.targetPosition == null) {
            return;
        }

        Vec3 offset = Vec3.atCenterOf(this.targetPosition).subtract(this.position());
        if (offset.lengthSqr() < 0.2D) {
            this.targetPosition = null;
            return;
        }

        Vec3 desired = offset.normalize().scale(FLIGHT_SPEED);
        int surfaceY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.getBlockX(), this.getBlockZ());
        if (this.getY() < surfaceY + 1.5D) {
            desired = new Vec3(desired.x, Math.max(desired.y, 0.18D), desired.z);
        }

        Vec3 motion = this.getDeltaMovement();
        Vec3 nextMotion = motion.add(
                (desired.x - motion.x) * FLIGHT_ACCELERATION,
                (desired.y - motion.y) * FLIGHT_ACCELERATION,
                (desired.z - motion.z) * FLIGHT_ACCELERATION
        );
        this.setDeltaMovement(nextMotion);
        if (nextMotion.horizontalDistanceSqr() > 1.0E-6D) {
            float yaw = (float) (Mth.atan2(nextMotion.z, nextMotion.x) * 180.0F / Math.PI) - 90.0F;
            this.setYRot(this.getYRot() + Mth.wrapDegrees(yaw - this.getYRot()));
            this.yBodyRot = this.getYRot();
        }
        this.zza = 0.25F;
    }

    private void chooseTargetPosition() {
        for (int attempt = 0; attempt < 12; attempt++) {
            int x = this.getBlockX() + this.random.nextInt(FLIGHT_RADIUS * 2 + 1) - FLIGHT_RADIUS;
            int z = this.getBlockZ() + this.random.nextInt(FLIGHT_RADIUS * 2 + 1) - FLIGHT_RADIUS;
            int surfaceY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            int minY = Math.max(surfaceY + 2, this.level().getMinBuildHeight() + 1);
            int maxY = Math.min(surfaceY + 5, this.level().getMaxBuildHeight() - 2);
            if (maxY < minY) {
                continue;
            }

            BlockPos candidate = new BlockPos(x, minY + this.random.nextInt(maxY - minY + 1), z);
            if (isFreeFlightTarget(candidate)) {
                this.targetPosition = candidate;
                return;
            }
        }

        BlockPos fallback = this.blockPosition().above(2 + this.random.nextInt(3));
        this.targetPosition = isFreeFlightTarget(fallback) ? fallback : null;
    }

    private boolean isFreeFlightTarget(BlockPos pos) {
        return pos.getY() > this.level().getMinBuildHeight()
                && pos.getY() < this.level().getMaxBuildHeight() - 1
                && this.level().getFluidState(pos).isEmpty()
                && this.level().getBlockState(pos).getCollisionShape(this.level(), pos).isEmpty()
                && this.level().getBlockState(pos.above()).getCollisionShape(this.level(), pos.above()).isEmpty();
    }
}
