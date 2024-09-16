package io.github.iceofsummer.privateremark.bridge.conf

import io.github.iceofsummer.privateremark.bridge.bean.IDEManagedSettings

data class SettingState (
    var managedSettings: IDEManagedSettings,
    var dbStatus: DBStatus
) {
    enum class DBStatus {
        // 运行中
        RUNNING,
        // 没有已经创建的数据库
        NONE,
        // 已经存在创建的数据库
        EXIST
    }
}