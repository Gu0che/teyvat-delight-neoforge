package com.guoche.teyvatdelight.integration.jei;

import com.guoche.teyvatdelight.client.katheryne.KatheryneScreen;
import java.util.Optional;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;

final class KatheryneGuiHandler implements IGuiContainerHandler<KatheryneScreen> {
  @Override
  public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
      IClickableIngredientFactory factory, KatheryneScreen screen, double mouseX, double mouseY) {
    return screen.itemUnderMouse(mouseX, mouseY)
        .flatMap(hit -> factory.createBuilder(hit.stack()).buildWithArea(hit.x(), hit.y(), 16, 16));
  }
}
