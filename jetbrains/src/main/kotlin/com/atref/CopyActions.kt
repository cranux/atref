package com.atref

import com.intellij.ide.CopyPasteManager
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.awt.datatransfer.StringSelection

abstract class CopyActionBase(
    private val kindResolver: () -> FormatKind,
) : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = resolveFile(e) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = resolveFile(e) ?: return
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val kind = kindResolver()

        val refs = buildReferences(kind, file, project, editor)
        if (refs.isEmpty()) return

        val settings = AtRefSettings.instance
        val joiner = settings.multiSelectionSeparator.replace("\\n", "\n").replace("\\t", "\t")
        val text = refs.joinToString(joiner)

        CopyPasteManager.getInstance().setContents(StringSelection(text))

        notifyCopied(project, refs)
    }

    private fun resolveFile(e: AnActionEvent): VirtualFile? {
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor != null) {
            FileDocumentManager.getInstance().getFile(editor.document)?.let { return it }
        }
        val explicit = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (explicit != null && !explicit.isDirectory) return explicit
        val multi = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (multi != null) {
            multi.firstOrNull { !it.isDirectory }?.let { return it }
        }
        return null
    }

    private fun buildReferences(
        kind: FormatKind,
        file: VirtualFile,
        project: Project?,
        editor: com.intellij.openapi.editor.Editor?,
    ): List<String> {
        val settings = AtRefSettings.instance
        val style = if (kind == FormatKind.ABSOLUTE) {
            PathStyle.ABSOLUTE
        } else {
            runCatching { PathStyle.valueOf(settings.pathStyle.uppercase()) }
                .getOrDefault(PathStyle.WORKSPACE)
        }

        var pathStr = PathFormatter.computePath(file, project, style)
        if (settings.usePosixSeparators) pathStr = PathFormatter.toPosix(pathStr)

        if (editor == null) {
            return listOf(PathFormatter.format(kind, pathStr, null, null, null))
        }

        val carets = editor.caretModel.allCarets
        if (carets.isEmpty()) {
            return listOf(PathFormatter.format(kind, pathStr, null, editor, null))
        }

        val seen = LinkedHashSet<String>()
        for (caret in carets) {
            val span = PathFormatter.spanFromCaret(caret, editor)
            seen.add(PathFormatter.format(kind, pathStr, span, editor, caret))
        }
        return seen.toList()
    }

    private fun notifyCopied(project: Project?, refs: List<String>) {
        val settings = AtRefSettings.instance
        val preview = if (refs.size > 1) "${refs[0]}  (+${refs.size - 1} more)" else refs[0]
        val message = AtRefBundle.message("notify.copied", preview)
        when (settings.notification) {
            "info" -> NotificationGroupManager.getInstance()
                .getNotificationGroup("AtRef")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project)
            "statusBar" -> if (project != null) {
                WindowManager.getInstance().getStatusBar(project)?.info = message
            }
            // "none" — do nothing
        }
    }
}

class CopyDefaultAction : CopyActionBase({
    when (AtRefSettings.instance.defaultFormat) {
        "markdown" -> FormatKind.MARKDOWN
        "colon" -> FormatKind.COLON
        "withCode" -> FormatKind.WITH_CODE
        "absolute" -> FormatKind.ABSOLUTE
        else -> FormatKind.AT
    }
})

class CopyAtRefAction : CopyActionBase({ FormatKind.AT })
class CopyColonAction : CopyActionBase({ FormatKind.COLON })
class CopyMarkdownLinkAction : CopyActionBase({ FormatKind.MARKDOWN })
class CopyAbsolutePathAction : CopyActionBase({ FormatKind.ABSOLUTE })
class CopyWithCodeAction : CopyActionBase({ FormatKind.WITH_CODE })
