package io.github.iceofsummer.privateremark.mapper.transaction

/**
 * 标记某个方法需要开启事务
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Transactional
