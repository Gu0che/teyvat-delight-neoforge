package com.guoche.teyvatdelight.client.config;

import java.util.List;
import java.util.function.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Paged definition lists; callbacks edit detached drafts, never server player records. */
class EditorListScreen extends Screen {
  record Row(Component label, List<ItemStack> icons, Runnable edit,
      Runnable remove, Runnable up, Runnable down) {}
  record Action(Component label, Runnable run) {}
  private final Screen parent;
  private final Supplier<List<Row>> rows;
  private final Supplier<List<Action>> actions;
  private final Runnable apply;
  private final BooleanSupplier dirty;
  private int page, pages, pageSize, left, span;
  private List<Row> visible = List.of();
  private Component status = Component.empty();
  private Supplier<Component> statusSupplier;
  private boolean busy;
  private boolean persistent;

  EditorListScreen(Screen parent, Component title, Supplier<List<Row>> rows,
      Supplier<List<Action>> actions, Runnable apply, BooleanSupplier dirty) {
    super(title);
    this.parent = parent;
    this.rows = rows;
    this.actions = actions;
    this.apply = apply;
    this.dirty = dirty;
  }

  void status(Component text) { status = text; statusSupplier = null; }
  void status(Supplier<Component> supplier) { statusSupplier = supplier; }
  void busy(boolean value) { busy = value; rebuild(); }
  void persistent() { persistent = true; }
  void rebuild() { if (minecraft != null) rebuildWidgets(); }

  @Override protected void init() {
    left = Math.max(12, (width - 650) / 2);
    span = width - left * 2;
    pageSize = Math.max(1, (height - 155) / 34);
    List<Row> all = rows.get();
    pages = Math.max(1, (all.size() + pageSize - 1) / pageSize);
    page = Math.min(page, pages - 1);
    List<Action> options = actions.get();
    int actionWidth = (span - Math.max(0, options.size() - 1) * 6)
        / Math.max(1, options.size());
    for (int i = 0; i < options.size(); i++) {
      Action option = options.get(i);
      button(option.label(), left + i * (actionWidth + 6), 35, actionWidth, option.run());
    }
    visible = all.subList(page * pageSize, Math.min(all.size(), (page + 1) * pageSize));
    for (int i = 0; i < visible.size(); i++) {
      Row row = visible.get(i);
      int y = 65 + i * 34, end = left + span;
      button(Component.empty(), left, y, span - 72, row.edit());
      button(Component.literal("x"), end - 66, y, 20, row.remove())
          .setTooltip(Tooltip.create(KatheryneEditorScreens.text("delete")));
      button(Component.literal("^"), end - 44, y, 20, row.up())
          .setTooltip(Tooltip.create(KatheryneEditorScreens.text("up")));
      button(Component.literal("v"), end - 22, y, 20, row.down())
          .setTooltip(Tooltip.create(KatheryneEditorScreens.text("down")));
    }
    button(Component.literal("<"), left, height - 84, 24, () -> { page--; rebuild(); })
        .active = !busy && page > 0;
    button(Component.literal(">"), left + span - 24, height - 84, 24,
        () -> { page++; rebuild(); }).active = !busy && page < pages - 1;
    button(KatheryneEditorScreens.text("cancel"), left, height - 27,
        (span - 6) / 2, this::onClose);
    button(KatheryneEditorScreens.text(apply == null ? "done" : persistent ? "save" : "apply"),
        left + (span + 6) / 2, height - 27, (span - 6) / 2,
        apply == null ? () -> minecraft.setScreen(parent) : apply);
  }

  private Button button(Component label, int x, int y, int w, Runnable action) {
    Button button = addRenderableWidget(Button.builder(label, ignored -> {
      if (action != null) action.run();
    }).bounds(x, y, w, 20).build());
    button.active = !busy && action != null;
    return button;
  }

  @Override public void onClose() {
    if (busy) return;
    if (!dirty.getAsBoolean()) { minecraft.setScreen(parent); return; }
    minecraft.setScreen(new ConfirmScreen(yes -> minecraft.setScreen(yes ? parent : this),
        KatheryneEditorScreens.text("discard"),
        KatheryneEditorScreens.text("discard_detail")));
  }

  @Override public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
    if (busy || vertical == 0) return false;
    int old = page;
    page = Math.max(0, page + (vertical < 0 ? 1 : -1));
    rebuild();
    return old != page;
  }

  @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(graphics, mouseX, mouseY, partialTick);
    super.render(graphics, mouseX, mouseY, partialTick);
    String heading = title.getString();
    if (font.width(heading) > width - 24)
      heading = font.plainSubstrByWidth(heading, width - 24 - font.width("...")) + "...";
    graphics.drawCenteredString(font, heading, width / 2, 15, 0xFFFFFF);
    for (int i = 0; i < visible.size(); i++) {
      Row row = visible.get(i);
      int y = 65 + i * 34, x = left + 6;
      int icons = Math.min(3, row.icons().size());
      for (int n = 0; n < icons; n++) {
        ItemStack stack = row.icons().get(n);
        graphics.renderItem(stack, x, y + 2);
        graphics.renderItemDecorations(font, stack, x, y + 2);
        x += 20;
      }
      String label = row.label().getString();
      int room = Math.max(10, left + span - 78 - x);
      if (font.width(label) > room) label = font.plainSubstrByWidth(label,
          Math.max(0, room - font.width("..."))) + "...";
      graphics.drawString(font, label, x, y + 6, 0xFFFFFF, false);
      graphics.fill(left, y + 28, left + span, y + 29, 0x55888888);
    }
    graphics.drawCenteredString(font, (page + 1) + " / " + pages,
        width / 2, height - 78, 0xBBBBBB);
    int y = height - 56;
    for (var line : font.split(statusSupplier == null ? status : statusSupplier.get(), span)) {
      if (y >= height - 29) break;
      graphics.drawString(font, line, left, y, 0xFFDD88, false);
      y += 10;
    }
    for (int i = 0; i < visible.size(); i++)
      for (int n = 0; n < Math.min(3, visible.get(i).icons().size()); n++) {
        int x = left + 6 + n * 20, top = 67 + i * 34;
        if (mouseX >= x && mouseX < x + 16 && mouseY >= top && mouseY < top + 16)
          graphics.renderTooltip(font, visible.get(i).icons().get(n), mouseX, mouseY);
      }
  }
}
