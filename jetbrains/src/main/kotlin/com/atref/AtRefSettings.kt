package com.atref

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "AtRefSettings",
    storages = [Storage("atref.xml")]
)
@Service(Service.Level.APP)
class AtRefSettings : PersistentStateComponent<AtRefSettings> {

    // at | markdown | colon | withCode | absolute
    var defaultFormat: String = "at"

    // workspace | git | absolute | basename
    var pathStyle: String = "workspace"

    var usePosixSeparators: Boolean = true

    var rangeSeparator: String = "-"

    // Stored literally; "\n" expands to newline at use site.
    var multiSelectionSeparator: String = "\\n"

    // statusBar | info | none
    var notification: String = "statusBar"

    var codeBlockLineNumbers: Boolean = true
    var codeBlockContextLines: Int = 0

    override fun getState(): AtRefSettings = this
    override fun loadState(state: AtRefSettings) = XmlSerializerUtil.copyBean(state, this)

    companion object {
        val instance: AtRefSettings
            get() = ApplicationManager.getApplication().getService(AtRefSettings::class.java)
    }
}
