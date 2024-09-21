package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "io.github.iceofsummer.privateremark.bridge.conf.PrivateRemarkSettings",
    storages = [ Storage("SdkSettingsPlugin.xml") ]
)
@Service(Service.Level.APP)
class PrivateRemarkSettings : PersistentStateComponent<PrivateRemarkState> {

    private var myState = PrivateRemarkState()

    override fun getState(): PrivateRemarkState {
        return myState
    }

    override fun loadState(state: PrivateRemarkState) {
        this.myState = state
    }

}