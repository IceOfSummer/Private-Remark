package io.github.iceofsummer.privateremark.svc

import io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl
import kotlin.reflect.KClass

@Deprecated("使用 Light Service.")
object ServiceFactory {


    private val serviceHolder = mutableMapOf<KClass<*>, Any>()

    init {
        serviceHolder[RemarkService::class] = InMemoryRemarkServiceImpl;
    }

    /**
     * 获取服务
     */
    @Deprecated(
        "使用 Light Service.",
        replaceWith = ReplaceWith("service<clazz>()", "com.intellij.openapi.components.service")
    )
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getService(clazz: KClass<T>): T {
        val svc = serviceHolder[clazz] ?: throw NoSuchElementException("No such service: $clazz")
        return svc as T
    }

}