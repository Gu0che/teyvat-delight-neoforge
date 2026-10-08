package com.guoche.teyvatdelight.client.katheryne;

import com.guoche.teyvatdelight.KatheryneMenu;
import com.guoche.teyvatdelight.KatheryneSnapshot;
import com.guoche.teyvatdelight.TeyvatDelight;
import com.guoche.teyvatdelight.entity.katheryne.CommissionBook;
import com.guoche.teyvatdelight.entity.katheryne.KatheryneNetwork;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;

public class KatheryneScreen extends AbstractContainerScreen<KatheryneMenu> {
  private static final ResourceLocation PORTRAIT =
      ResourceLocation.fromNamespaceAndPath(
          TeyvatDelight.MODID, "textures/gui/katheryne_portrait.png");
  private static final int BACK = 0xC01C2527, BORDER = 0xFF779693, TEXT = 0xFFE9F0E5, ROWS = 5;
  private static final int DETAIL_TEXT_TOP = 85, DETAIL_TEXT_BOTTOM = 151,
      DETAIL_INTRO_ROWS = 6, DETAIL_OBJECTIVE_ROWS = 4, DETAIL_OBJECTIVE_STEP = 16,
      DETAIL_REWARD_LABEL_Y = 153, DETAIL_REWARDS_Y = 164, DETAIL_SUBMIT_Y = 184;
  private int tab, tabOffset, scroll, detailScroll, rewardScroll, bonusScroll;
  private boolean draggingScroll;
  private String selected = "";
  private int cursorX, cursorY;
  private final KatheryneItemHitMap itemHits = new KatheryneItemHitMap();
  private KatheryneSnapshot renderedSnapshot;
  private final Map<String, net.minecraft.world.entity.LivingEntity> previews = new HashMap<>();
  private final Set<String> failedPreviews = new HashSet<>();
  private final Map<String, ItemStack> cyclingItems = new HashMap<>();
  private String wrappedIntroduction = "", wrappedLanguage = "";
  private List<CommissionBook.ObjectiveView> wrappedObjectives = List.of();
  private List<net.minecraft.util.FormattedCharSequence> wrappedLines = List.of();
  private final List<Button> tabs = new ArrayList<>(), purchases = new ArrayList<>();
  private Button submit, tabsLeft, tabsRight, pageLeft, pageRight;

  public KatheryneScreen(KatheryneMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = 320;
    imageHeight = 210;
  }

  @Override
  protected void init() {
    itemHits.clear();
    super.init();
    tabs.clear();
    purchases.clear();
    for (int i = 0; i < 3; i++) {
      int button = i;
      tabs.add(
          addRenderableWidget(
              Button.builder(
                      Component.empty(),
                      b -> {
                        var names = tabHeaders();
                        int index = tabOffset + button;
                        if (index >= names.size()) return;
                        scroll = 0;
                        selected = "";
                        action(6, 0, names.get(index).id());
                      })
                  .bounds(leftPos + 8 + i * 102, topPos + 24, 98, 20)
                  .build()));
    }
    tabsLeft =
        addRenderableWidget(
            Button.builder(
                    Component.literal("<"),
                    b -> {
                      tabOffset = Math.max(0, tabOffset - 1);
                      updateButtons();
                    })
                .bounds(leftPos + 8, topPos + 24, 18, 20)
                .build());
    tabsRight =
        addRenderableWidget(
            Button.builder(
                    Component.literal(">"),
                    b -> {
                      tabOffset = Math.min(Math.max(0, tabHeaders().size() - 3), tabOffset + 1);
                      updateButtons();
                    })
                .bounds(leftPos + 294, topPos + 24, 18, 20)
                .build());
    submit =
        addRenderableWidget(
            Button.builder(
                    Component.translatable("gui.teyvatdelight.katheryne.submit"),
                    b -> {
                      var q = selectedQuest();
                      if (q != null) {
                        if (q.viewing() && q.viewUnlocked() && !q.ready()) openDetails(q);
                        else action(q.ready() ? 7 : 0, 0, q.id());
                      }
                    })
                .bounds(leftPos + 11, topPos + DETAIL_SUBMIT_Y, 90, 20)
                .build());
    for (int i = 0; i < ROWS; i++) {
      int row = i;
      purchases.add(
          addRenderableWidget(
              Button.builder(
                      Component.translatable("gui.teyvatdelight.katheryne.buy"),
                      b -> {
                        int index = scroll + row;
                        if (tab == 0 && view().quests().get(index).viewing()
                            && view().quests().get(index).viewUnlocked()
                            && !view().quests().get(index).ready()) {
                          openDetails(view().quests().get(index));
                          return;
                        }
                        String key =
                            tab == 0
                                ? view().quests().get(index).id()
                                : storeRows().get(index).id();
                        action(tab == 0 ? view().quests().get(index).ready() ? 7 : 0 : 5, index, key);
                      })
                  .bounds(leftPos + 251, topPos + 51 + i * 24, 57, 19)
                  .build()));
    }
    pageLeft = addRenderableWidget(Button.builder(Component.literal("<"), b -> {
      selected = ""; scroll = 0;
      action(8, Math.max(0, view().offset() - 32), "");
    }).bounds(leftPos + 272, topPos + 191, 18, 16).build());
    pageRight = addRenderableWidget(Button.builder(Component.literal(">"), b -> {
      selected = ""; scroll = 0;
      action(8, view().nextOffset(), "");
    }).bounds(leftPos + 294, topPos + 191, 18, 16).build());
    updateButtons();
  }

  private CommissionBook.Snapshot view() {
    return menu.commissionView();
  }

  private CommissionBook.View selectedQuest() {
    return view().quests().stream().filter(q -> q.id().equals(selected)).findFirst().orElse(null);
  }

  private void action(int kind, int index, String key) {
    if (renderedSnapshot != menu.snapshot()) return;
    KatheryneNetwork.sendAction(menu.containerId, kind, index, menu.snapshot().revision(), key);
  }

  private List<KatheryneSnapshot.StoreHeader> tabHeaders() {
    List<KatheryneSnapshot.StoreHeader> names = new ArrayList<>();
    names.add(new KatheryneSnapshot.StoreHeader("", "gui.teyvatdelight.katheryne.quest"));
    names.addAll(menu.snapshot().stores().headers());
    return names;
  }

  private List<KatheryneSnapshot.StoreRow> storeRows() {
    return menu.snapshot().stores().rows();
  }

  private int listSize() {
    return tab == 0 ? view().quests().size() : storeRows().size();
  }

  private static boolean canSettle(CommissionBook.View q) {
    return q.canAct();
  }

  private void openDetails(CommissionBook.View q) {
    selected = q.id();
    detailScroll = rewardScroll = 0;
    if (q.viewing() && q.viewUnlocked() && !q.done() && !q.ready()) action(9, 0, q.id());
    updateButtons();
  }

  private static String unavailable(CommissionBook.View q) {
    if (q == null) return "gui.teyvatdelight.katheryne.not_ready";
    if (q.done()) return "gui.teyvatdelight.katheryne.completed";
    if (q.objectives().stream().anyMatch(o -> o.type().equals("item") && o.progress() < o.count()))
      return "gui.teyvatdelight.katheryne.missing_materials";
    if (q.viewing() && !q.viewUnlocked()) return "gui.teyvatdelight.katheryne.view_locked";
    return "gui.teyvatdelight.katheryne.not_ready";
  }

  private boolean affordable(List<KatheryneSnapshot.StackAmount> prices) {
    var inventory = minecraft.player.getInventory();
    for (var p : prices) {
      int amount = 0;
      for (int i = 0; i < inventory.getContainerSize(); i++)
        if (inventory.getItem(i).is(p.icon().getItem())) amount += inventory.getItem(i).getCount();
      if (amount < p.count()) return false;
    }
    return true;
  }

  private void updateButtons() {
    tab = menu.snapshot().stores().active().isEmpty() ? 0 : 1;
    if (tab == 0 && !selected.isEmpty() && selectedQuest() == null) selected = "";
    scroll = Math.min(scroll, Math.max(0, listSize() - ROWS));
    var headers = tabHeaders();
    tabOffset = Math.max(0, Math.min(tabOffset, Math.max(0, headers.size() - 3)));
    boolean crowded = headers.size() > 3;
    tabsLeft.visible = tabsRight.visible = crowded;
    tabsLeft.active = tabOffset > 0;
    tabsRight.active = tabOffset + 3 < headers.size();
    long completed = view().completed();
    pageLeft.visible = pageRight.visible = tab == 0 && selected.isEmpty()
        && (view().offset() > 0 || view().nextOffset() >= 0);
    pageLeft.active = view().offset() > 0;
    pageRight.active = view().nextOffset() >= 0;
    var pageTip = Tooltip.create(Component.translatable("gui.teyvatdelight.katheryne.page",
        view().offset() + 1, view().offset() + view().quests().size(), view().total()));
    pageLeft.setTooltip(pageTip);
    pageRight.setTooltip(pageTip);
    for (int i = 0; i < tabs.size(); i++) {
      var button = tabs.get(i);
      int index = tabOffset + i;
      button.visible = index < headers.size();
      if (!button.visible) continue;
      var header = headers.get(index);
      Component label = Component.translatable(header.title());
      if (header.id().isEmpty())
        label = label.copy().append(" (" + completed + "/" + view().total() + ")");
      button.setX(leftPos + (crowded ? 30 + i * 87 : 8 + i * 102));
      button.setWidth(crowded ? 84 : 98);
      button.setMessage(
          Component.literal(font.plainSubstrByWidth(label.getString(), button.getWidth() - 8)));
      button.setTooltip(Tooltip.create(label));
      button.active =
          !header.id().equals(menu.snapshot().stores().active())
              || header.id().isEmpty() && !selected.isEmpty();
    }
    var q = selectedQuest();
    submit.visible = tab == 0 && q != null;
    submit.active = q != null && canSettle(q);
    submit.setMessage(
        Component.translatable(
            q == null ? "gui.teyvatdelight.katheryne.submit" : q.actionKey()));
    submit.setTooltip(
        submit.active ? submissionTooltip(q) : Tooltip.create(Component.translatable(unavailable(q))));
    for (int row = 0; row < purchases.size(); row++) {
      int index = scroll + row;
      Button b = purchases.get(row);
      b.visible = index < listSize() && (tab != 0 || selected.isEmpty());
      if (!b.visible) continue;
      String reason = "";
      if (tab == 0) {
        b.setX(leftPos + 251);
        b.setWidth(57);
        var quest = view().quests().get(index);
        b.active = canSettle(quest);
        b.setMessage(
            Component.translatable(quest.actionKey()));
        if (!b.active) reason = unavailable(quest);
      } else {
        b.setX(leftPos + 263);
        b.setWidth(45);
        b.setMessage(Component.translatable("gui.teyvatdelight.katheryne.buy"));
        boolean sold = storeRows().get(index).remaining() == 0;
        if (sold) reason = "gui.teyvatdelight.katheryne.sold_out";
        else if (!affordable(prices(index))) reason = "gui.teyvatdelight.katheryne.missing_payment";
        b.active = reason.isEmpty();
      }
      b.setTooltip(reason.isEmpty()
          ? tab == 0 ? submissionTooltip(view().quests().get(index)) : null
          : Tooltip.create(Component.translatable(reason)));
    }
  }

  @Override
  public void render(GuiGraphics g, int x, int y, float partial) {
    cursorX = x;
    cursorY = y;
    itemHits.clear();
    updateButtons();
    // In 1.21.1 the container parent draws both the world backdrop and panel.
    super.render(g, x, y, partial);
    if (tab == 0) {
      var current = selectedQuest();
      if (current != null && current.ready() && !current.done()) claimMarker(g, submit);
      if (current == null)
        for (int row = 0; row < purchases.size() && scroll + row < listSize(); row++) {
          var quest = view().quests().get(scroll + row);
          if (quest.ready() && !quest.done()) claimMarker(g, purchases.get(row));
        }
    }
    renderedSnapshot = menu.snapshot();
    renderTooltip(g, x, y);
    for (var balance : balances())
      if (inside(x, y, balance.x(), 5, balance.right(), 22))
        tooltip(g, List.of(balance.icon().getHoverName().copy().append(": " + balance.count())), x, y);
    if (inside(x, y, 83, 5, 127, 22) || inside(x, y, 132, 5, 132 + bonusCapacity() * 24, 23)) {
      List<Component> lines = new ArrayList<>();
      lines.add(Component.translatable("gui.teyvatdelight.katheryne.bonus", view().claimable()));
      if (view().claimable() > 0)
        lines.add(Component.translatable("gui.teyvatdelight.katheryne.click_bonus"));
      for (var r : view().bonus())
        lines.add(Component.literal(r.count() + " x ").append(item(r.item()).getHoverName()));
      tooltip(g, lines, x, y);
    }
    boolean details = tab == 0 && selectedQuest() != null;
    int noticeX = details ? 110 : 11;
    int noticeY = details ? DETAIL_SUBMIT_Y + 2 : 173;
    if (!menu.snapshot().feedback().isEmpty() && inside(x, y, noticeX, noticeY, 309, noticeY + 20))
      tooltip(g, List.of(Component.translatable(menu.snapshot().feedback())), x, y);
    if (tab == 0) {
      var q = selectedQuest();
      if (q == null) {
        for (int row = 0; row < ROWS && scroll + row < listSize(); row++) {
          var quest = view().quests().get(scroll + row);
          if (inside(x, y, 10, 51 + 24 * row, 269, 74 + 24 * row))
            tooltip(g, questLines(quest), x, y);
        }
      } else {
        if (inside(x, y, 10, 51, 309, 83))
          tooltip(
              g,
              List.of(title(q).copy().append(timer(q)), Component.translatable(q.description())),
              x,
              y);
        var goals = q.visibleObjectives();
        for (int row = 0; !hasIntroduction(q) && row < DETAIL_OBJECTIVE_ROWS && detailScroll + row < goals.size(); row++) {
          var o = goals.get(detailScroll + row);
          if (inside(x, y, 11, DETAIL_TEXT_TOP + DETAIL_OBJECTIVE_STEP * row,
              309, DETAIL_TEXT_TOP + DETAIL_OBJECTIVE_STEP * (row + 1)))
            tooltip(g, List.of(objective(o)), x, y);
        }
        for (int i = 0; i < Math.min(12, q.rewards().size() - rewardScroll); i++)
          if (inside(x, y, 11 + 24 * i, DETAIL_REWARDS_Y, 29 + 24 * i, DETAIL_REWARDS_Y + 19))
            g.renderTooltip(font, item(q.rewards().get(rewardScroll + i).item()), x, y);
      }
    } else
      for (int row = 0; row < ROWS && scroll + row < listSize(); row++) {
        int i = scroll + row, ry = 51 + row * 24;
        var output = outputs(i);
        var cost = prices(i);
        if (inside(x, y, 11, ry, 180, ry + 22)
            && !itemTooltip(g, output, x, y, 11, ry + 1, 20))
          stackTooltip(g, output, x, y);
        int costX = 239 - (Math.min(3, cost.size()) - 1) * 27;
        if (inside(x, y, 182, ry, 261, ry + 22)
            && !itemTooltip(g, cost, x, y, costX, ry, 27))
          stackTooltip(g, cost, x, y);
      }
  }

  private List<Component> questLines(CommissionBook.View q) {
    List<Component> lines = new ArrayList<>();
    lines.add(title(q).copy().append(timer(q)));
    if (!q.description().isEmpty()) lines.add(Component.translatable(q.description()));
    q.visibleObjectives().forEach(o -> lines.add(objective(o)));
    return lines;
  }

  private Component title(CommissionBook.View q) {
    Component value =
        q.title().isEmpty()
            ? Component.translatable(
                "gui.teyvatdelight.katheryne.target_title", targetName(q.objectives().get(0)))
            : Component.translatable(q.title());
    if (q.important() && !q.done())
      value = value.copy().withStyle(style -> style.withColor(ChatFormatting.RED).withBold(false));
    return q.done() ? value.copy().withStyle(ChatFormatting.STRIKETHROUGH) : value;
  }

  private Component timer(CommissionBook.View q) {
    if (q.done()) return Component.empty();
    if (q.ready())
      return Component.literal(" ")
          .append(Component.translatable("gui.teyvatdelight.katheryne.claimable"))
          .withStyle(ChatFormatting.ITALIC);
    if (q.viewing() && q.viewUnlocked())
      return Component.literal(" ")
          .append(Component.translatable("gui.teyvatdelight.katheryne.viewable"))
          .withStyle(ChatFormatting.ITALIC);
    if (q.secondsRemaining() < 0)
      return Component.literal(" ")
          .append(Component.translatable("gui.teyvatdelight.katheryne.no_expiry"))
          .withStyle(ChatFormatting.ITALIC);
    int seconds = q.secondsRemaining();
    String time =
        seconds >= 3600
            ? String.format("%d:%02d:%02d", seconds / 3600, seconds / 60 % 60, seconds % 60)
            : String.format("%02d:%02d", seconds / 60, seconds % 60);
    return Component.literal(" " + time).withStyle(ChatFormatting.ITALIC);
  }

  private void drawTitle(
      GuiGraphics g, CommissionBook.View q, int x, int y, int maxWidth, int color) {
    Component suffix = timer(q);
    Component heading = title(q);
    var text = Component.empty();
    font.substrByWidth(heading, Math.max(0, maxWidth - font.width(suffix) - 2))
        .visit((style, part) -> {
          text.append(Component.literal(part).withStyle(style));
          return java.util.Optional.empty();
        }, net.minecraft.network.chat.Style.EMPTY);
    if (q.important() && !q.done())
      suffix = suffix.copy().withStyle(style -> style.withColor(color).withBold(false));
    g.drawString(font, text.append(suffix), x, y, color, false);
  }

  private int bonusCapacity() {
    int left = balances().stream().mapToInt(Balance::x).min().orElse(imageWidth - 9);
    return Math.max(1, (left - 6 - 132) / 24);
  }

  private record Balance(ItemStack icon, int count, int x, int right) {}

  private List<Balance> balances() {
    List<String> configured = view().balanceItems();
    if (configured == null) return List.of();
    List<Balance> result = new ArrayList<>();
    int right = imageWidth - 9;
    for (int i = Math.min(2, configured.size()) - 1; i >= 0; i--) {
      ItemStack icon = item(configured.get(i));
      if (icon.isEmpty()) continue;
      int count = menu.countItem(icon.getItem());
      int left = right - font.width(compact(count)) - 18;
      result.add(new Balance(icon, count, left, right));
      right = left - 8;
    }
    return result;
  }

  private Component targetName(CommissionBook.ObjectiveView o) {
    ResourceLocation id = ResourceLocation.tryParse(o.target());
    if (o.type().equals("item")) {
      if (o.alternatives() == null || o.alternatives().isEmpty()) return item(o.target()).getHoverName();
      if (o.name() != null && !o.name().isEmpty())
        return Component.translatable("gui.teyvatdelight.katheryne.any_of", Component.translatable(o.name()));
      if (o.tag() != null && !o.tag().isEmpty()) {
        String key = "tag.item." + o.tag().replace(':', '.').replace('/', '.');
        Component label = net.minecraft.client.resources.language.I18n.exists(key)
            ? Component.translatable(key)
            : Component.translatable("gui.teyvatdelight.katheryne.tag_items", "#" + o.tag());
        return Component.translatable("gui.teyvatdelight.katheryne.any_of", label);
      }
      Component choices = Component.empty();
      for (String candidate : o.alternatives()) {
        if (!choices.getString().isEmpty()) choices = choices.copy().append(" / ");
        choices = choices.copy().append(item(candidate).getHoverName());
      }
      return Component.translatable("gui.teyvatdelight.katheryne.any_of", choices);
    }
    if (o.type().equals("view")) return Component.translatable("gui.teyvatdelight.katheryne.view_information");
    if (o.type().equals("kill"))
      return BuiltInRegistries.ENTITY_TYPE
          .getOptional(id)
          .map(t -> Component.translatable(t.getDescriptionId()))
          .orElse(Component.literal(o.target()));
    if (o.type().equals("event")) return Component.translatable(o.target());
    return Component.translatable(
        "stat." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
  }

  private Component objective(CommissionBook.ObjectiveView o) {
    if (o.type().equals("item") && o.alternatives() != null && !o.alternatives().isEmpty())
      return Component.translatable("gui.teyvatdelight.katheryne.deliver_any",
          targetName(o), o.count(), o.progress(), o.count());
    String key =
        o.type().equals("item")
            ? "gui.teyvatdelight.katheryne.deliver"
            : o.type().equals("kill")
                ? "gui.teyvatdelight.katheryne.defeat"
                : "gui.teyvatdelight.katheryne.target_progress";
    return Component.translatable(key, targetName(o), o.progress(), o.count());
  }

  private Tooltip submissionTooltip(CommissionBook.View quest) {
    if (quest == null || !quest.pendingSubmission() || quest.payment() == null || quest.payment().isEmpty()) return null;
    Component lines = Component.empty();
    for (var payment : quest.payment()) {
      ItemStack stack = minecraft.player.getInventory().getItem(payment.slot());
      Component name = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(payment.item())
          ? stack.getHoverName() : item(payment.item()).getHoverName();
      if (!lines.getString().isEmpty()) lines = lines.copy().append("\n");
      lines = lines.copy().append(Component.translatable(
          "gui.teyvatdelight.katheryne.submit_preview", name, payment.count()));
      if (stack.isDamageableItem()) {
        for (Component detail : getTooltipFromItem(minecraft, stack).stream().skip(1).limit(6).toList())
          lines = lines.copy().append("\n").append(detail);
        lines = lines.copy().append("\n").append(Component.translatable("item.durability",
            stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
      }
    }
    return Tooltip.create(lines);
  }

  private void claimMarker(GuiGraphics graphics, Button button) {
    if (!button.visible || !button.active) return;
    drawClaimMarker(graphics, button.getX() + button.getWidth() - 3, button.getY() - 2);
  }

  private void drawClaimMarker(GuiGraphics graphics, int x, int y) {
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, 300);
    // Solid pixel strokes are independent of font shadows and resource-pack glyphs.
    graphics.fill(x - 2, y - 2, x + 4, y + 9, 0xFFDF3030);
    graphics.fill(x, y, x + 2, y + 4, 0xFFFFFFFF);
    graphics.fill(x, y + 5, x + 2, y + 7, 0xFFFFFFFF);
    graphics.pose().popPose();
  }

  private void renderIcon(GuiGraphics g, CommissionBook.View q, int x, int y) {
    var o = q.objectives().get(0);
    if (q.icon().isEmpty() && o.type().equals("kill") && !failedPreviews.contains(o.target())) {
      try {
        var entity =
            previews.computeIfAbsent(
                o.target(),
                key -> {
                  var created =
                      BuiltInRegistries.ENTITY_TYPE
                          .get(ResourceLocation.tryParse(key))
                          .create(minecraft.level);
                  return created instanceof net.minecraft.world.entity.LivingEntity living
                      ? living
                      : null;
                });
        if (entity != null) {
          int scale =
              Math.max(1, (int) Math.min(18 / entity.getBbHeight(), 16 / entity.getBbWidth()));
          renderCreature(g, entity, x, y, scale);
          return;
        }
      } catch (RuntimeException error) {
        failedPreviews.add(o.target());
        TeyvatDelight.LOGGER.warn("Cannot render commission target {}", o.target(), error);
      }
    }
    displayItem(g, icon(q), x, y);
  }

  private void renderCreature(
      GuiGraphics g, net.minecraft.world.entity.LivingEntity entity, int x, int y, int scale) {
    var dispatcher = minecraft.getEntityRenderDispatcher();
    var orientation = new org.joml.Quaternionf(dispatcher.cameraOrientation());
    float body = entity.yBodyRot,
        yaw = entity.getYRot(),
        pitch = entity.getXRot(),
        head = entity.yHeadRot,
        oldHead = entity.yHeadRotO;
    g.enableScissor(leftPos + x, topPos + y, leftPos + x + 18, topPos + y + 20);
    g.pose().pushPose();
    try {
      float lookX = (float) Math.atan((leftPos + x + 9 - cursorX) / 40.0);
      float lookY = (float) Math.atan((topPos + y + 10 - cursorY) / 40.0);
      var tilt = new org.joml.Quaternionf().rotateX(lookY * 20 * (float) (Math.PI / 180));
      g.pose().translate(x + 9, y + 10, 50);
      g.pose().scale(scale, scale, -scale);
      g.pose().translate(0, entity.getBbHeight() / 2F, 0);
      g.pose().mulPose(new org.joml.Quaternionf().rotateZ((float) Math.PI));
      g.pose().mulPose(tilt);
      entity.yBodyRot = 180 + lookX * 20;
      entity.setYRot(180 + lookX * 40);
      entity.setXRot(-lookY * 20);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      com.mojang.blaze3d.platform.Lighting.setupForEntityInInventory();
      dispatcher.overrideCameraOrientation(
          new org.joml.Quaternionf().rotateY((float) Math.PI)
              .mul(tilt.conjugate(new org.joml.Quaternionf())));
      dispatcher.setRenderShadow(false);
      com.mojang.blaze3d.systems.RenderSystem.runAsFancy(
          () -> dispatcher.render(entity, 0, 0, 0, 0, 1, g.pose(), g.bufferSource(), 15728880));
      g.flush();
    } finally {
      dispatcher.setRenderShadow(true);
      dispatcher.overrideCameraOrientation(orientation);
      entity.yBodyRot = body;
      entity.setYRot(yaw);
      entity.setXRot(pitch);
      entity.yHeadRot = head;
      entity.yHeadRotO = oldHead;
      g.pose().popPose();
      com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
      g.disableScissor();
    }
  }

  private ItemStack icon(CommissionBook.View q) {
    if (!q.icon().isEmpty()) return item(q.icon());
    String cycling = q.cyclingItem(net.minecraft.Util.getMillis() / 1000);
    if (!cycling.isEmpty()) return cyclingItems.computeIfAbsent(cycling, KatheryneScreen::item);
    var o = q.objectives().get(0);
    if (o.type().equals("item")) return objectiveIcon(o);
    if (o.type().equals("view")) return new ItemStack(Items.WRITABLE_BOOK);
    if (o.type().equals("kill")) {
      var type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(o.target()));
      var egg = SpawnEggItem.byId(type);
      if (egg != null) return new ItemStack(egg);
    }
    return new ItemStack(Items.ELYTRA);
  }

  private ItemStack objectiveIcon(CommissionBook.ObjectiveView objective) {
    var candidates = objective.alternatives();
    String id = candidates == null || candidates.isEmpty() ? objective.target()
        : candidates.get((int) Math.floorMod(net.minecraft.Util.getMillis() / 1000, candidates.size()));
    return cyclingItems.computeIfAbsent(id, KatheryneScreen::item);
  }

  private boolean hasIntroduction(CommissionBook.View quest) {
    return quest.introduction() != null && !quest.introduction().isEmpty();
  }

  private List<net.minecraft.util.FormattedCharSequence> introductionLines(CommissionBook.View quest) {
    String language = minecraft.getLanguageManager().getSelected();
    if (wrappedIntroduction.equals(quest.introduction()) && wrappedLanguage.equals(language)
        && wrappedObjectives.equals(quest.objectives())) return wrappedLines;
    var result = new ArrayList<net.minecraft.util.FormattedCharSequence>();
    if (quest.viewing() && !quest.ready() && !quest.done() && !quest.visibleObjectives().isEmpty()) {
      quest.visibleObjectives().forEach(goal -> result.addAll(font.split(objective(goal), 272)));
      result.add(net.minecraft.util.FormattedCharSequence.EMPTY);
    }
    result.addAll(font.split(Component.translatable(quest.introduction()), 272));
    if (!quest.viewing()) {
      result.add(net.minecraft.util.FormattedCharSequence.EMPTY);
      quest.visibleObjectives().forEach(goal -> result.addAll(font.split(objective(goal), 272)));
    }
    wrappedIntroduction = quest.introduction();
    wrappedLanguage = language;
    wrappedObjectives = quest.objectives();
    wrappedLines = List.copyOf(result);
    return wrappedLines;
  }

  private void renderIntroduction(GuiGraphics graphics, CommissionBook.View quest) {
    var lines = introductionLines(quest);
    detailScroll = Math.max(0, Math.min(detailScroll, Math.max(0, lines.size() - DETAIL_INTRO_ROWS)));
    for (int row = 0; row < DETAIL_INTRO_ROWS && detailScroll + row < lines.size(); row++)
      graphics.drawString(font, lines.get(detailScroll + row), 32, DETAIL_TEXT_TOP + 2 + row * 10, TEXT, false);
    if (lines.size() > DETAIL_INTRO_ROWS) {
      int height = DETAIL_TEXT_BOTTOM - DETAIL_TEXT_TOP;
      int thumb = Math.max(4, height * DETAIL_INTRO_ROWS / lines.size());
      int offset = detailScroll * (height - thumb) / (lines.size() - DETAIL_INTRO_ROWS);
      graphics.fill(307, DETAIL_TEXT_TOP, 310, DETAIL_TEXT_BOTTOM, 0xFF344143);
      graphics.fill(307, DETAIL_TEXT_TOP + offset, 310, DETAIL_TEXT_TOP + offset + thumb, BORDER);
    }
  }

  private void displayItem(GuiGraphics g, ItemStack stack, int x, int y) {
    g.renderItem(stack, x, y);
    itemHits.add(stack, leftPos + x, topPos + y);
  }

  public Optional<KatheryneItemHitMap.Hit> itemUnderMouse(double x, double y) {
    return renderedSnapshot == menu.snapshot() ? itemHits.find(x, y) : Optional.empty();
  }

  private static ItemStack item(String id) {
    ResourceLocation key = ResourceLocation.tryParse(id);
    return key == null ? ItemStack.EMPTY : new ItemStack(BuiltInRegistries.ITEM.get(key));
  }

  @Override
  protected void renderBg(GuiGraphics g, float partial, int x, int y) {
    g.fill(leftPos, topPos, leftPos + imageWidth, topPos + 2, BORDER);
    g.fill(leftPos, topPos + imageHeight - 2, leftPos + imageWidth, topPos + imageHeight, BORDER);
    g.fill(leftPos, topPos + 2, leftPos + 2, topPos + imageHeight - 2, BORDER);
    g.fill(leftPos + imageWidth - 2, topPos + 2, leftPos + imageWidth, topPos + imageHeight - 2, BORDER);
    g.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, BACK);
    g.fill(leftPos + 8, topPos + 47, leftPos + imageWidth - 8, topPos + 48, BORDER);
    if (tab != 0 || selected.isEmpty()) {
      int visibleRows = Math.min(ROWS, listSize() - scroll);
      for (int row = 1; row < visibleRows; row++) {
        int lineY = topPos + 49 + row * 24;
        g.fill(leftPos + 10, lineY, leftPos + 309, lineY + 1, 0xFF344143);
      }
    }
    if (selected.isEmpty() && listSize() > ROWS) {
      int h = 119,
          thumb = Math.max(12, h * ROWS / listSize()),
          offset = scroll * (h - thumb) / (listSize() - ROWS);
      g.fill(leftPos + 311, topPos + 51, leftPos + 317, topPos + 170, 0xFF344143);
      g.fill(
          leftPos + 311, topPos + 51 + offset, leftPos + 317, topPos + 51 + offset + thumb, BORDER);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics g, int x, int y) {
    g.blit(PORTRAIT, 9, 5, 16, 16, 16F, 14F, 32, 32, 64, 64);
    g.drawString(font, font.plainSubstrByWidth(title.getString(), 50), 31, 9, TEXT, false);
    g.fill(83, 12, 127, 16, 0xFF344143);
    int fill =
        view().claimable() > 0
            ? 44
            : (int) Math.min(44, 44L * view().remainder() / Math.max(1, view().every()));
    g.fill(83, 12, 83 + fill, 16, view().claimable() > 0 ? 0xFF9AA945 : 0xFF47796C);
    g.pose().pushPose();
    g.pose().translate(0, 0, 200);
    g.drawCenteredString(
        font,
        Component.literal(
            (view().claimable() > 0 ? compact(view().every()) : compact(view().remainder()))
                + "/"
                + compact(view().every())),
        105,
        9,
        TEXT);
    g.pose().popPose();
    int capacity = bonusCapacity();
    bonusScroll = Math.max(0, Math.min(bonusScroll, Math.max(0, view().bonus().size() - capacity)));
    for (int i = 0; i < Math.min(capacity, view().bonus().size() - bonusScroll); i++) {
      var reward = view().bonus().get(bonusScroll + i);
      ItemStack stack = item(reward.item());
      displayItem(g, stack, 132 + i * 24, 5);
      g.renderItemDecorations(font, stack, 132 + i * 24, 5, compact(reward.count()));
      if (view().claimable() > 0) {
        drawClaimMarker(g, 143 + i * 24, 3);
      }
    }
    if (view().bonus().size() > capacity) {
      int track = capacity * 24 - 8;
      int thumb = Math.max(4, track * capacity / view().bonus().size());
      int offset = bonusScroll * (track - thumb) / (view().bonus().size() - capacity);
      g.fill(132, 22, 132 + track, 23, 0xFF344143);
      g.fill(132 + offset, 22, 132 + offset + thumb, 23, BORDER);
    }
    for (var balance : balances()) {
      displayItem(g, balance.icon(), balance.x(), 5);
      g.drawString(font, compact(balance.count()), balance.x() + 18, 9, TEXT, false);
    }
    if (tab == 0) {
      var q = selectedQuest();
      if (q == null)
        for (int row = 0; row < ROWS && scroll + row < listSize(); row++) {
          var quest = view().quests().get(scroll + row);
          int yy = 51 + row * 24;
          renderIcon(g, quest, 11, yy + 1);
          var first = quest.objectives().get(0);
          String progress = first.progress() + "/" + first.count();
          drawTitle(g, quest, 32, yy, 206 - font.width(progress), quest.done() ? 0xFF82948C : TEXT);
          g.drawString(
              font,
              progress,
              244 - font.width(progress),
              yy,
              quest.ready() ? 0xFF90C79D : TEXT,
              false);
          String description =
              quest.description().isEmpty()
                  ? objective(quest.objectives().get(0)).getString()
                  : Component.translatable(quest.description()).getString();
          g.drawString(font, ellipsize(description, 210, false), 32, yy + 11, 0xFFADC2BA, false);
        }
      else {
        renderIcon(g, q, 11, 51);
        drawTitle(g, q, 32, 53, 276, TEXT);
        var lines = font.split(Component.translatable(q.description()), 276);
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
          if (i == 1 && lines.size() > 2) {
            StringBuilder lastLine = new StringBuilder();
            lines
                .get(i)
                .accept(
                    (index, style, codePoint) -> {
                      lastLine.appendCodePoint(codePoint);
                      return true;
                    });
            g.drawString(
                font,
                ellipsize(lastLine.toString(), 276, true),
                32,
                66 + i * 10,
                0xFFADC2BA,
                false);
          } else g.drawString(font, lines.get(i), 32, 66 + i * 10, 0xFFADC2BA, false);
        }
        if (hasIntroduction(q)) renderIntroduction(g, q);
        else detailScroll = Math.min(detailScroll, Math.max(0, q.visibleObjectives().size() - DETAIL_OBJECTIVE_ROWS));
        var goals = q.visibleObjectives();
        for (int row = 0; !hasIntroduction(q) && row < DETAIL_OBJECTIVE_ROWS && detailScroll + row < goals.size(); row++) {
          var o = goals.get(detailScroll + row);
          int yy = DETAIL_TEXT_TOP + 2 + row * DETAIL_OBJECTIVE_STEP;
          if (o.type().equals("item")) displayItem(g, objectiveIcon(o), 11, yy);
          g.drawString(
              font,
              font.plainSubstrByWidth(objective(o).getString(), 274),
              32,
              yy + 3,
              o.progress() >= o.count() ? 0xFF90C79D : TEXT,
              false);
        }
        g.drawString(
            font,
            Component.translatable("gui.teyvatdelight.katheryne.reward_label"),
            11,
            DETAIL_REWARD_LABEL_Y,
            TEXT,
            false);
        rewardScroll = Math.min(rewardScroll, Math.max(0, q.rewards().size() - 12));
        for (int i = 0; i < Math.min(12, q.rewards().size() - rewardScroll); i++) {
          var r = q.rewards().get(rewardScroll + i);
          ItemStack stack = item(r.item());
          displayItem(g, stack, 11 + i * 24, DETAIL_REWARDS_Y);
          g.renderItemDecorations(font, stack, 11 + i * 24, DETAIL_REWARDS_Y, compact(r.count()));
        }
      }
      if (q == null)
        g.drawString(
            font,
            Component.translatable("gui.teyvatdelight.katheryne.limit", view().limit()),
            11,
            196,
            0xFFADC2BA,
            false);
    } else
      for (int row = 0; row < ROWS && scroll + row < listSize(); row++) {
        int i = scroll + row, yy = 51 + row * 24;
        var output = outputs(i);
        var cost = prices(i);
        for (int n = 0; n < Math.min(3, output.size()); n++) {
          var a = output.get(n);
          displayItem(g, a.icon(), 11 + n * 20, yy + 1);
          if (a.count() > 1)
            g.renderItemDecorations(font, a.icon(), 11 + n * 20, yy + 1, compact(a.count()));
        }
        int tx = output.size() == 1 ? 31 : output.size() == 2 ? 55 : 75;
        String name = storeRows().get(i).name();
        Component label =
            name.isEmpty() ? output.get(0).icon().getHoverName() : Component.translatable(name);
        g.drawString(
            font, font.plainSubstrByWidth(label.getString(), 177 - tx), tx, yy, TEXT, false);
        int remaining = storeRows().get(i).remaining();
        Component stock =
            Component.translatable(
                remaining < 0
                    ? "gui.teyvatdelight.katheryne.unlimited"
                    : remaining == 0
                        ? "gui.teyvatdelight.katheryne.sold_out"
                        : "gui.teyvatdelight.katheryne.remaining",
                remaining);
        g.drawString(font, stock, tx, yy + 11, 0xFFADC2BA, false);
        int visibleCosts = Math.min(3, cost.size());
        int costX = 239 - (visibleCosts - 1) * 27;
        for (int n = 0; n < visibleCosts; n++) {
          var a = cost.get(n);
          displayItem(g, a.icon(), costX + n * 27, yy);
          g.renderItemDecorations(font, a.icon(), costX + n * 27, yy, compact(a.count()));
        }
      }
    boolean details = tab == 0 && selectedQuest() != null;
    int seconds = tab == 0 ? menu.getSecondsUntilRefresh() : menu.snapshot().stores().seconds();
    Component refresh =
        tab != 0 && seconds < 0
            ? Component.translatable("gui.teyvatdelight.katheryne.no_refresh")
            : tab == 0 && menu.snapshot().immediateRefresh()
                ? Component.translatable("gui.teyvatdelight.katheryne.refresh_after_completion")
                : Component.translatable(
                    "gui.teyvatdelight.katheryne.refresh",
                    String.format("%02d:%02d", seconds / 60, seconds % 60));
    if (details) refresh = Component.literal(ellipsize(refresh.getString(), 180, false));
    g.drawString(
        font, refresh, tab == 0 ? imageWidth - 11 - font.width(refresh) : 11,
        details ? DETAIL_REWARD_LABEL_Y : 196, TEXT, false);
    if (!menu.snapshot().feedback().isEmpty()) {
      int noticeX = details ? 110 : 11;
      int noticeY = details ? DETAIL_SUBMIT_Y + 2 : 173;
      var lines = font.split(Component.translatable(menu.snapshot().feedback()), 309 - noticeX);
      for (int i = 0; i < Math.min(2, lines.size()); i++)
        g.drawString(font, lines.get(i), noticeX, noticeY + i * 10, 0xFFFFB99E, false);
    }
  }

  private String ellipsize(String text, int maxWidth, boolean hasMoreLines) {
    if (!hasMoreLines && font.width(text) <= maxWidth) return text;
    return font.plainSubstrByWidth(text, Math.max(0, maxWidth - font.width("..."))) + "...";
  }

  private List<KatheryneSnapshot.StackAmount> outputs(int i) {
    return storeRows().get(i).outputs();
  }

  private List<KatheryneSnapshot.StackAmount> prices(int i) {
    return storeRows().get(i).prices();
  }

  private void stackTooltip(
      GuiGraphics g, List<KatheryneSnapshot.StackAmount> values, int x, int y) {
    tooltip(
        g,
        values.stream()
            .map(s -> Component.literal(s.count() + " x ").append(s.icon().getHoverName()))
            .map(c -> (Component) c)
            .toList(),
        x,
        y);
  }

  private boolean itemTooltip(
      GuiGraphics g, List<KatheryneSnapshot.StackAmount> values, int x, int y,
      int startX, int rowY, int spacing) {
    for (int i = 0; i < Math.min(3, values.size()); i++) {
      int iconX = startX + i * spacing;
      if (inside(x, y, iconX, rowY, iconX + 16, rowY + 16)) {
        g.renderTooltip(font, values.get(i).icon(), x, y);
        return true;
      }
    }
    return false;
  }

  private void tooltip(GuiGraphics g, List<Component> lines, int x, int y) {
    g.renderTooltip(
        font,
        lines.stream()
            .flatMap(line -> font.split(line, Math.min(260, width - 24)).stream())
            .toList(),
        x,
        y);
  }

  private boolean inside(int x, int y, int x1, int y1, int x2, int y2) {
    return x >= leftPos + x1 && x < leftPos + x2 && y >= topPos + y1 && y < topPos + y2;
  }

  @Override
  public boolean mouseClicked(double x, double y, int button) {
    itemHits.clear();
    if (button == 0
        && selected.isEmpty()
        && listSize() > ROWS
        && inside((int) x, (int) y, 311, 51, 317, 170)) {
      draggingScroll = true;
      setScroll(y);
      return true;
    }
    if (button == 0 && view().claimable() > 0)
      for (int i = 0; i < Math.min(bonusCapacity(), view().bonus().size() - bonusScroll); i++)
        if (inside((int) x, (int) y, 132 + i * 24, 3, 150 + i * 24, 22)) {
          action(3, 0, "");
          return true;
        }
    if (button == 0
        && tab == 0
        && selected.isEmpty()
        && inside((int) x, (int) y, 10, 51, 269, 171)) {
      int i = scroll + ((int) y - topPos - 51) / 24;
      if (i < view().quests().size()) {
        openDetails(view().quests().get(i));
        return true;
      }
    }
    return super.mouseClicked(x, y, button);
  }

  @Override
  public boolean mouseScrolled(double x, double y, double sx, double sy) {
    itemHits.clear();
    if (inside((int) x, (int) y, 132, 5, 132 + bonusCapacity() * 24, 23)) {
      bonusScroll =
          Math.max(
              0,
              Math.min(
                  Math.max(0, view().bonus().size() - bonusCapacity()),
                  bonusScroll - (int) Math.signum(sy)));
      return true;
    }
    if (tab == 0 && !selected.isEmpty()) {
      var q = selectedQuest();
      if (q != null && inside((int) x, (int) y, 8, DETAIL_TEXT_TOP - 1, 310, DETAIL_TEXT_BOTTOM))
        detailScroll =
              Math.max(0, Math.min(hasIntroduction(q) ? Math.max(0, introductionLines(q).size() - DETAIL_INTRO_ROWS)
                  : Math.max(0, q.visibleObjectives().size() - DETAIL_OBJECTIVE_ROWS), detailScroll - (int) Math.signum(sy)));
      else if (q != null)
        rewardScroll =
            Math.max(0, Math.min(q.rewards().size() - 12, rewardScroll - (int) Math.signum(sy)));
      return true;
    }
    scroll = Math.max(0, Math.min(Math.max(0, listSize() - ROWS), scroll - (int) Math.signum(sy)));
    return true;
  }

  private void setScroll(double y) {
    itemHits.clear();
    double ratio = Math.max(0, Math.min(1, (y - topPos - 51) / 119));
    scroll = (int) Math.round(ratio * Math.max(0, listSize() - ROWS));
  }

  @Override
  public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
    if (draggingScroll && button == 0) {
      setScroll(y);
      return true;
    }
    return super.mouseDragged(x, y, button, dx, dy);
  }

  @Override
  public boolean mouseReleased(double x, double y, int button) {
    if (button == 0) draggingScroll = false;
    return super.mouseReleased(x, y, button);
  }

  private static String compact(int n) {
    return n >= 1000000 ? n / 1000000 + "m" : n >= 1000 ? n / 1000 + "k" : "" + n;
  }
}
