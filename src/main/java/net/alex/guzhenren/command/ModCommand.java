package net.alex.guzhenren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.enums.core.*;
import net.alex.guzhenren.enums.path.*;
import net.alex.guzhenren.gameplay.action.*;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachment;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;

public class ModCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("guzhenren")
                        .requires(s -> s.hasPermission(2))
                        .then(buildAwaken())
                        .then(buildInfo())
                        .then(buildRank())
                        .then(buildStage())
                        .then(buildTalent())
                        .then(buildBase())
                        .then(buildEssence())
                        .then(buildPhysique())
                        .then(buildPath())
                        .then(buildReset())
        );
    }

    //region AWAKEN
    private static LiteralArgumentBuilder<CommandSourceStack> buildAwaken() {
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
        ModPlayerData data = player.getData(ModAttachment.PLAYER_DATA.get());
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

    //region INFO
    private static LiteralArgumentBuilder<CommandSourceStack> buildInfo() {
        return Commands.literal("info").executes(ctx -> cmdInfo(ctx.getSource()));
    }

    private static int cmdInfo(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData data = player.getData(ModAttachment.PLAYER_DATA.get());

        src.sendSuccess(() -> Component.translatable("guzhenren.command.info.cultivation",
                Component.translatable(data.cultivation().getPlayerRank().getTranslationKey()),
                Component.translatable(data.cultivation().getPlayerStage().getTranslationKey())), false);

        Component talentMsg = Component.translatable(data.cultivation().getPlayerTalent().getTranslationKey())
                .copy().append(Component.literal(" " + data.cultivation().getPlayerBaseEssence() + "%"));
        if (data.cultivation().getPlayerExtremePhysique() != TenExtreme.NONE) {
            talentMsg = talentMsg.copy().append(Component.literal(" ["))
                    .append(Component.translatable(data.cultivation().getPlayerExtremePhysique().getTranslationKey()))
                    .append(Component.literal("]"));
        }
        final Component finalTalent = talentMsg;
        src.sendSuccess(() -> Component.translatable("guzhenren.command.info.talent", finalTalent), false);

        if (data.status().isApertureAwakened()) {
            src.sendSuccess(() -> Component.translatable("guzhenren.command.info.essence",
                    (long) data.essence().getCurrentEssence(), data.essence().getMaxEssence()), false);
        } else {
            src.sendSuccess(() -> Component.translatable("guzhenren.command.info.not_awakened"), false);
        }

        boolean any = false;
        for (Path p : Path.values()) {
            long marks = data.path().getMarks(p);
            if (marks <= 0L) continue;
            any = true;
            src.sendSuccess(() -> Component.translatable("guzhenren.command.info.path_entry",
                    Component.translatable(p.getTranslationKey()),
                    Component.translatable(data.path().getAttainment(p).getTranslationKey()),
                    marks), false);
        }
        if (!any) src.sendSuccess(() -> Component.translatable("guzhenren.command.info.no_paths"), false);
        return 1;
    }
    //endregion

    //region RANK
    private static LiteralArgumentBuilder<CommandSourceStack> buildRank() {
        return Commands.literal("rank")
                .then(Commands.literal("set")
                        .then(Commands.argument("rank", EnumArgument.enumArgument(Rank.class))
                                .executes(ctx -> cmdSetRank(ctx.getSource(), ctx.getArgument("rank", Rank.class)))))
                .then(Commands.literal("up").executes(ctx -> cmdRankUp(ctx.getSource())))
                .then(Commands.literal("down").executes(ctx -> cmdRankDown(ctx.getSource())));
    }

    private static int cmdSetRank(CommandSourceStack src, Rank rank) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
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
        if (player == null || !requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.rankUp(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.rank_up_failed"));
            return 0;
        }
        Rank now = player.getData(ModAttachment.PLAYER_DATA.get()).cultivation().getPlayerRank();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.rank_up",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }

    private static int cmdRankDown(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.rankDown(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.rank_down_failed"));
            return 0;
        }
        Rank now = player.getData(ModAttachment.PLAYER_DATA.get()).cultivation().getPlayerRank();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.rank_down",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }
    //endregion

    //region STAGE
    private static LiteralArgumentBuilder<CommandSourceStack> buildStage() {
        return Commands.literal("stage")
                .then(Commands.literal("set")
                        .then(Commands.argument("stage", EnumArgument.enumArgument(Stage.class))
                                .executes(ctx -> cmdSetStage(ctx.getSource(), ctx.getArgument("stage", Stage.class)))))
                .then(Commands.literal("up").executes(ctx -> cmdStageUp(ctx.getSource())))
                .then(Commands.literal("down").executes(ctx -> cmdStageDown(ctx.getSource())));
    }

    private static int cmdSetStage(CommandSourceStack src, Stage stage) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
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
        if (player == null || !requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.stageUp(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.stage_up_failed"));
            return 0;
        }
        Stage now = player.getData(ModAttachment.PLAYER_DATA.get()).cultivation().getPlayerStage();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.stage_up",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }

    private static int cmdStageDown(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        if (!PlayerCoreActions.stageDown(player)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.stage_down_failed"));
            return 0;
        }
        Stage now = player.getData(ModAttachment.PLAYER_DATA.get()).cultivation().getPlayerStage();
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.stage_down",
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }
    //endregion

    //region TALENT
    private static LiteralArgumentBuilder<CommandSourceStack> buildTalent() {
        return Commands.literal("talent")
                .then(Commands.literal("set")
                        .then(Commands.argument("talent", EnumArgument.enumArgument(Talent.class))
                                .executes(ctx -> cmdSetTalent(ctx.getSource(), ctx.getArgument("talent", Talent.class)))));
    }

    private static int cmdSetTalent(CommandSourceStack src, Talent talent) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
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
    private static LiteralArgumentBuilder<CommandSourceStack> buildBase() {
        return Commands.literal("base")
                .then(Commands.literal("add").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> cmdAddBase(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> cmdSubBase(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))));
    }

    private static int cmdAddBase(CommandSourceStack src, int amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerCoreActions.addBaseEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.base_added", amount), false);
        return 1;
    }

    private static int cmdSubBase(CommandSourceStack src, int amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerCoreActions.subBaseEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.base_subbed", amount), false);
        return 1;
    }
    //endregion

    //region ESSENCE
    private static LiteralArgumentBuilder<CommandSourceStack> buildEssence() {
        return Commands.literal("essence")
                .then(Commands.literal("add").then(Commands.argument("amount", FloatArgumentType.floatArg(0.01f))
                        .executes(ctx -> cmdAddEssence(ctx.getSource(), FloatArgumentType.getFloat(ctx, "amount")))))
                .then(Commands.literal("sub").then(Commands.argument("amount", FloatArgumentType.floatArg(0.01f))
                        .executes(ctx -> cmdSubEssence(ctx.getSource(), FloatArgumentType.getFloat(ctx, "amount")))))
                .then(Commands.literal("refill").executes(ctx -> cmdRefillEssence(ctx.getSource())));
    }

    private static int cmdAddEssence(CommandSourceStack src, float amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerEssenceActions.addEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_added", amount), false);
        return 1;
    }

    private static int cmdSubEssence(CommandSourceStack src, float amount) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerEssenceActions.subEssence(player, amount);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_subbed", amount), false);
        return 1;
    }

    private static int cmdRefillEssence(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerEssenceActions.refillEssence(player);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.essence_refilled"), false);
        return 1;
    }
    //endregion

    //region PHYSIQUE
    private static LiteralArgumentBuilder<CommandSourceStack> buildPhysique() {
        return Commands.literal("physique")
                .then(Commands.literal("set")
                        .then(Commands.argument("physique", EnumArgument.enumArgument(TenExtreme.class))
                                .executes(ctx -> cmdSetPhysique(ctx.getSource(),
                                        ctx.getArgument("physique", TenExtreme.class)))));
    }

    private static int cmdSetPhysique(CommandSourceStack src, TenExtreme physique) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
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

    //region PATH
    private static LiteralArgumentBuilder<CommandSourceStack> buildPath() {
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
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerPathActions.addMarks(player, path, marks);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_marks_added",
                Component.translatable(path.getTranslationKey()), marks), false);
        return 1;
    }

    private static int cmdSubMarks(CommandSourceStack src, Path path, long marks) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerPathActions.subMarks(player, path, marks);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_marks_subbed",
                Component.translatable(path.getTranslationKey()), marks), false);
        return 1;
    }

    private static int cmdSetAttainment(CommandSourceStack src, Path path, Attainment attainment) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        PlayerPathActions.setAttainment(player, path, attainment);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_attainment_set",
                Component.translatable(path.getTranslationKey()),
                Component.translatable(attainment.getTranslationKey())), false);
        return 1;
    }

    private static int cmdAttainmentUp(CommandSourceStack src, Path path) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        if (!PlayerPathActions.attainmentUp(player, path)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.path_attainment_up_failed"));
            return 0;
        }
        Attainment now = player.getData(ModAttachment.PLAYER_DATA.get()).path().getAttainment(path);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_attainment_up",
                Component.translatable(path.getTranslationKey()),
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }

    private static int cmdAttainmentDown(CommandSourceStack src, Path path) {
        ServerPlayer player = src.getPlayer();
        if (player == null || !requireAwakened(src, player)) return 0;
        if (!PlayerPathActions.attainmentDown(player, path)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.path_attainment_down_failed"));
            return 0;
        }
        Attainment now = player.getData(ModAttachment.PLAYER_DATA.get()).path().getAttainment(path);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.path_attainment_down",
                Component.translatable(path.getTranslationKey()),
                Component.translatable(now.getTranslationKey())), false);
        return 1;
    }
    //endregion

    //region RESET
    private static LiteralArgumentBuilder<CommandSourceStack> buildReset() {
        return Commands.literal("reset").executes(ctx -> cmdReset(ctx.getSource()));
    }

    private static int cmdReset(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        PlayerCoreActions.reset(player);
        src.sendSuccess(() -> Component.translatable("guzhenren.command.success.reset"), false);
        return 1;
    }
    //endregion

    //region HELPERS
    private static boolean requireAwakened(CommandSourceStack src, ServerPlayer player) {
        ModPlayerData data = player.getData(ModAttachment.PLAYER_DATA.get());
        if (!PlayerCoreActions.isAwakened(data)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.not_awakened"));
            return false;
        }
        return true;
    }
    //endregion
}
