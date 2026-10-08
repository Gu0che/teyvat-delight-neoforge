package com.guoche.teyvatdelight.entity.katheryne;

import com.guoche.teyvatdelight.api.KatheryneApi;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

/** Datapack functions may report named events without depending on a Java integration. */
final class KatheryneEventCommand {
  private KatheryneEventCommand() {}

  static LiteralArgumentBuilder<CommandSourceStack> command() {
    return Commands.literal("event").requires(s -> s.hasPermission(2))
        .then(Commands.argument("players", EntityArgument.players())
            .then(Commands.argument("event", ResourceLocationArgument.id())
                .executes(c -> report(c, 1))
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(c -> report(c, IntegerArgumentType.getInteger(c, "amount"))))));
  }

  private static int report(CommandContext<CommandSourceStack> c, int amount)
      throws CommandSyntaxException {
    if (!CommissionConfig.runtimeValid) {
      c.getSource().sendFailure(Component.translatable("gui.teyvatdelight.katheryne.invalid_rules"));
      return 0;
    }
    var players = EntityArgument.getPlayers(c, "players");
    String event = ResourceLocationArgument.getId(c, "event").toString();
    for (var player : players) {
      KatheryneApi.progress(player, event, amount);
      if (player.containerMenu instanceof KatheryneMenu menu) menu.sendCommissionUpdate();
    }
    c.getSource().sendSuccess(() -> Component.translatable(
        "commands.teyvatdelight.katheryne.event", event, amount, players.size()), false);
    return players.size();
  }
}
