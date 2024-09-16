package io.github.iceofsummer.privateremark.bridge.bean


data class IDEManagedSettings(
    /**
     * 持久化类型
     */
    var persistenceType: PersistenceType = PersistenceType.NONE,
    /**
     * 本地文件系统文件夹路径
     */
    var localFileSystemDirectory: String = ""
)
