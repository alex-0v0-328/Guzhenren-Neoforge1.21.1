# AGENTS.md — Guzhenren (蛊真人)

Minecraft NeoForge 1.21.1 mod. Gradle + ModDevGradle + Java 21.

## Build commands

```bash
./gradlew runData       # generate lang/item models into src/generated/resources/ — MUST run first or after adding items
./gradlew runClient     # launch client for testing
./gradlew build         # build the mod jar
./gradlew runGameTestServer  # run game tests
```

**`runData` is required before `runClient` or `build`** if you've added/modified items, damage types, or lang keys. Generated assets go to `src/generated/resources/` which is included in the source set but gitignored (except `.cache` files).

## Architecture

- **Single mod**, one source set. `mod_id = guzhenren`, group `net.alex.guzhenren`.
- **Entrypoint**: `Guzhenren.java` — `@Mod(Guzhenren.MOD_ID)` constructor registers attachments, items, creative tabs on the mod event bus.
- **Client**:
  - `GuzhenrenClient.java` — `@Mod(..., dist = Dist.CLIENT)` constructor, currently empty (placeholder for future config screens).
  - `event/ClientEvents.java` — `@EventBusSubscriber(Dist.CLIENT)` registers HUD overlay (`PlayerStatsHud`) via `RegisterGuiLayersEvent`.
  - `network/ModPayloads.java` — `@EventBusSubscriber` registers all play-to-client payload handlers. Handlers dispatch to static methods in `client/ClientPayloadHandlers.java`.
  - `client/ClientPlayerData.java` — static fields mirroring server-side `ModPlayerData` components (Core, Essence, Status, Path, Lifespan, Soul). Payload handlers write into these.
- **Registries**: `registry/` — `DeferredRegister` pattern for attachments, items, creative tabs. Damage types use `ResourceKey` directly (no DeferredRegister), generated via datagen.
- **Events**: `event/` — `@EventBusSubscriber` auto-registered handlers:
  - `ModPlayerEvents` — server tick (natural essence recovery, aging, death checks, throttled sync), login/respawn/clone/wake-up events.
  - `ModCommandEvents` — registers `/guzhenren` command dispatcher.
- **Player data**: `gameplay/data/ModPlayerData.java` — single `DataAttachment<ModPlayerData>` using codec serialization. Sub-components: CoreComponent, EssenceComponent, StatusComponent, PathComponent, LifespanComponent, SoulComponent (all codec-serializable, with dirty flags for sync).
- **Commands**: `command/ModCommands.java` — root `/guzhenren` (permission level 2), subcommands: awaken, info, rank, stage, talent, base, essence, physique, path, reset, lifespan, soul.
- **Gameplay actions**: `gameplay/action/` — pure logic classes (`PlayerCoreActions`, `PlayerEssenceActions`, `PlayerLifespanActions`, `PlayerPathActions`, `PlayerSoulActions`) that mutate `ModPlayerData` via `player.getData(ATTACHMENT)`.
- **Network sync**: `network/sync/` — 8 payload types (ModPlayerSyncPayload full, + per-component: Core/Essence/Status/Path/PathDelta/Lifespan/Soul). All use `CustomPacketPayload` + `StreamCodec`. Two sync strategies:
  - Immediate (dirty flag): Status, Essence/Lifespan/Soul action changes.
  - Throttled (every 20 ticks): Core, Path, Essence natural recovery.
- **Data generation**: `datagen/DataGenerators.java` — auto-registered via `@EventBusSubscriber`, generates lang (en_us, zh_cn), item models, damage types, damage type tags.
- **Enums**: `enums/` — core (Rank, Stage, Talent, TenExtreme, SoulLevel), path (Path, Attainment), gu (GuType).
- **Gu system**: `item/gu/` — `GuItem` (extends Item, has `GuProperties` + `GuEffect`), `GuProperties` (path, rank, type, feed/refine config), `GuEffect` functional interface, `GuEffectResult` record. Effects use lambda factories in `GuEffects.java`.
- **Mixins**: declared in `guzhenren.mixins.json` but currently empty (no mixin classes yet).

## Project-specific quirks

- **`neoforge.mods.toml` is a template** in `src/main/templates/META-INF/`. Variables (`${mod_id}`, `${mod_version}`, etc.) are expanded from `gradle.properties` by the `generateModMetadata` task. Never edit generated output directly.
- **Parchment mappings** are used (version in `gradle.properties`). Parameter names in Minecraft classes are human-readable.
- **Gradle configuration cache** is enabled (`org.gradle.configuration-cache=true`). If builds behave oddly, `./gradlew --stop && rm -rf .gradle/configuration-cache`.
- **BlockBench `.bbmodel` files** in resources are excluded from the final jar (`build.gradle` sourceSet config).
- **Generated JSON line endings**: `.gitattributes` enforces LF for `src/generated/**/*.json` to avoid git churn from datagen on Windows.
- **Wrapper distribution**: BIN (not ALL). If you switch to ALL for IDE javadoc support, run the wrapper task twice afterwards.
- **No CI** configured currently. **No formatter/linter** configured. **No `src/test/`** directory.
- Game tests are enabled in all run configs via `neoforge.enabledGameTestNamespaces = guzhenren`.
