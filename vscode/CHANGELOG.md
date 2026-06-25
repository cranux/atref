# Changelog

## 0.2.1

- **插件图标**：白色 `@` + indigo `#4F46E5` 圆角方块。VS Code Extensions 列表 / Marketplace 缩略图都能看到
- **运行时消息中文化**：之前 `package.nls.zh-cn.json` 只覆盖命令面板 / 菜单 / 设置；这一版把 `extension.js` 里 3 条运行时消息（无激活编辑器警告、不支持文件协议警告、"已复制" 提示）也按 VS Code 显示语言切到中文 / 英文，跟 JetBrains 版的本地化覆盖范围对齐
- **打包优化**：`icon.svg` 源文件加入 `.vscodeignore`，不进 `.vsix`

## 0.2.0

- **重命名**：插件 `name` 改为 `atref`，所有命令前缀 `copyPathWithLine.*` → `atRef.*`，所有配置前缀同样改为 `atRef.*`。⚠️ 升级后需要在 Keyboard Shortcuts 重新绑定（如果你之前用了旧的命令 ID），原有 settings.json 里的 `copyPathWithLine.*` 配置需要手动迁移成 `atRef.*`
- `+ Code Snippet`（`Cmd+Alt+Shift+C`）现在默认在代码块每行前加上**原始行号**，AI 可以精确引用某一行
- 新配置 `codeBlockLineNumbers`（默认 `true`）—— 可关掉行号回到纯代码
- 新配置 `codeBlockContextLines`（默认 `0`）—— 在选区前后额外带 N 行上下文，但 `@path:起-止` 头部仍然只反映原始选区
- 代码块语言标签：当 VS Code 识别不出时按扩展名兜底（`.py` → `python`，`.tsx` → `tsx`，等 30+ 种）

## 0.1.0

- 多种命令：`@path:line`、Markdown 链接、`path:line`、带代码片段、绝对路径
- 默认命令格式可在设置里切换
- 路径基准可选：workspace / git 根目录 / 绝对路径 / 仅文件名
- 多光标 / 多选区：每个选区生成一条引用，可配置连接符
- 行号范围分隔符、提示位置（状态栏 / 信息提示 / 关闭）、是否强制 POSIX 分隔符可配
- 编辑器右键二级菜单聚合所有格式
- 命令标题、配置说明支持中英文（VS Code NLS）
- 对未保存 / 非本地文件给出明确提示

## 0.0.1

- 初始版本：`@path:line` 单一命令 + `Cmd/Ctrl+Alt+C` 快捷键 + 编辑器/标签/资源管理器右键
