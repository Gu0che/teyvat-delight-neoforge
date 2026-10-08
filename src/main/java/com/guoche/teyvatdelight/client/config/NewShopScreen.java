package com.guoche.teyvatdelight.client.config;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

final class NewShopScreen extends Screen {
  private final Screen parent;
  private final List<String> existing;
  private final Consumer<String> create;
  private EditBox id;
  private Button add;
  NewShopScreen(Screen parent, List<String> existing, Consumer<String> create) {
    super(KatheryneEditorScreens.text("new_shop"));
    this.parent = parent; this.existing = existing; this.create = create;
  }
  @Override protected void init() {
    String value = id == null ? "teyvatdelight:custom_shop" : id.getValue();
    int span = Math.min(360, width - 30), x = (width - span) / 2;
    id = addRenderableWidget(new EditBox(font, x, height / 2 - 20, span, 20,
        KatheryneEditorScreens.text("resource")));
    id.setMaxLength(128); id.setValue(value);
    add = addRenderableWidget(Button.builder(KatheryneEditorScreens.text("new_shop"),
        ignored -> create.accept(id.getValue())).bounds(x + span / 2 + 3,
        height / 2 + 25, span / 2 - 3, 20).build());
    addRenderableWidget(Button.builder(KatheryneEditorScreens.text("cancel"),
        ignored -> onClose()).bounds(x, height / 2 + 25, span / 2 - 3, 20).build());
    id.setResponder(v -> validate()); validate();
    setInitialFocus(id);
  }
  private void validate() {
    ResourceLocation parsed = ResourceLocation.tryParse(id.getValue());
    add.active = com.guoche.teyvatdelight.entity.katheryne.KatheryneConfigEditor
        .validShopId(id.getValue())
        && !existing.contains(parsed.toString());
  }
  @Override public void onClose() { minecraft.setScreen(parent); }
  @Override public void render(GuiGraphics g, int x, int y, float delta) {
    renderBackground(g, x, y, delta);
    super.render(g, x, y, delta);
    g.drawCenteredString(font, title, width / 2, height / 2 - 50, 0xFFFFFF);
    g.drawCenteredString(font, KatheryneEditorScreens.text("resource"),
        width / 2, height / 2 - 34, 0xBBBBBB);
    if (!add.active) g.drawCenteredString(font, KatheryneEditorScreens.text("invalid_shop"),
        width / 2, height / 2 + 7, 0xFF8888);
  }
}
