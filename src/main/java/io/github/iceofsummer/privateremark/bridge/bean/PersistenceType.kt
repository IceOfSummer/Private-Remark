package io.github.iceofsummer.privateremark.bridge.bean

enum class PersistenceType {
    IN_MEMORY,
    LOCAL_FILE_SYSTEM,
    /**
     * 无持久化. 用于防止用户第一次使用时忘了持久化设置，
     */
    NONE
}