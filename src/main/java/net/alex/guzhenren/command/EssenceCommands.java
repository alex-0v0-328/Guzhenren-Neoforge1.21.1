package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.gameplay.action.PlayerEssenceActions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class EssenceCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> buildEssence() {
        return Commands.literal("essence")
                .then(Commands.literal("add").then(Commands.argument("amount", FloatArgumentType.floatArg(0.01f))
                        .executes(ctx -> cmdAddEssence(ctx.getSource(), FloatArgumentType.getFloat(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", FloatArgumentType.floatArg(0.01f))
                        .executes(ctx -> cmdSubEssence(ctx.getSource(), FloatArgumentType.getFloat(ctx, "amount")))))
                .then(Commands.literal("refill").executes(ctx -> cmdRefillEssence(ctx.getSource())));
    }

    private static int cmdAddEssence(CommandSourceStack src, float amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerEssenceActions.addEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_added", amount), false);
        return 1;
    }

    private static int cmdSubEssence(CommandSourceStack src, float amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerEssenceActions.subEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_subbed", amount), false);
        return 1;
    }

    private static int cmdRefillEssence(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerEssenceActions.refillEssence(player);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_refilled"), false);
        return 1;
    }
}
