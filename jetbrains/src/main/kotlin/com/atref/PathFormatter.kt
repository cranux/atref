package com.atref

import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

enum class PathStyle { WORKSPACE, GIT, ABSOLUTE, BASENAME }
enum class FormatKind { AT, MARKDOWN, COLON, WITH_CODE, ABSOLUTE }

data class LineSpan(val start: Int, val end: Int) // 1-based inclusive

object PathFormatter {

    // ----- path -----

    fun computePath(file: VirtualFile, project: Project?, style: PathStyle): String {
        val fsPath = file.path
        return when (style) {
            PathStyle.ABSOLUTE -> fsPath
            PathStyle.BASENAME -> file.name
            PathStyle.GIT -> findGitRoot(File(fsPath))?.let { root ->
                relativize(fsPath, root.path)
            } ?: workspaceRelative(file, project)
            PathStyle.WORKSPACE -> workspaceRelative(file, project)
        }
    }

    private fun workspaceRelative(file: VirtualFile, project: Project?): String {
        val base = project?.guessProjectDir()?.path ?: return file.name
        return relativize(file.path, base)
    }

    private fun relativize(absolutePath: String, basePath: String): String {
        val rel = File(basePath).toPath().relativize(File(absolutePath).toPath())
        return rel.toString()
    }

    private fun findGitRoot(start: File): File? {
        var dir: File? = if (start.isDirectory) start else start.parentFile
        var depth = 0
        while (depth < 64) {
            val d = dir ?: return null
            if (File(d, ".git").exists()) return d
            dir = d.parentFile
            depth++
        }
        return null
    }

    fun toPosix(p: String): String = p.replace(File.separatorChar, '/')

    // ----- selection -----

    fun spanFromCaret(caret: Caret, editor: Editor): LineSpan {
        if (!caret.hasSelection()) {
            val line = caret.logicalPosition.line + 1
            return LineSpan(line, line)
        }
        val doc = editor.document
        val startLine = doc.getLineNumber(caret.selectionStart) + 1
        var endLine = doc.getLineNumber(caret.selectionEnd) + 1
        val endLineStartOffset = doc.getLineStartOffset(endLine - 1)
        if (caret.selectionEnd == endLineStartOffset && endLine > startLine) {
            endLine -= 1
        }
        return LineSpan(startLine, endLine)
    }

    // ----- format -----

    fun format(
        kind: FormatKind,
        pathStr: String,
        span: LineSpan?,
        editor: Editor?,
        caret: Caret?,
    ): String {
        val s = AtRefSettings.instance
        val rangeSep = s.rangeSeparator
        return when (kind) {
            FormatKind.AT -> formatAt(pathStr, span, rangeSep)
            FormatKind.COLON, FormatKind.ABSOLUTE -> formatColon(pathStr, span, rangeSep)
            FormatKind.MARKDOWN -> formatMarkdown(pathStr, span, rangeSep)
            FormatKind.WITH_CODE -> formatWithCode(pathStr, span, rangeSep, editor, caret)
        }
    }

    private fun lineSuffix(span: LineSpan?, rangeSep: String): String {
        if (span == null) return ""
        return if (span.start == span.end) ":${span.start}" else ":${span.start}${rangeSep}${span.end}"
    }

    private fun formatAt(p: String, span: LineSpan?, rangeSep: String) =
        "@$p${lineSuffix(span, rangeSep)}"

    private fun formatColon(p: String, span: LineSpan?, rangeSep: String) =
        "$p${lineSuffix(span, rangeSep)}"

    private fun formatMarkdown(p: String, span: LineSpan?, rangeSep: String): String {
        if (span == null) return "[$p]($p)"
        val label = "$p${lineSuffix(span, rangeSep)}"
        val hash = if (span.start == span.end) "#L${span.start}" else "#L${span.start}-L${span.end}"
        return "[$label]($p$hash)"
    }

    private fun formatWithCode(
        pathStr: String,
        span: LineSpan?,
        rangeSep: String,
        editor: Editor?,
        @Suppress("UNUSED_PARAMETER") caret: Caret?,
    ): String {
        val ref = formatAt(pathStr, span, rangeSep)
        if (span == null || editor == null) return ref

        val settings = AtRefSettings.instance
        val withNumbers = settings.codeBlockLineNumbers
        val ctx = maxOf(0, settings.codeBlockContextLines)

        val doc = editor.document
        var blockStartLine = span.start - 1 // 0-based
        var blockEndLine = span.end - 1
        if (ctx > 0) {
            blockStartLine = maxOf(0, blockStartLine - ctx)
            blockEndLine = minOf(doc.lineCount - 1, blockEndLine + ctx)
        }

        val startOffset = doc.getLineStartOffset(blockStartLine)
        val endOffset = doc.getLineEndOffset(blockEndLine)
        var text = doc.getText(TextRange(startOffset, endOffset))

        if (withNumbers) {
            text = withLineNumbers(text, blockStartLine + 1)
        }

        val lang = resolveLang(editor)
        return "$ref\n```$lang\n$text\n```"
    }

    private fun withLineNumbers(text: String, startLine: Int): String {
        val lines = text.split("\n")
        val lastLine = startLine + lines.size - 1
        val width = lastLine.toString().length
        return lines.mapIndexed { i, line ->
            "${(startLine + i).toString().padStart(width, ' ')}  $line"
        }.joinToString("\n")
    }

    private fun resolveLang(editor: Editor): String {
        val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return ""
        val ext = "." + (file.extension?.lowercase() ?: "")
        EXT_TO_LANG[ext]?.let { return it }
        // Fallback: IntelliJ file type name, lowercased and cleaned.
        val typeName = file.fileType.name.lowercase()
        return typeName.replace(" ", "").replace("plain text", "")
    }

    private val EXT_TO_LANG = mapOf(
        ".py" to "python",
        ".ts" to "typescript", ".tsx" to "tsx", ".mts" to "typescript", ".cts" to "typescript",
        ".js" to "javascript", ".jsx" to "jsx", ".mjs" to "javascript", ".cjs" to "javascript",
        ".go" to "go",
        ".rs" to "rust",
        ".java" to "java", ".kt" to "kotlin", ".kts" to "kotlin",
        ".swift" to "swift",
        ".rb" to "ruby",
        ".php" to "php",
        ".cs" to "csharp", ".fs" to "fsharp",
        ".c" to "c", ".h" to "c",
        ".cpp" to "cpp", ".cc" to "cpp", ".cxx" to "cpp", ".hpp" to "cpp", ".hxx" to "cpp",
        ".m" to "objective-c", ".mm" to "objective-cpp",
        ".sh" to "bash", ".bash" to "bash", ".zsh" to "zsh", ".fish" to "fish",
        ".ps1" to "powershell",
        ".lua" to "lua",
        ".r" to "r",
        ".md" to "markdown", ".mdx" to "mdx",
        ".json" to "json", ".json5" to "json5", ".jsonc" to "jsonc",
        ".yaml" to "yaml", ".yml" to "yaml",
        ".toml" to "toml",
        ".xml" to "xml",
        ".html" to "html", ".htm" to "html",
        ".css" to "css", ".scss" to "scss", ".sass" to "sass", ".less" to "less",
        ".vue" to "vue",
        ".svelte" to "svelte",
        ".sql" to "sql",
        ".dockerfile" to "dockerfile",
        ".tf" to "terraform",
        ".proto" to "protobuf",
        ".graphql" to "graphql", ".gql" to "graphql",
    )
}
