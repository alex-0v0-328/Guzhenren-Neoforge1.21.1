package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.action.PlayerPathActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;

/** 流派命令: add / sub / set / up / down. 所有子命令 requireAwakened */
public final class PathCommands {

    private PathCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> buildPath() {
        return Commands.literal("path")
                .then(Commands.literal("add")
                        .then(Commands.argument("path", EnumArgument.enumArgument(Path.class))
                                .then(Commands.argument("marks", LongArgumentType.longArg(1))
                                        .executes(ctx -> cmdAddMarks(ctx.getSource(),
                                                ctx.getArgument("path", Path.class),
                                                LongArgumentType.getLong(ctx, "marks"))))))
                .then(Commands.literal("sub")
                        .then(Commands.argument("path", EnumArgument.enumArgument(Path.class))
                                .then(Commands.argument("marks", LongArgumentType.longArg(1))
                                        .executes(ctx -> cmdSubMarks(ctx.getSource(),
                                                ctx.getArgument("path", Path.class),
                                                LongArgumentType.getLong(ctx, "marks"))))))
                .then(Commands.literal("set")
                        .then(Commands.argument("path", EnumArgument.enumArgument(Path.class))
                                .then(Commands.argument("attainment", EnumArgument.enumArgument(Attainment.class))
                                        .executes(ctx -> cmdSetAttainment(ctx.getSource(),
                                                ctx.getArgument("path", Path.class),
                                                ctx.getArgument("attainment", Attainment.class))))))
                .then(Commands.literal("up")
                        .then(Commands.argument("path", EnumArgument.enumArgument(Path.class))
                                .executes(ctx -> cmdAttainmentUp(ctx.getSource(),
                                        ctx.getArgument("path", Path.class)))))
                .then(Commands.literal("down")
                        .then(Commands.argument("path", EnumArgument.enumArgument(Path.class))
                                .executes(ctx -> cmdAttainmentDown(ctx.getSource(),
                                        ctx.getArgument("path", Path.class)))));
    }

    private static int cmdAddMarks(CommandSourceStack src, Path path, long marks) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerPathActions.addMarks(player, path, marks);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_marks_added",
                Component.translatable(path.getTranslationKey()), marks), false);
        return 1;
    }

    private static int cmdSubMarks(CommandSourceStack src, Path path, long marks) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerPathActions.subMarks(player, path, marks);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_marks_subbed",
                Component.translatable(path.getTranslationKey()), marks), false);
        return 1;
    }

    private static int cmdSetAttainment(CommandSourceStack src, Path path, Attainment attainment) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerPathActions.setAttainment(player, path, attainment);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_attainment_set",
                Component.translatable(path.getTranslationKey()),
                Component.translatable(attainment.getTranslationKey())), false);
        return 1;
    }

    private static int cmdAttainmentUp(CommandSourceStack src, Path path) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (!PlayerPathActions.attainmentUp(player, path)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.path_attainment_up_failed"));
            return 0;
        }
        Attainment now = ModPlayerData.of(player).path().getAttainment(path);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_attainment_up",
                Component.translatable(path.getTranslationKey()),
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }

    private static int cmdAttainmentDown(CommandSourceStack src, Path path) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (!PlayerPathActions.attainmentDown(player, path)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.path_attainment_down_failed"));
            return 0;
        }
        Attainment now = ModPlayerData.of(player).path().getAttainment(path);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_attainment_down",
                Component.translatable(path.getTranslationKey()),
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }
}
