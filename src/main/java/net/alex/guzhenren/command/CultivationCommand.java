package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.CultivationManager;
import net.alex.guzhenren.cultivation.EssenceManager;
import net.alex.guzhenren.cultivation.RealmManager;
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

                        // ========== awaken (跨系统) ==========
                        .then(withOptionalTarget(
                                Commands.literal("awaken"),
                                CultivationCommand::awaken
                        ))

                        // ========== rank <0-5> (RealmManager) ==========
                        .then(Commands.literal("rank")
                                .then(withOptionalTarget(
                                        Commands.argument("value", IntegerArgumentType.integer(0, 5)),
                                        CultivationCommand::setRank
                                )))

                        // ========== stage <init|mid|up|peak> (RealmManager) ==========
                        .then(Commands.literal("stage")
                                .then(stageBranch("init", Stage.INITIAL))
                                .then(stageBranch("mid",  Stage.MIDDLE))
                                .then(stageBranch("up",   Stage.UPPER))
                                .then(stageBranch("peak", Stage.PEAK)))

                        // ========== roll <0-100> (RealmManager) ==========
                        .then(Commands.literal("roll")
                                .then(withOptionalTarget(
                                        Commands.argument("value", IntegerArgumentType.integer(0, 100)),
                                        CultivationCommand::setRoll
                                )))

                        // ========== essence <add|set|consume> <value> (EssenceManager) ==========
                        .then(Commands.literal("essence")
                                .then(essenceBranch("add",     EssenceOp.ADD))
                                .then(essenceBranch("set",     EssenceOp.SET))
                                .then(essenceBranch("consume", EssenceOp.CONSUME)))

                        // ========== info ==========
                        .then(withOptionalTarget(
                                Commands.literal("info"),
                                CultivationCommand::info
                        ))

                        // ========== reset (跨系统) ==========
                        .then(withOptionalTarget(
                                Commands.literal("reset"),
                                CultivationCommand::reset
                        ))
        );
    }

    //region Branch Builders
    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T withOptionalTarget(
            T base, CommandExecutor exec
    ) {
        base.executes(ctx -> exec.run(ctx, self(ctx)));
        base.then(Commands.argument("target", EntityArgument.player())
                .executes(ctx -> exec.run(ctx, target(ctx))));
        return base;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> stageBranch(String name, Stage stage) {
        return withOptionalTarget(
                Commands.literal(name),
                (ctx, p) -> setStage(ctx, p, stage)
        );
    }

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
            BasicAttachments b = RealmManager.realm(player);
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
        RealmManager.setRank(player, rank);
        feedback(ctx, player.getDisplayName().copy().append(" rank → ").append(rank.getDisplayName()));
        return 1;
    }

    private static int setStage(CommandContext<CommandSourceStack> ctx, ServerPlayer player, Stage stage) {
        RealmManager.setStage(player, stage);
        feedback(ctx, player.getDisplayName().copy().append(" stage → ").append(stage.getDisplayName()));
        return 1;
    }

    private static int setRoll(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        int value = IntegerArgumentType.getInteger(ctx, "value");
        RealmManager.setAptitudeRoll(player, value);
        BasicAttachments b = RealmManager.realm(player);
        feedback(ctx, player.getDisplayName().copy()
                .append(" roll → " + value + " (").append(b.getAptitude().getDisplayName()).append(")"));
        return 1;
    }

    private static int essenceOp(CommandContext<CommandSourceStack> ctx, ServerPlayer player, EssenceOp op) {
        long value = LongArgumentType.getLong(ctx, "value");
        EssenceAttachment e = EssenceManager.essence(player);
        switch (op) {
            case ADD     -> EssenceManager.addEssence(player, value);
            case SET     -> EssenceManager.setCurrentEssence(player, value);
            case CONSUME -> {
                boolean ok = EssenceManager.consumeEssence(player, value);
                if (!ok) {
                    ctx.getSource().sendFailure(Component.literal(
                            "Not enough essence (have " + e.getCurrentEssence() + ", need " + value + ")"));
                    return 0;
                }
            }
        }
        long after = EssenceManager.essence(player).getCurrentEssence();
        feedback(ctx, player.getDisplayName().copy()
                .append(" essence " + op.name().toLowerCase() + " " + value
                        + " → " + after + "/" + e.getMaxEssence()));
        return 1;
    }

    private static int info(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        BasicAttachments b = RealmManager.realm(player);
        EssenceAttachment e = EssenceManager.essence(player);
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
    private static ServerPlayer self(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }

    private static ServerPlayer target(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return EntityArgument.getPlayer(ctx, "target");
    }

    private static void feedback(CommandContext<CommandSourceStack> ctx, Component msg) {
        ctx.getSource().sendSuccess(() -> msg, true);
    }

    @FunctionalInterface
    private interface CommandExecutor {
        int run(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException;
    }

    private enum EssenceOp { ADD, SET, CONSUME }
    //endregion
}
