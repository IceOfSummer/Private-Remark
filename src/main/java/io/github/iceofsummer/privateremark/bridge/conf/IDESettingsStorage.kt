package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import io.github.iceofsummer.privateremark.bridge.bean.IDEManagedSettings

@State(
    name = "io.github.iceofsummer.privateremark.bridge.conf.PrivateRemarkSettings",
    storages = [ Storage("PrivateRemarkSettings.xml") ]
)
@Service(Service.Level.APP)
class IDESettingsStorage : PersistentStateComponent<IDEManagedSettings> {

    private var myState = IDEManagedSettings()

    override fun getState(): IDEManagedSettings {
        return myState
    }

    override fun loadState(state: IDEManagedSettings) {
        this.myState = state
    }

}