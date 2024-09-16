package io.github.iceofsummer.privateremark.svc.factory

import kotlin.reflect.KClass

/**
 * 指示当前类有多个实现，并且具体要使用哪个类必须在运行时确定.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MultiImplementOnRuntime(
    /**
     *  获取对应实例的工厂.
     */
    val value: KClass<out InstanceFactory<*>>
)
