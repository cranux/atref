package com.atref

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

class AtRefSettingsConfigurable :
    BoundConfigurable(AtRefBundle.message("settings.title")) {

    override fun createPanel(): DialogPanel {
        val s = AtRefSettings.instance
        return panel {
            group(AtRefBundle.message("group.format")) {
                row(AtRefBundle.message("cfg.defaultFormat")) {
                    comboBox(listOf("at", "markdown", "colon", "withCode", "absolute"))
                        .bindItem({ s.defaultFormat }, { s.defaultFormat = it ?: "at" })
                }.rowComment(AtRefBundle.message("cfg.defaultFormat.comment"))

                row(AtRefBundle.message("cfg.rangeSeparator")) {
                    textField()
                        .bindText({ s.rangeSeparator }, { s.rangeSeparator = it })
                }.rowComment(AtRefBundle.message("cfg.rangeSeparator.comment"))

                row(AtRefBundle.message("cfg.multiSelectionSeparator")) {
                    textField()
                        .bindText({ s.multiSelectionSeparator }, { s.multiSelectionSeparator = it })
                }.rowComment(AtRefBundle.message("cfg.multiSelectionSeparator.comment"))
            }

            group(AtRefBundle.message("group.path")) {
                row(AtRefBundle.message("cfg.pathStyle")) {
                    comboBox(listOf("workspace", "git", "absolute", "basename"))
                        .bindItem({ s.pathStyle }, { s.pathStyle = it ?: "workspace" })
                }.rowComment(AtRefBundle.message("cfg.pathStyle.comment"))

                row {
                    checkBox(AtRefBundle.message("cfg.usePosixSeparators"))
                        .bindSelected({ s.usePosixSeparators }, { s.usePosixSeparators = it })
                }
            }

            group(AtRefBundle.message("group.codeBlock")) {
                row {
                    checkBox(AtRefBundle.message("cfg.codeBlockLineNumbers"))
                        .bindSelected({ s.codeBlockLineNumbers }, { s.codeBlockLineNumbers = it })
                }
                row(AtRefBundle.message("cfg.codeBlockContextLines")) {
                    intTextField(0..10_000)
                        .bindIntText({ s.codeBlockContextLines }, { s.codeBlockContextLines = it })
                }
            }

            group(AtRefBundle.message("cfg.notification")) {
                row {
                    comboBox(listOf("statusBar", "info", "none"))
                        .bindItem({ s.notification }, { s.notification = it ?: "statusBar" })
                }
            }
        }
    }
}
