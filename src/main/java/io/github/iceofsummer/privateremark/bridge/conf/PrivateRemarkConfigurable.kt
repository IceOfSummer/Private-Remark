package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import io.github.iceofsummer.privateremark.bean.PrivateRemarkProject
import io.github.iceofsummer.privateremark.bean.PrivateRemarkProjectRootConfig
import io.github.iceofsummer.privateremark.svc.PrivateRemarkProjectService
import io.github.iceofsummer.privateremark.svc.factory.ServiceManager
import javax.swing.JComponent

class PrivateRemarkConfigurable : Configurable {

    private var lastUI: PrivateRemarkSettingsUI? = null

    private val privateRemarkProjectService: PrivateRemarkProjectService = ServiceManager.getService(PrivateRemarkProjectService::class)

    companion object {
        const val PROJECT_CONFIG = "config.json"
    }

    private fun loadProjects(rootDirectory: String): MutableList<PrivateRemarkProject> {
        val config = privateRemarkProjectService.readConfig(rootDirectory) ?: return mutableListOf()
        return config.privateRemarkProject
    }

    private fun saveProjects(rootDirectory: String, projects: MutableList<PrivateRemarkProject>) {
        privateRemarkProjectService.saveConfig(rootDirectory, PrivateRemarkProjectRootConfig(projects))
    }


    override fun createComponent(): JComponent {
        val settingsUI = PrivateRemarkSettingsUI(::loadProjects)
        lastUI = settingsUI
        return settingsUI.component()
    }



    override fun isModified(): Boolean {
        val state = lastUI ?: return false
        return state.isModified()
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
        privateRemarkProjectService.saveConfig(managedSettings.localFileSystemDirectory, PrivateRemarkProjectRootConfig(state.projects))
        state.resetModifiedState()
    }

    override fun getDisplayName(): String {
        return "Private Remark Settings"
    }

    override fun disposeUIResources() {
        lastUI = null
    }


}

