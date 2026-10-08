package com.guoche.teyvatdelight.client.katheryne;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

/** Item hit areas for the current GUI frame, without any optional integration types. */
public final class KatheryneItemHitMap {
  private final List<Hit> items = new ArrayList<>();

  public record Hit(ItemStack stack, int x, int y) {}

  public void clear() {
    items.clear();
  }

  public void add(ItemStack stack, int x, int y) {
    if (!stack.isEmpty()) items.add(new Hit(stack.copy(), x, y));
  }

  public Optional<Hit> find(double mouseX, double mouseY) {
    for (int i = items.size() - 1; i >= 0; i--) {
      Hit hit = items.get(i);
      if (mouseX >= hit.x() && mouseX < hit.x() + 16
          && mouseY >= hit.y() && mouseY < hit.y() + 16)
        return Optional.of(new Hit(hit.stack().copy(), hit.x(), hit.y()));
    }
    return Optional.empty();
  }
}
