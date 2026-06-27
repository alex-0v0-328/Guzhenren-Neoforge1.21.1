package net.alex.guzhenren.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.enums.core.Talent;
import net.alex.guzhenren.enums.core.TenExtreme;
import net.alex.guzhenren.gameplay.action.PlayerCoreActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;

public class CoreCommands {

    //region AWAKEN
    public static LiteralArgumentBuilder<CommandSourceStack> buildAwaken() {
        return Commands.literal("awaken")
                .executes(ctx -> cmdAwaken(ctx.getSource(), null, -1))
                .then(Commands.argument("talent", EnumArgument.enumArgument(Talent.class))
                        .executes(ctx -> cmdAwaken(ctx.getSource(), ctx.getArgument("talent", Talent.class), -1))
                        .then(Commands.argument("percent", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> cmdAwaken(ctx.getSource(),
                                        ctx.getArgument("talent", Talent.class),
                                        IntegerArgumentType.getInteger(ctx, "percent")))));
    }

    private static int cmdAwaken(CommandSourceStack src, Talent talent, int percent) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        if (!PlayerCoreActions.canAwaken(data)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.cannot_awaken"));
            return 0;
        }
        if (talent == Talent.NONE) {
            src.sendFailure(Component.translatable("guzhenren.command.error.invalid_talent_none"));
            return 0;
        }
        if (percent >= 0) {
            if (percent < talent.getMinEssence() || percent > talent.getMaxEssence()) {
                src.sendFailure(Component.translatable("guzhenren.command.error.percent_out_of_range",
                        talent.getMinEssence(), talent.getMaxEssence()));
                return 0;
            }
            PlayerCoreActions.awaken(player, talent, percent);
        } else if (talent != null) {
            PlayerCoreActions.awaken(player, talent);
        } else {
            PlayerCoreActions.awaken(player);
        }
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.awakened"), false);
        return 1;
    }
//endregion

    //region RANK
    public static LiteralArgumentBuilder<CommandSourceStack> buildRank() {
        return Commands.literal("rank")
                .then(Commands.literal("set")
                        .then(Commands.argument("rank", EnumArgument.enumArgument(Rank.class))
                                .executes(ctx -> cmdSetRank(ctx.getSource(), ctx.getArgument("rank", Rank.class)))))
                .then(Commands.literal("up").executes(ctx -> cmdRankUp(ctx.getSource())))
                .then(Commands.literal("down").executes(ctx -> cmdRankDown(ctx.getSource())));
    }

    private static int cmdSetRank(CommandSourceStack src, Rank rank) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (rank == Rank.MORTAL) {
            src.sendFailure(Component.translatable("guzhenren.command.error.invalid_rank_mortal"));
            return 0;
        }
        PlayerCoreActions.setRank(player, rank);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.rank_set",
                Component.translatable(rank.getTranslationKey())), false);
        return 1;
    }

    private static int cmdRankUp(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.rankUp(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.rank_up_failed"));
            return 0;
        }
        Rank now = player.getData(ModAttachments.PLAYER_DATA.get()).core().getPlayerRank();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.rank_up",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }

    private static int cmdRankDown(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.rankDown(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.rank_down_failed"));
            return 0;
        }
        Rank now = player.getData(ModAttachments.PLAYER_DATA.get()).core().getPlayerRank();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.rank_down",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }
//endregion

    //region STAGE
    public static LiteralArgumentBuilder<CommandSourceStack> buildStage() {
        return Commands.literal("stage")
                .then(Commands.literal("set")
                        .then(Commands.argument("stage", EnumArgument.enumArgument(Stage.class))
                                .executes(ctx -> cmdSetStage(ctx.getSource(), ctx.getArgument("stage", Stage.class)))))
                .then(Commands.literal("up").executes(ctx -> cmdStageUp(ctx.getSource())))
                .then(Commands.literal("down").executes(ctx -> cmdStageDown(ctx.getSource())));
    }

    private static int cmdSetStage(CommandSourceStack src, Stage stage) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (stage == Stage.NONE) {
            src.sendFailure(Component.translatable("guzhenren.command.error.invalid_stage_none"));
            return 0;
        }
        PlayerCoreActions.setStage(player, stage);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.stage_set",
                Component.translatable(stage.getTranslationKey())), false);
        return 1;
    }

    private static int cmdStageUp(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.stageUp(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.stage_up_failed"));
            return 0;
        }
        Stage now = player.getData(ModAttachments.PLAYER_DATA.get()).core().getPlayerStage();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.stage_up",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }

    private static int cmdStageDown(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.stageDown(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.stage_down_failed"));
            return 0;
        }
        Stage now = player.getData(ModAttachments.PLAYER_DATA.get()).core().getPlayerStage();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.stage_down",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }
//endregion

    //region TALENT
    public static LiteralArgumentBuilder<CommandSourceStack> buildTalent() {
        return Commands.literal("talent")
                .then(Commands.literal("set")
                        .then(Commands.argument("talent", EnumArgument.enumArgument(Talent.class))
                                .executes(ctx -> cmdSetTalent(ctx.getSource(), ctx.getArgument("talent", Talent.class)))));
    }

    private static int cmdSetTalent(CommandSourceStack src, Talent talent) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (talent == Talent.NONE) {
            src.sendFailure(Component.translatable("guzhenren.command.error.invalid_talent_none"));
            return 0;
        }
        PlayerCoreActions.setTalent(player, talent);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.talent_set",
                Component.translatable(talent.getTranslationKey())), false);
        return 1;
    }
//endregion

    //region BASE
    public static LiteralArgumentBuilder<CommandSourceStack> buildBase() {
        return Commands.literal("base")
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> cmdAddBase(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> cmdSubBase(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))));
    }

    private static int cmdAddBase(CommandSourceStack src, int amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerCoreActions.addBaseEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.base_added", amount), false);
        return 1;
    }

    private static int cmdSubBase(CommandSourceStack src, int amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        PlayerCoreActions.subBaseEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.base_subbed", amount), false);
        return 1;
    }
//endregion

    //region PHYSIQUE
    public static LiteralArgumentBuilder<CommandSourceStack> buildPhysique() {
        return Commands.literal("physique")
                .then(Commands.literal("set")
                        .then(Commands.argument("physique", EnumArgument.enumArgument(TenExtreme.class))
                                .executes(ctx -> cmdSetPhysique(ctx.getSource(),
                                        ctx.getArgument("physique", TenExtreme.class)))));
    }

    private static int cmdSetPhysique(CommandSourceStack src, TenExtreme physique) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !ModCommands.requireAwakened(src, player)) return 0;
        if (physique == TenExtreme.NONE) {
            src.sendFailure(Component.translatable("guzhenren.command.error.invalid_physique_none"));
            return 0;
        }
        PlayerCoreActions.setPhysique(player, physique);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.physique_set",
                Component.translatable(physique.getTranslationKey())), false);
        return 1;
    }
//endregion
}
