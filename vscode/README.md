# AtRef — VS Code extension

一键把当前文件复制为 `@<路径>:<行号>` —— Claude Code / Cursor / Copilot 都认识的引用格式。同时支持 Markdown 链接、带行号代码片段、绝对路径、多光标。

One keystroke to copy the current file as `@<path>:<line>` — the reference format Claude Code / Cursor / Copilot recognise. Also Markdown links, with-code snippets, absolute paths, multi-cursor.

> JetBrains 系 IDE（IDEA / PyCharm / WebStorm / GoLand …）请用同仓库的 [`../jetbrains/`](../jetbrains/) 平行实现。

---

## 中文说明

### 输出示例

| 场景 | 输出 |
| --- | --- |
| 光标在第 45 行 | `@src/services/voice_input_vad.py:45` |
| 选中第 45–60 行 | `@src/services/voice_input_vad.py:45-60` |
| 在资源管理器右键（无选区） | `@src/services/voice_input_vad.py` |
| Markdown 链接 | `[src/services/voice_input_vad.py:45](src/services/voice_input_vad.py#L45)` |
| 多光标（两条） | `@src/a.ts:10`<br>`@src/b.ts:20` |
| 带代码片段 | `@src/a.ts:45` + 代码围栏（每行带原始行号） |

### 命令

| 命令 ID | 中文菜单 | 默认快捷键 |
| --- | --- | --- |
| `atRef.copy` | 复制（默认格式） | `Cmd/Ctrl+Alt+C` |
| `atRef.copyAsAtRef` | 复制为 `@path:line` | — |
| `atRef.copyAsMarkdownLink` | 复制为 Markdown 链接 | `Cmd/Ctrl+Alt+M` |
| `atRef.copyAsColon` | 复制为 `path:line` | — |
| `atRef.copyWithCode` | 复制 `@path:line` + 代码片段 | `Cmd/Ctrl+Alt+Shift+C` |
| `atRef.copyAbsolutePath` | 复制绝对路径与行号 | — |

触发方式：

- **快捷键**：见上表
- **编辑器右键** → *AtRef · 复制为…* 子菜单（所有格式）
- **编辑器标签 / 资源管理器右键** → *AtRef · 复制（默认格式）*
- **命令面板**（`Cmd/Ctrl+Shift+P`）→ 搜索 `AtRef`

### 配置项（Settings → 搜索 `atRef`）

| 配置 | 取值 | 默认 | 说明 |
| --- | --- | --- | --- |
| `atRef.defaultFormat` | `at` / `markdown` / `colon` / `withCode` / `absolute` | `at` | 主命令与快捷键使用的格式 |
| `atRef.pathStyle` | `workspace` / `git` / `absolute` / `basename` | `workspace` | 路径基准 |
| `atRef.rangeSeparator` | string | `-` | 行号范围分隔符，例如 `:` 输出 `:45:60` |
| `atRef.multiSelectionSeparator` | string | `\n` | 多选区时多条引用之间的连接符 |
| `atRef.notification` | `statusBar` / `info` / `none` | `statusBar` | 复制成功后的提示位置 |
| `atRef.usePosixSeparators` | boolean | `true` | 是否强制使用正斜杠 |
| `atRef.codeBlockLineNumbers` | boolean | `true` | "+代码片段" 时代码块每行前加原始行号 |
| `atRef.codeBlockContextLines` | integer | `0` | "+代码片段" 时选区前后额外带的上下文行数 |

### 开发调试

```bash
code /Users/crounix/data/www/AI/atref/vscode    # 用 VS Code 打开本子目录（不是仓库根）
```

然后按 `F5` 启动 Extension Development Host，在新窗口里测试命令。

> 注意：必须用 `code vscode/` 打开**这个子目录**，因为 F5 走的是 `.vscode/launch.json` 里的 `${workspaceFolder}`，必须能找到旁边的 `package.json`。

### 打包并本地安装

```bash
cd vscode
npm install
npx vsce package          # 生成 atref-0.2.0.vsix
code --install-extension atref-0.2.0.vsix
```

---

## English

### Example output

| Scenario | Output |
| --- | --- |
| Cursor on line 45 | `@src/services/voice_input_vad.py:45` |
| Selection lines 45–60 | `@src/services/voice_input_vad.py:45-60` |
| Right-clicked in Explorer (no selection) | `@src/services/voice_input_vad.py` |
| Markdown link | `[src/services/voice_input_vad.py:45](src/services/voice_input_vad.py#L45)` |
| Multi-cursor (two carets) | `@src/a.ts:10`<br>`@src/b.ts:20` |
| With code | `@src/a.ts:45` plus a fenced code block (each line prefixed with its original line number) |

### Commands

| Command id | Title | Default keybinding |
| --- | --- | --- |
| `atRef.copy` | Copy (default format) | `Cmd/Ctrl+Alt+C` |
| `atRef.copyAsAtRef` | Copy as `@path:line` | — |
| `atRef.copyAsMarkdownLink` | Copy as Markdown Link | `Cmd/Ctrl+Alt+M` |
| `atRef.copyAsColon` | Copy as `path:line` | — |
| `atRef.copyWithCode` | Copy `@path:line` + Code Snippet | `Cmd/Ctrl+Alt+Shift+C` |
| `atRef.copyAbsolutePath` | Copy Absolute Path with Line | — |

Trigger points: keybindings (above), the editor right-click submenu *AtRef — Copy as…* (all formats), the editor tab and Explorer context menus (default format only), and the Command Palette (search `AtRef`).

### Settings (search `atRef`)

| Setting | Values | Default | Notes |
| --- | --- | --- | --- |
| `atRef.defaultFormat` | `at` / `markdown` / `colon` / `withCode` / `absolute` | `at` | Format used by the main command + keybinding |
| `atRef.pathStyle` | `workspace` / `git` / `absolute` / `basename` | `workspace` | Path base |
| `atRef.rangeSeparator` | string | `-` | Multi-line range separator (`:` produces `:45:60`) |
| `atRef.multiSelectionSeparator` | string | `\n` | Joiner for multi-selection output |
| `atRef.notification` | `statusBar` / `info` / `none` | `statusBar` | Where the "Copied …" confirmation appears |
| `atRef.usePosixSeparators` | boolean | `true` | Force forward slashes |
| `atRef.codeBlockLineNumbers` | boolean | `true` | Prefix each line in "+ code" block with its original line number |
| `atRef.codeBlockContextLines` | integer | `0` | Extra lines of context before/after the selection in "+ code" |

### Develop

```bash
code /Users/crounix/data/www/AI/atref/vscode    # open *this subdirectory*, not the repo root
```

Then press `F5` to launch an Extension Development Host. The `.vscode/launch.json` uses `${workspaceFolder}`, which must point at the directory containing `package.json`.

### Package & install locally

```bash
cd vscode
npm install
npx vsce package          # produces atref-0.2.0.vsix
code --install-extension atref-0.2.0.vsix
```
