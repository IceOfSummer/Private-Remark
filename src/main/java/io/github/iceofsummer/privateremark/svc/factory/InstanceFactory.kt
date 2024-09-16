package io.github.iceofsummer.privateremark.svc.factory

interface InstanceFactory<T> {

    /**
     * 获取对应的实例
     */
    fun createInstance(): T

    /**
     * 是否可以缓存实例
     */
    fun isCacheDisabled(): Boolean {
        return false
    }

    /**
     * 是否应该刷新缓存. 当需要变动实现类时，应该返回 true 以替换对应的实现类.
     */
    fun shouldRefreshCache(): Boolean {
        return false
    }

}