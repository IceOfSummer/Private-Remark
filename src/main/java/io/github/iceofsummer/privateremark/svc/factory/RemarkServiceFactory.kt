package io.github.iceofsummer.privateremark.svc.factory

import com.intellij.openapi.components.service
import io.github.iceofsummer.privateremark.bridge.bean.PersistenceType
import io.github.iceofsummer.privateremark.bridge.conf.IDESettingsStorage
import io.github.iceofsummer.privateremark.mapper.DatasourceManager
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import io.github.iceofsummer.privateremark.svc.impl.LocalStorageRemarkServiceImpl

class RemarkServiceFactory : InstanceFactory<RemarkServiceV2> {

    private var lastState: PersistenceType? = null

    override fun createInstance(): RemarkServiceV2 {
        val ideSettingsStorage = service<IDESettingsStorage>()

        val type = ideSettingsStorage.state.persistenceType

        lastState = type
        return when (type) {
            PersistenceType.NONE -> TODO("Show dialog to tip user switch the type.")
            PersistenceType.IN_MEMORY -> io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl()
            PersistenceType.LOCAL_FILE_SYSTEM -> LocalStorageRemarkServiceImpl(DatasourceManager.getSqlSessionFactory(ideSettingsStorage.state.localFileSystemDirectory))
            else -> TODO("Unknown type")
        }
    }

    override fun shouldRefreshCache(): Boolean {
        val ideSettingsStorage = service<IDESettingsStorage>()
        return ideSettingsStorage.state.persistenceType == lastState
    }

}