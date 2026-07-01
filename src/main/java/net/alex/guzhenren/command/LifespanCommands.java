package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 寿元命令: add / sub / setAge / reset. 不 require 开窍 (凡人也有寿元) */
public final class LifespanCommands {

    private LifespanCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> buildLifespan() {
        return Commands.literal("lifespan")
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> cmdAddMax(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> cmdSubMax(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))))
                .then(Commands.literal("setAge").then(Commands.argument("age", IntegerArgumentType.integer(0))
                        .executes(ctx -> cmdSetAge(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "age")))))
                .then(Commands.literal("reset").executes(ctx -> cmdReset(ctx.getSource())));
    }

    private static int cmdAddMax(CommandSourceStack src, int amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).lifespan().addMaxLifespan(amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.lifespan_added", amount), false);
        return 1;
    }

    private static int cmdSubMax(CommandSourceStack src, int amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).lifespan().subMaxLifespan(amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.lifespan_subbed", amount), false);
        return 1;
    }

    private static int cmdSetAge(CommandSourceStack src, int age) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).lifespan().setAge(age);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.age_set", age), false);
        return 1;
    }

    private static int cmdReset(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData.of(player).lifespan().reset();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.lifespan_reset"), false);
        return 1;
    }
}
