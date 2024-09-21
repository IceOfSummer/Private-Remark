package io.github.iceofsummer.privateremark.bridge.conf

data class PrivateRemarkState(
    /**
     * 持久化类型
     */
    var persistenceType: PersistenceType = PersistenceType.NONE,
    /**
     * 本地文件系统文件夹路径
     */
    var localFileSystemDirectory: String = ""
)

enum class PersistenceType {
    IN_MEMORY,
    LOCAL_FILE_SYSTEM,
    /**
     * 无持久化. 用于防止用户第一次使用时忘了持久化设置，
     */
    NONE
}