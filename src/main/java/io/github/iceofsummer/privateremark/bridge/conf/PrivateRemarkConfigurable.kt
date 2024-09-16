package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PrivateRemarkConfigurable : Configurable {

    private var lastUI: PrivateRemarkSettingsUI? = null


    override fun createComponent(): JComponent {
        val settingsUI = PrivateRemarkSettingsUI()
        lastUI = settingsUI
        return settingsUI.component()
    }

    override fun isModified(): Boolean {
        val state = lastUI ?: return false
        val panel = state.component()
        val modified = panel.isModified()
        if (modified) {
            return panel.validateAll().isEmpty()
        }
        return false
    }

    override fun apply() {
        val state = lastUI ?: return
        state.component().apply()

        val remarkSettings = service<IDESettingsStorage>()

        val persistenceState = remarkSettings.state
        val settings = state.settingState

        val managedSettings = settings.managedSettings

        persistenceState.persistenceType = managedSettings.persistenceType
        persistenceState.localFileSystemDirectory = managedSettings.localFileSystemDirectory
    }

    override fun getDisplayName(): String {
        return "Private Remark Settings"
    }

    override fun disposeUIResources() {
        lastUI = null
    }


}

