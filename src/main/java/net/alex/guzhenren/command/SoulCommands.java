package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 魂魄命令: add / sub / reset. 不 require 开窍 (凡人也有魂魄) */
public final class SoulCommands {

    private SoulCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> buildSoul() {
        return Commands.literal("soul")
                .then(Commands.literal("add").then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(ctx -> cmdAdd(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(ctx -> cmdSub(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                .then(Commands.literal("reset").executes(ctx -> cmdReset(ctx.getSource())));
    }

    private static int cmdAdd(CommandSourceStack src, long amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).soul().addSoul(amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.soul_added", amount), false);
        return 1;
    }

    private static int cmdSub(CommandSourceStack src, long amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).soul().subSoul(amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.soul_subbed", amount), false);
        return 1;
    }

    private static int cmdReset(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).soul().reset();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.soul_reset"), false);
        return 1;
    }
}
