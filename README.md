# AtRef

[![CI](https://github.com/cranux/atref/actions/workflows/ci.yml/badge.svg)](https://github.com/cranux/atref/actions/workflows/ci.yml)
[![Release](https://github.com/cranux/atref/actions/workflows/release.yml/badge.svg)](https://github.com/cranux/atref/actions/workflows/release.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)

一键把当前文件复制为 `@<路径>:<行号>` —— Claude Code / Cursor / Copilot 都认识的引用格式。也支持 Markdown 链接、带行号代码片段、绝对路径、多光标。

One keystroke to copy the current file as `@<path>:<line>` — the reference format Claude Code / Cursor / Copilot recognise. Also Markdown links, with-code snippets, absolute paths, multi-cursor.

**下载预编译产物 / Download prebuilt binaries**：[GitHub Releases](https://github.com/cranux/atref/releases)

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

## 发布流程 / Releasing

CI 在每次 `push` 到 `main` 或开 PR 时跑，并行打包两边、把产物上传到 workflow run（保留 14 天，可在 GitHub Actions 页下载试装）。

正式发布走 tag：

```bash
# 1) 把 vscode/package.json 和 jetbrains/build.gradle.kts 的 version 同步到目标版本
# 2) commit + push 这两个版本号改动
# 3) 打 tag 并 push
git tag v0.3.0
git push origin v0.3.0
```

`Release` workflow 看到 `v*` tag 就会：

1. 并行跑 `vsce package`（产出 `atref-0.3.0.vsix`）和 `gradle buildPlugin`（产出 `atref-0.3.0.zip`）
2. 把两个产物都挂到一个新的 GitHub Release（标题 = tag 名）
3. release notes 自动从上一次 tag 至今的 commit 生成

CI runs on every push to `main` and on PRs, building both implementations in parallel and uploading the artifacts to the workflow run (kept for 14 days; downloadable from the Actions tab for sanity-installs).

Production releases are tag-triggered. Bump versions in `vscode/package.json` and `jetbrains/build.gradle.kts`, commit, then `git tag v0.3.0 && git push origin v0.3.0`. The `Release` workflow packages both, attaches the `.vsix` + `.zip` to a new GitHub Release, and auto-generates notes from the commit log.

## License

MIT — see [`LICENSE`](./LICENSE).
