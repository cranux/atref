# AtRef

一键把当前文件复制为 `@<路径>:<行号>` —— Claude Code / Cursor / Copilot 都认识的引用格式。也支持 Markdown 链接、带行号代码片段、绝对路径、多光标。

One keystroke to copy the current file as `@<path>:<line>` — the reference format Claude Code / Cursor / Copilot recognise. Also Markdown links, with-code snippets, absolute paths, multi-cursor.

## 两个独立实现 / Two parallel implementations

| 子目录 | 目标 IDE | 技术栈 | 构建产物 | 文档 |
| --- | --- | --- | --- | --- |
| [`vscode/`](./vscode/) | VS Code · Cursor · Windsurf · 其它 VS Code 系 | JS + `package.json` | `.vsix` | [`vscode/README.md`](./vscode/README.md) |
| [`jetbrains/`](./jetbrains/) | IntelliJ IDEA · PyCharm · WebStorm · GoLand · PhpStorm · RubyMine · Rider · CLion · RustRover | Kotlin + Gradle + `plugin.xml` | `.zip`（plugin） | [`jetbrains/README.md`](./jetbrains/README.md) |

两边功能 1:1 对齐：5 种复制格式（`@path:line` / Markdown 链接 / `path:line` / 带行号代码片段 / 绝对路径）、多光标去重、4 种路径基准（workspace · git · absolute · basename）、中英双语菜单。

技术栈完全不同（VS Code 是 Node.js + Extension API，JetBrains 是 JVM + IntelliJ Platform），所以代码一行也不共享，只共享设计语义。

## 快速开始 / Quickstart

**VS Code / Cursor**

```bash
code vscode/        # 打开子目录，按 F5 启动 Extension Host
```

**JetBrains（IDEA / PyCharm / WebStorm / GoLand …）**

用 IDEA 打开 `jetbrains/`，Gradle 工具窗双击 `runIde` 拉起一个干净的沙盒 IDE。

详细的命令清单、快捷键、配置项、安装方法见两个子目录各自的 README。

## 仓库布局 / Layout

```
atref/  (this repo)
├── README.md                  ← 本文件（总览）
├── LICENSE                    ← MIT，覆盖整个仓库
├── .gitignore
│
├── vscode/                    ← VS Code 扩展
│   ├── package.json           （name: "atref"，命令 + 配置前缀均为 atRef.*）
│   ├── src/extension.js
│   ├── package.nls.{,zh-cn}.json
│   ├── .vscode/launch.json
│   ├── .vscodeignore
│   ├── CHANGELOG.md
│   └── README.md
│
└── jetbrains/                 ← IntelliJ Platform 插件
    ├── build.gradle.kts       （group: "com.atref"）
    ├── settings.gradle.kts    （rootProject.name = "atref"）
    ├── gradle.properties
    ├── src/main/kotlin/com/atref/*.kt
    ├── src/main/resources/META-INF/plugin.xml   （<id>com.atref</id>）
    ├── src/main/resources/messages/AtRefBundle{,_zh_CN}.properties
    └── README.md
```

## 命名 / Naming

**AtRef** = `@` + reference。直接对应 Claude Code / Cursor 圈子里 `@path:line` 这个"召唤文件"的核心符号。

- VS Code 命令前缀：`atRef.*`（如 `atRef.copy` / `atRef.copyWithCode`）
- VS Code 配置前缀：`atRef.*`
- JetBrains plugin ID：`com.atref`
- JetBrains action ID 前缀：`AtRef.*`（如 `AtRef.Copy` / `AtRef.CopyWithCode`）
- Kotlin 包：`com.atref`

## License

MIT — see [`LICENSE`](./LICENSE).
