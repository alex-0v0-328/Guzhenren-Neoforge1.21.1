package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 真元命令: add / sub / refill. Phase 2 后 essence 值为 long, 直接操作 EssenceComponent */
public final class EssenceCommands {

    private EssenceCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> buildEssence() {
        return Commands.literal("essence")
                .then(Commands.literal("add").then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(ctx -> cmdAddEssence(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(ctx -> cmdSubEssence(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                .then(Commands.literal("refill").executes(ctx -> cmdRefillEssence(ctx.getSource())));
    }

    private static int cmdAddEssence(CommandSourceStack src, long amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        ModPlayerData.of(player).essence().addCurrent(amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_added", amount), false);
        return 1;
    }

    private static int cmdSubEssence(CommandSourceStack src, long amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        ModPlayerData.of(player).essence().subCurrent(amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_subbed", amount), false);
        return 1;
    }

    private static int cmdRefillEssence(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        ModPlayerData.of(player).essence().refillCurrentEssence();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_refilled"), false);
        return 1;
    }
}
