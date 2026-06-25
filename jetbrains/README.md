# AtRef — JetBrains plugin

VSCode 扩展的 JetBrains 平行版本。单个插件兼容所有 IntelliJ 系 IDE（IDEA / PyCharm / WebStorm / GoLand / RubyMine / PhpStorm / Rider / CLion / RustRover…）。

JetBrains counterpart of the AtRef VSCode extension. One plugin runs on every IntelliJ-based IDE.

## 命令 / Actions

| Action ID | 中文 | English |
| --- | --- | --- |
| `AtRef.Copy` | AtRef: 复制（默认格式） | AtRef: Copy (Default Format) |
| `AtRef.CopyAtRef` | @path:line | @path:line |
| `AtRef.CopyColon` | path:line | path:line |
| `AtRef.CopyMarkdownLink` | Markdown 链接 | Markdown Link |
| `AtRef.CopyAbsolutePath` | 绝对路径与行号 | Absolute Path with Line |
| `AtRef.CopyWithCode` | @path:line + 代码片段 | @path:line + Code Snippet |

## 触发方式 / Trigger points

- 编辑器右键 → *AtRef — Copy as…* 子菜单
- 编辑器标签右键 → *AtRef — Copy as…* 子菜单
- Project View 右键 → *AtRef — Copy as…* 子菜单
- Find Action（`Cmd/Ctrl+Shift+A`）搜 `AtRef`

## 快捷键 / Keybindings

**默认不绑定任何快捷键**，因为 `Cmd+Alt+C` / `Cmd+Alt+Shift+C` / `Cmd+Alt+M` 在 JetBrains 里都被 *Extract Constant / Copy Reference / Extract Method* 占了。

To bind a shortcut: **Settings → Keymap**, search "AtRef", right-click the action → *Add Keyboard Shortcut*. Suggested non-conflicting combos: `Cmd/Ctrl+Shift+Y`, `Cmd/Ctrl+Alt+Y`.

默认不绑快捷键。在 **Settings → Keymap** 搜 "AtRef"，右键想用的 action → *Add Keyboard Shortcut* 自己绑。推荐不冲突的组合：`Cmd/Ctrl+Shift+Y`、`Cmd/Ctrl+Alt+Y`。

## 设置 / Settings

**Settings → Tools → AtRef**

- Default format / 默认格式：`at` · `markdown` · `colon` · `withCode` · `absolute`
- Path style / 路径基准：`workspace` · `git` · `absolute` · `basename`
- POSIX separators / 正斜杠分隔符（默认开）
- Range separator / 行号范围分隔符（默认 `-`）
- Multi-selection joiner / 多选区连接符（默认 `\n`，即换行）
- Code block line numbers / 代码块带行号（默认开）
- Code block context lines / 上下文行数（默认 0）
- Notification / 提示位置：`statusBar` · `info` · `none`

## 构建与本地安装 / Build & install

**推荐方式（不用装 JDK / Gradle）**：用 IDEA Ultimate / Community 直接打开 `jetbrains/` 目录，IDEA 会自动识别 Gradle 项目并下载所需 JDK。然后：

1. 右侧 **Gradle 工具窗** → `atref` → `Tasks` → `intellij platform` → 双击 `buildPlugin`
2. 产物在 `jetbrains/build/distributions/atref-0.2.0.zip`
3. 在任意 JetBrains IDE 里：**Settings → Plugins → ⚙ → Install Plugin from Disk…** 选上面那个 zip
4. 重启 IDE 生效

**命令行方式**（需要本机有 JDK 17+ 和 Gradle 8+，或先生成 wrapper）：

```bash
cd jetbrains
# 首次：用 IDEA 打开一次会自动生成 ./gradlew；或者本机装 Gradle 后跑：
#   gradle wrapper --gradle-version 8.10
./gradlew buildPlugin
```

## 调试 / Run in a sandbox IDE

在 Gradle 工具窗找到 `runIde` 双击（或命令行 `./gradlew runIde`）。会拉起一个干净的 IDEA Community 沙盒实例（首次约 1–2 分钟下载），插件已预装。改完代码 stop 重跑即可。

## 跟 VSCode 版的差异 / Differences vs the VSCode extension

- 默认不附带快捷键（VSCode 版默认绑了三个）
- 设置 UI 是 IntelliJ 原生的 `Configurable` 面板，不是 `settings.json`
- `pathStyle: workspace` 在 JetBrains 里指 *Project 根目录*（`Project.guessProjectDir()`），效果与 VSCode 的 workspace folder 一致
- 通知用 `NotificationGroup`（balloon）或 status bar，对应 VSCode 的 `info` / `statusBar`

## 标识符总览 / Identifiers

| 类型 | 值 |
| --- | --- |
| Plugin ID | `com.atref` |
| Plugin name | `AtRef` |
| Gradle group | `com.atref` |
| Gradle rootProject | `atref` |
| Kotlin package | `com.atref` |
| Action ID prefix | `AtRef.*` |
| Resource bundle | `messages.AtRefBundle` |
| Settings storage file | `atref.xml` |
| Notification group | `AtRef` |
