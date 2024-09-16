package io.github.iceofsummer.privateremark.svc

import ai.grazie.utils.WeakHashMap
import io.github.iceofsummer.privateremark.svc.factory.InstanceFactory
import io.github.iceofsummer.privateremark.svc.factory.MultiImplementOnRuntime
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

private typealias InstanceCacheType = Pair<InstanceFactory<*>, Any?>

@Deprecated("使用 Light Service.")
object ServiceFactory {


    private val singleImplServiceHolder = mutableMapOf<KClass<*>, Any>()

    /**
     * k: factory
     * v: last instance
     */
    private val instanceCache = WeakHashMap<KClass<*>, InstanceCacheType>()


//    init {
//        singleImplServiceHolder[RemarkService::class] = InMemoryRemarkServiceImpl;
//    }

    /**
     * 获取服务
     */
    @Deprecated(
        "使用 Light Service.",
        replaceWith = ReplaceWith("service<clazz>()", "com.intellij.openapi.components.service")
    )
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getService(clazz: KClass<T>): T {
        val cached = tryGetFromCache(clazz)
        if (cached != null) {
            return cached as T
        }

        var annotation: MultiImplementOnRuntime? = null

        for (anno in clazz.annotations) {
            if (anno is MultiImplementOnRuntime) {
                annotation = anno
                break
            }
        }
        if (annotation == null) {
            val single = singleImplServiceHolder[clazz] ?: throw IllegalArgumentException("No such service: $clazz")
            return single as T
        }
        return initCache(clazz, annotation) as T
    }

    /**
     * 初始化缓存并返回创建的实例
     */
    private fun initCache(clazz: KClass<*>, multiImplementOnRuntime: MultiImplementOnRuntime): Any? {
        val factory = multiImplementOnRuntime.value.createInstance()
        if (factory.isCacheDisabled()) {
            instanceCache[clazz] = Pair(factory, null)
            return factory.createInstance()
        }
        val instance = factory.createInstance()
        instanceCache[clazz] = Pair(factory, instance)
        return instance
    }

    private fun tryGetFromCache(clazz: KClass<*>): Any? {
        val pair = instanceCache[clazz] ?: return null
        val factory = pair.first
        if (factory.isCacheDisabled()) {
            return factory.createInstance()
        }
        if (factory.shouldRefreshCache()) {
            val instance = factory.createInstance()
            instanceCache[clazz] = Pair(factory, instance)
            return instance
        }
        return pair.second
    }
}