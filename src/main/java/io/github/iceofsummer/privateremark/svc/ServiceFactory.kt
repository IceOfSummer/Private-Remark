package io.github.iceofsummer.privateremark.svc

import io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl
import kotlin.reflect.KClass

object ServiceFactory {


    private val serviceHolder = mutableMapOf<KClass<*>, Any>()

    init {
        serviceHolder[RemarkService::class] = InMemoryRemarkServiceImpl;
    }

    /**
     * 获取服务
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getService(clazz: KClass<T>): T {
        val svc = serviceHolder[clazz] ?: throw NoSuchElementException("No such service: $clazz")
        return svc as T
    }

}