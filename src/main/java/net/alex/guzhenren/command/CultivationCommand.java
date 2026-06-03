package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.CultivationManager;
import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.alex.guzhenren.cultivation.enums.*;
import net.alex.guzhenren.cultivation.enums.Stage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public class CultivationCommand {
    private CultivationCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cultivation")
                        .requires(s -> s.hasPermission(2))

                        // ========== awaken ==========
                        .then(withOptionalTarget(
                                Commands.literal("awaken"),
                                CultivationCommand::awaken
                        ))

                        // ========== rank <0-5> ==========
                        .then(Commands.literal("rank")
                                .then(withOptionalTarget(
                                        Commands.argument("value", IntegerArgumentType.integer(0, 5)),
                                        CultivationCommand::setRank
                                )))

                        // ========== stage <init|mid|up|peak> ==========
                        .then(Commands.literal("stage")
                                .then(stageBranch("init", Stage.INITIAL))
                                .then(stageBranch("mid",  Stage.MIDDLE))
                                .then(stageBranch("up",   Stage.UPPER))
                                .then(stageBranch("peak", Stage.PEAK)))

                        // ========== roll <0-100> ==========
                        .then(Commands.literal("roll")
                                .then(withOptionalTarget(
                                        Commands.argument("value", IntegerArgumentType.integer(0, 100)),
                                        CultivationCommand::setRoll
                                )))

                        // ========== essence <add|set|consume> <value> ==========
                        .then(Commands.literal("essence")
                                .then(essenceBranch("add",     EssenceOp.ADD))
                                .then(essenceBranch("set",     EssenceOp.SET))
                                .then(essenceBranch("consume", EssenceOp.CONSUME)))

                        // ========== info ==========
                        .then(withOptionalTarget(
                                Commands.literal("info"),
                                CultivationCommand::info
                        ))

                        // ========== reset ==========
                        .then(withOptionalTarget(
                                Commands.literal("reset"),
                                CultivationCommand::reset
                        ))
        );
    }

    //region Branch Builders
    /**
     * 把 "可选 target 参数" 包装到任意分支末端
     * 调用方式: Commands.literal("xxx") → withOptionalTarget(literal, executor)
     */
    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T withOptionalTarget(
            T base, CommandExecutor exec
    ) {
        base.executes(ctx -> exec.run(ctx, self(ctx)));
        base.then(Commands.argument("target", EntityArgument.player())
                .executes(ctx -> exec.run(ctx, target(ctx))));
        return base;
    }

    /** stage 子分支生成器 */
    private static ArgumentBuilder<CommandSourceStack, ?> stageBranch(String name, Stage stage) {
        return withOptionalTarget(
                Commands.literal(name),
                (ctx, p) -> setStage(ctx, p, stage)
        );
    }

    /** essence 子分支生成器 */
    private static ArgumentBuilder<CommandSourceStack, ?> essenceBranch(String name, EssenceOp op) {
        return Commands.literal(name)
                .then(withOptionalTarget(
                        Commands.argument("value", LongArgumentType.longArg(0)),
                        (ctx, p) -> essenceOp(ctx, p, op)
                ));
    }
    //endregion

    //region Command Implementations

    private static int awaken(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        boolean ok = CultivationManager.awaken(player, player.getRandom());
        if (ok) {
            BasicAttachments b = CultivationManager.realm(player);
            feedback(ctx, Component.literal("Awakened ").append(player.getDisplayName())
                    .append(": ").append(b.getAptitude().getDisplayName())
                    .append(" (roll=" + b.getAptitudeRoll() + ")"));
            return 1;
        }
        ctx.getSource().sendFailure(player.getDisplayName().copy().append(" is already awakened"));
        return 0;
    }

    private static int setRank(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        int value = IntegerArgumentType.getInteger(ctx, "value");
        Rank rank = switch (value) {
            case 0 -> Rank.ORDINARY;
            case 1 -> Rank.RANK_1;
            case 2 -> Rank.RANK_2;
            case 3 -> Rank.RANK_3;
            case 4 -> Rank.RANK_4;
            case 5 -> Rank.RANK_5;
            default -> throw new IllegalStateException("rank out of range: " + value);
        };
        CultivationManager.setRank(player, rank);
        feedback(ctx, player.getDisplayName().copy().append(" rank → ").append(rank.getDisplayName()));
        return 1;
    }

    private static int setStage(CommandContext<CommandSourceStack> ctx, ServerPlayer player, Stage stage) {
        CultivationManager.setStage(player, stage);
        feedback(ctx, player.getDisplayName().copy().append(" stage → ").append(stage.getDisplayName()));
        return 1;
    }

    private static int setRoll(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        int value = IntegerArgumentType.getInteger(ctx, "value");
        CultivationManager.setAptitudeRoll(player, value);
        BasicAttachments b = CultivationManager.realm(player);
        feedback(ctx, player.getDisplayName().copy()
                .append(" roll → " + value + " (").append(b.getAptitude().getDisplayName()).append(")"));
        return 1;
    }

    private static int essenceOp(CommandContext<CommandSourceStack> ctx, ServerPlayer player, EssenceOp op) {
        long value = LongArgumentType.getLong(ctx, "value");
        EssenceAttachment e = CultivationManager.essence(player);
        switch (op) {
            case ADD     -> CultivationManager.addEssence(player, value);
            case SET     -> CultivationManager.setCurrentEssence(player, value);
            case CONSUME -> {
                boolean ok = CultivationManager.consumeEssence(player, value);
                if (!ok) {
                    ctx.getSource().sendFailure(Component.literal(
                            "Not enough essence (have " + e.getCurrentEssence() + ", need " + value + ")"));
                    return 0;
                }
            }
        }
        long after = CultivationManager.essence(player).getCurrentEssence();
        feedback(ctx, player.getDisplayName().copy()
                .append(" essence " + op.name().toLowerCase() + " " + value
                        + " → " + after + "/" + e.getMaxEssence()));
        return 1;
    }

    private static int info(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        BasicAttachments b = CultivationManager.realm(player);
        EssenceAttachment e = CultivationManager.essence(player);
        CommandSourceStack src = ctx.getSource();

        src.sendSuccess(() -> player.getDisplayName().copy()
                .append("'s Cultivation:").withStyle(ChatFormatting.GOLD), false);
        src.sendSuccess(() -> Component.literal("  awaken=" + b.isAwaken()), false);
        src.sendSuccess(() -> Component.literal("  rank=").append(b.getRank().getDisplayName())
                .append("  stage=").append(b.getStage().getDisplayName()), false);
        src.sendSuccess(() -> Component.literal("  aptitude=").append(b.getAptitude().getDisplayName())
                .append("  roll=" + b.getAptitudeRoll()), false);
        src.sendSuccess(() -> Component.literal("  essence=" + e.getCurrentEssence() + "/" + e.getMaxEssence()), false);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        CultivationManager.reset(player);
        feedback(ctx, player.getDisplayName().copy().append(" → reset to Ordinary mortal"));
        return 1;
    }
    //endregion

    //region Helpers
    /** 取自己 (执行者必须是玩家) */
    private static ServerPlayer self(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }

    /** 取 target 参数指定的玩家 */
    private static ServerPlayer target(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return EntityArgument.getPlayer(ctx, "target");
    }

    /** 统一反馈 (success，broadcast 给 admin) */
    private static void feedback(CommandContext<CommandSourceStack> ctx, Component msg) {
        ctx.getSource().sendSuccess(() -> msg, true);
    }

    /** 函数式接口：执行带 player 参数的命令 */
    @FunctionalInterface
    private interface CommandExecutor {
        int run(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException;
    }

    /** essence 子命令操作类型 */
    private enum EssenceOp { ADD, SET, CONSUME }
    //endregion
}
