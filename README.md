# 蛊真人 Guzhenren

A Minecraft NeoForge 1.21.1 mod based on the cultivation novel *Reverend Insanity* (《蛊真人》).

基于网络小说《蛊真人》设定的 Minecraft NeoForge 1.21.1 模组。

---

## Status / 状态

⚠ **Early development.** Core systems are functional, content is being added.

⚠ **早期开发阶段。**核心系统已可用，内容正在补充中。

---

## Implemented / 已实现

- **修为系统 Cultivation**：Rank (一转~五转) × Stage (初/中/高/巅峰) + Talent (十绝~丁等) + 十绝体
- **真元 Essence**：上限随天赋/转/阶动态计算，按天恢复
- **流派 Path**：25 个流派 × 9 档 Attainment，独立道痕 (marks)
- **持久化与同步**：DataAttachment + 多通道 codec sync
- **测试指令** `/guzhenren ...`：完整覆盖 awaken / rank / stage / talent / base / essence / physique / path / info / reset
- **元石 Essence Stone**：第一个物品，按比例恢复真元

---

## Tech Stack

- Minecraft 1.21.1
- NeoForge
- Java 21
- Gradle (ModDevGradle)

---

## Build

```bash
./gradlew runData       # 生成 lang / item model
./gradlew runClient     # 启动客户端测试
./gradlew build         # 构建 jar
```

---

## License

MIT