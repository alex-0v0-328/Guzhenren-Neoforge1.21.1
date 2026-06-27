# AGENTS.md - Guzhenren (蛊真人)

A Minecraft NeoForge 1.21.1 mod based on the cultivation novel *Reverend Insanity* (《蛊真人》).

## Tech Stack

- **Minecraft** 1.21.1
- **Mod Loader** NeoForge (21.1.233)
- **Java** 21 (via `java.toolchain.languageVersion`)
- **Build** Gradle with ModDevGradle 2.0.141
- **Mappings** Parchment (2024.11.17)
- **Serialization** Codec + StreamCodec (attachments and network)

## Package Structure

```
net.alex.guzhenren
├── Guzhenren.java              # @Mod entry point (common side)
├── GuzhenrenClient.java        # @Mod(dist=Dist.CLIENT) entry point
├── client/                     # HUD, client events, client cache
├── command/                    # /guzhenren subcommands
├── datagen/                    # Lang + item model data generators
├── enums/                      # Rank, Stage, Talent, TenExtreme, Path, Attainment
├── event/                      # PlayerEvents, CommandEvents (server-side)
├── gameplay/
│   ├── action/                 # Actions that mutate player state
│   └── data/                   # Data records: Core, Essence, Status, Path, ModPlayerData
├── item/                       # Custom items (e.g. EssenceStoneItem)
├── network/
│   ├── ModPayloads.java        # Network packet registration (playToClient)
│   └── sync/                   # Sync payloads (Core, Essence, Status, Path, Full)
└── registry/                   # ModItems, ModAttachments, ModCreativeTabs
```

## Architecture

### Data System (`gameplay/data/`)

All player data lives in a single `ModPlayerData` record attached via NeoForge `AttachmentType`. Sub-components:

| Record | Fields | Purpose |
|--------|--------|---------|
| `CoreComponent` | rank, stage, talent, extremePhysique, baseEssence | Cultivation core |
| `EssenceComponent` | currentEssence, maxEssence | True essence pool |
| `StatusComponent` | apertureAwakened | Whether player has awakened |
| `PathComponent` | Map<Path, Attainment> + Map<Path, Long> | 25 paths × attainment + dao marks |

- Each component implements `Codec` for NBT persistence.
- `ModPlayerData.CODEC` is used by `AttachmentType.builder().serialize(...)`.
- Dirty tracking: `core.clearDirty()` / `status.clearDirty()` / `path.clearDirty()` is called after copying to prevent redundant sync.

### Network Sync (`network/`)

Payloads are registered in `ModPayloads.java` via `@EventBusSubscriber`. All sync is `playToClient` — the server pushes state to clients. Each component has its own payload (`CoreSyncPayload`, `EssenceSyncPayload`, `StatusSyncPayload`, `PathSyncPayload`) plus a full `ModPlayerSyncPayload` for bulk sync.

### Commands (`command/`)

All commands are under `/guzhenren` (permission level 2). Subcommands:
- `awaken`, `rank`, `stage`, `talent`, `base`, `physique` → `CoreCommands`
- `essence` → `EssenceCommands`
- `path` → `PathCommands`
- `info` → `ModCommands`
- `reset` → `ModCommands`

### Enums (`enums/`)

- `Rank`: MORTAL ~ FIVE (一转~五转)
- `Stage`: EARLY, MIDDLE, ADVANCED, PEAK (初/中/高/巅峰)
- `Talent`: levels from A~D plus 十绝体-related
- `TenExtreme`: 十绝体 types
- `Path`: 25 cultivation paths
- `Attainment`: 9 attainment levels per path

All enums implement `StringRepresentable` for codec compatibility and have `getTranslationKey()` for i18n.

### Registries (`registry/`)

Pattern: Each registry class has a `public static void register(IEventBus modEventBus)` method. Registries are called from `Guzhenren` constructor.

- `ModItems`: Uses `DeferredRegister.Items`, registers custom items.
- `ModAttachments`: Uses `DeferredRegister<AttachmentType<?>>`, registers `ModPlayerData` attachment.
- `ModCreativeTabs`: Creative mode tab registration.

### Client (`client/`)

- `ClientPayloadHandlers`: Handles all incoming sync payloads, updates `ClientPlayerData` cache.
- `ClientPlayerData`: Client-side singleton cache for current player's data.
- `PlayerStatsHud`: Renders cultivation info HUD overlay.
- `ClientEvents`: Handles client tick, login, etc.

## Code Conventions

1. **No comments by default** — follow existing style; code should be self-documenting.
2. **No emojis** in code or logs.
3. **Chinese comments** are acceptable where they exist (the mod is based on Chinese source material).
4. Use **records** for data classes, **enums** for enumerations implementing `StringRepresentable`.
5. Registration pattern: `DeferredRegister` fields → `register(IEventBus)` static method.
6. Translation keys use the namespace `guzhenren.*` (e.g. `guzhenren.enum.core.rank.mortal`).
7. `PlayerCoreActions`, `PlayerEssenceActions`, `PlayerPathActions` are the only classes that mutate player state — all mutations go through them.
8. Component fields are mutable (`set*` methods) but accessed through the record wrapper.
9. `requireAwakened()` helper in `ModCommands` is shared by subcommand builders.

## Build Commands

```bash
./gradlew runData       # Run data generators (lang files, item models)
./gradlew runClient     # Launch client for testing
./gradlew runServer     # Launch dedicated server
./gradlew build         # Build the mod JAR
./gradlew check         # Run tests (if any)
```

## Key Files

| File | Purpose |
|------|---------|
| `gradle.properties` | All mod metadata (mod_id, version, group, Neo version) |
| `build.gradle` | NeoForge MDG build configuration |
| `src/main/resources/guzhenren.mixins.json` | Mixin config (currently empty) |
| `src/generated/resources/` | Auto-generated lang files and item models |

## Adding a New Feature

1. Define data fields in the appropriate `*Component` record.
2. Add serialization (update `Codec` in the component).
3. Create a new `*SyncPayload` if needed, register in `ModPayloads`.
4. Add handler in `ClientPayloadHandlers`.
5. Add mutation methods in the appropriate `*Actions` class.
6. Add command in the appropriate `*Commands` class.
7. Run `./gradlew runData` to regenerate language entries if new translation keys were added.
