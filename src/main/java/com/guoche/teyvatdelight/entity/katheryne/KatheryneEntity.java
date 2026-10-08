package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.KatheryneData;
import com.guoche.teyvatdelight.KatheryneMenu;
import com.guoche.teyvatdelight.KatheryneSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class KatheryneEntity extends PathfinderMob {
  // Vanilla handles some positive entity events before dispatching to this entity.
  private static final byte WHY = -100;
  private static final byte THANKS = -101;
  private static final byte HAPPY = -102;
  private static final byte QUESTION_ON = -103;
  private static final byte QUESTION_OFF = -104;
  private final java.util.Map<java.util.UUID, Boolean> questVisuals = new java.util.HashMap<>();

  public final AnimationState whyAnimation = new AnimationState();
  public final AnimationState thanksAnimation = new AnimationState();
  public final AnimationState happyAnimation = new AnimationState();
  public final AnimationState questionAnimation = new AnimationState();

  public KatheryneEntity(EntityType<? extends KatheryneEntity> type, Level level) {
    super(type, level);
    setPersistenceRequired();
    ambientSoundTime = -getAmbientSoundInterval();
  }

  public static AttributeSupplier.Builder createAttributes() {
    return PathfinderMob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 20.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.0D)
        .add(Attributes.FOLLOW_RANGE, 12.0D);
  }

  @Override
  protected void registerGoals() {
    goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 7.0F));
    goalSelector.addGoal(2, new RandomLookAroundGoal(this));
  }

  @Override
  protected InteractionResult mobInteract(Player player, InteractionHand hand) {
    if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
    if (player instanceof ServerPlayer serverPlayer) {
      KatheryneData.get(serverPlayer.server).commissions.advance(
          serverPlayer, "event", "teyvatdelight:katheryne_interaction", 1);
      KatheryneData.get(serverPlayer.server).questFor(serverPlayer);
      level().broadcastEntityEvent(this, WHY);
      ambientSoundTime = -getAmbientSoundInterval();
      playSound(KatheryneSounds.INTERACT.get(), 1.0F, 1.0F);
      serverPlayer.openMenu(
          new SimpleMenuProvider(
              (id, inventory, viewer) -> new KatheryneMenu(id, inventory, serverPlayer, this),
              Component.translatable("entity.teyvatdelight.katheryne")));
      if (serverPlayer.containerMenu instanceof KatheryneMenu menu) menu.sendSnapshot();
    }
    return InteractionResult.sidedSuccess(level().isClientSide);
  }

  public void playThanks() {
    level().broadcastEntityEvent(this, THANKS);
    ambientSoundTime = -getAmbientSoundInterval();
    playSound(KatheryneSounds.COMMISSION_COMPLETE.get(), 1.0F, 1.0F);
  }

  public void playHappy() {
    level().broadcastEntityEvent(this, HAPPY);
  }

  @Override
  protected SoundEvent getAmbientSound() {
    return KatheryneSounds.IDLE_ANOMALY.get();
  }

  @Override
  public int getAmbientSoundInterval() {
    return 1200 + random.nextInt(1200);
  }

  @Override
  public float getVoicePitch() {
    return 1.0F;
  }

  public void updateQuestVisual(ServerPlayer player) {
    var data = KatheryneData.get(player.server);
    boolean available = data.commissions.hasWork(player, data.questFor(player));
    Boolean previous = questVisuals.put(player.getUUID(), available);
    if (previous != null && previous == available) return;
    player.connection.send(
        new ClientboundEntityEventPacket(this, available ? QUESTION_ON : QUESTION_OFF));
  }

  @Override
  public void startSeenByPlayer(ServerPlayer player) {
    super.startSeenByPlayer(player);
    questVisuals.remove(player.getUUID());
    updateQuestVisual(player);
  }

  @Override
  public void stopSeenByPlayer(ServerPlayer player) {
    super.stopSeenByPlayer(player);
    questVisuals.remove(player.getUUID());
  }

  @Override
  public void tick() {
    super.tick();
    yBodyRot = Mth.rotLerp(0.12F, yBodyRot, getYHeadRot());
    setYRot(yBodyRot);
    if (level() instanceof ServerLevel serverLevel) {
      if (tickCount % 20 == 0) {
        for (ServerPlayer player : serverLevel.players()) {
          if (questVisuals.containsKey(player.getUUID())) updateQuestVisual(player);
        }
      }
    }
  }

  @Override
  public void handleEntityEvent(byte id) {
    if (id == WHY || id == THANKS || id == HAPPY) {
      whyAnimation.stop();
      thanksAnimation.stop();
      happyAnimation.stop();
      (id == WHY ? whyAnimation : id == THANKS ? thanksAnimation : happyAnimation).start(tickCount);
    } else if (id == QUESTION_ON) {
      questionAnimation.startIfStopped(tickCount);
    } else if (id == QUESTION_OFF) {
      questionAnimation.stop();
    } else {
      super.handleEntityEvent(id);
    }
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  @Override
  public void push(Vec3 movement) {}

  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) {
    return false;
  }
}
