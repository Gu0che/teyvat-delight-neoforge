package com.guoche.teyvatdelight.entity.katheryne;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

/** Operator-only refresh; uses the existing per-player state and settlement channels. */
final class KatheryneRefreshCommand {
  private KatheryneRefreshCommand() {}

  static LiteralArgumentBuilder<CommandSourceStack> command() {
    var command = branch("refresh", true, true).requires(source -> source.hasPermission(2));
    return command.then(branch("all", true, true))
        .then(branch("commissions", true, false))
        .then(branch("shops", false, true))
        .then(Commands.literal("shop")
            .then(Commands.argument("shop", ResourceLocationArgument.id())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                    ShopDefinitions.shops().stream().map(ShopDefinitions.Shop::id), builder))
                .executes(context -> refreshShop(context, false))
                .then(Commands.argument("players", EntityArgument.players())
                    .executes(context -> refreshShop(context, true)))));
  }

  private static LiteralArgumentBuilder<CommandSourceStack> branch(
      String name, boolean commissions, boolean shops) {
    return Commands.literal(name)
        .executes(context -> refresh(context, commissions, shops, false))
        .then(Commands.argument("players", EntityArgument.players())
            .executes(context -> refresh(context, commissions, shops, true)));
  }

  private static int refresh(CommandContext<CommandSourceStack> context,
      boolean commissions, boolean shops, boolean selected) throws CommandSyntaxException {
    return refresh(context, commissions, shops, selected, null);
  }

  private static int refreshShop(CommandContext<CommandSourceStack> context, boolean selected)
      throws CommandSyntaxException {
    var id = ResourceLocationArgument.getId(context, "shop");
    var shop = ShopDefinitions.get(id.toString());
    if (shop == null && id.getNamespace().equals("minecraft")) shop = ShopDefinitions.get(id.getPath());
    if (shop == null) {
      context.getSource().sendFailure(
          Component.translatable("commands.teyvatdelight.katheryne.unknown_shop", id.toString()));
      return 0;
    }
    return refresh(context, false, true, selected, shop);
  }

  private static int refresh(CommandContext<CommandSourceStack> context,
      boolean commissions, boolean shops, boolean selected, ShopDefinitions.Shop singleShop)
      throws CommandSyntaxException {
    var source = context.getSource();
    var players = selected ? EntityArgument.getPlayers(context, "players")
        : List.of(source.getPlayerOrException());
    if (!CommissionConfig.runtimeValid) {
      source.sendFailure(Component.translatable("gui.teyvatdelight.katheryne.invalid_rules"));
      return 0;
    }
    var data = KatheryneData.get(source.getServer());
    int generated = 0;
    for (var player : players) {
      if (commissions) generated += data.commissions.refresh(player, data.commissionState(player));
      if (shops) {
        if (singleShop == null) data.stores.refresh(player);
        else data.stores.refresh(player, singleShop);
      }
      if (player.containerMenu instanceof KatheryneMenu menu) menu.sendSnapshot();
    }
    String section = commissions && shops ? "all" : commissions ? "commissions" : "shops";
    int issued = generated;
    source.sendSuccess(() -> {
      if (singleShop != null)
        return Component.translatable("commands.teyvatdelight.katheryne.refreshed.shop",
            singleShop.id(), players.size());
      String key = "commands.teyvatdelight.katheryne.refreshed." + section;
      return commissions ? Component.translatable(key, players.size(), issued)
          : Component.translatable(key, players.size());
    }, true);
    return players.size();
  }
}
