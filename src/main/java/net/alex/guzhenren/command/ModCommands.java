package net.alex.guzhenren.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.enums.core.SoulLevel;
import net.alex.guzhenren.enums.core.TenExtreme;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.action.PlayerCoreActions;
import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.EssenceComponent;
import net.alex.guzhenren.gameplay.data.LifespanComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.PathComponent;
import net.alex.guzhenren.gameplay.data.SoulComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/** /guzhenren 根命令. 子命令 build 器分散在 CoreCommands / EssenceCommands / PathCommands / LifespanCommands / SoulCommands */
public final class ModCommands {

    private ModCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("guzhenren")
                        .requires(s -> s.hasPermission(2))
                        .then(CoreCommands.buildAwaken())
                        .then(buildInfo())
                        .then(CoreCommands.buildRank())
                        .then(CoreCommands.buildStage())
                        .then(CoreCommands.buildTalent())
                        .then(CoreCommands.buildBase())
                        .then(CoreCommands.buildPhysique())
                        .then(EssenceCommands.buildEssence())
                        .then(PathCommands.buildPath())
                        .then(LifespanCommands.buildLifespan())
                        .then(SoulCommands.buildSoul())
                        .then(buildReset())
        );
    }

//region INFO
    private static LiteralArgumentBuilder<CommandSourceStack> buildInfo() {
        return Commands.literal("info").executes(ctx -> cmdInfo(ctx.getSource()));
    }

    private static int cmdInfo(CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        if (player == null) return 0;
        ModPlayerData data = ModPlayerData.of(player);
        CoreComponent core = data.core();
        EssenceComponent essence = data.essence();
        PathComponent path = data.path();
        LifespanComponent lifespan = data.lifespan();
        SoulComponent soul = data.soul();

        src.sendSuccess(() -> Component.translatable("guzhenren.command.info.core",
                Component.translatable(core.getPlayerRank().getTranslationKey()),
                Component.translatable(core.getPlayerStage().getTranslationKey())), false);

        MutableComponent talentMsg = Component.translatable(core.getPlayerTalent().getTranslationKey())
                .append(Component.literal(" " + core.getPlayerBaseEssence() + "%"));
        TenExtreme physique = core.getPlayerExtremePhysique();
        if (physique != TenExtreme.NONE) {
            talentMsg.append(Component.literal(" ["))
                    .append(Component.translatable(physique.getTranslationKey()))
                    .append(Component.literal("]"));
        }
        final Component finalTalent = talentMsg;
        src.sendSuccess(() -> Component.translatable("guzhenren.command.info.talent", finalTalent), false);

        if (data.status().isApertureAwakened()) {
            src.sendSuccess(() -> Component.translatable("guzhenren.command.info.essence",
                    essence.getCurrentEssence(), essence.getMaxEssence()), false);
        } else {
            src.sendSuccess(() -> Component.translatable("guzhenren.command.info.not_awakened"), false);
        }

        src.sendSuccess(() -> Component.translatable("guzhenren.command.info.lifespan",
                lifespan.getAge(), lifespan.getRemainingYears()), false);

        SoulLevel soulLevel = SoulLevel.fromSoulValue(soul.getSoul());
        src.sendSuccess(() -> Component.translatable("guzhenren.command.info.soul",
                Component.translatable(soulLevel.getTranslationKey()), soul.getSoul()), false);

        boolean any = false;
        for (Path p : Path.values()) {
            long marks = path.getMarks(p);
            if (marks <= 0L) continue;
            any = true;
            src.sendSuccess(() -> Component.translatable("guzhenren.command.info.path_entry",
                    Component.translatable(p.getTranslationKey()),
                    Component.translatable(path.getAttainment(p).getTranslationKey()),
                    marks), false);
        }
        if (!any) src.sendSuccess(() -> Component.translatable("guzhenren.command.info.no_paths"), false);
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
    /** 命令 helper: 检查玩家是否已开窍, 未开窍时 send failure + return false */
    static boolean requireAwakened(CommandSourceStack src, ServerPlayer player) {
        ModPlayerData data = ModPlayerData.of(player);
        if (!PlayerCoreActions.isAwakened(data)) {
            src.sendFailure(Component.translatable("guzhenren.command.error.not_awakened"));
            return false;
        }
        return true;
    }
//endregion
}
