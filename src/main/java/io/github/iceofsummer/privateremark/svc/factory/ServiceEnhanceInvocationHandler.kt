package io.github.iceofsummer.privateremark.svc.factory

import io.github.iceofsummer.privateremark.mapper.common.SqlSessionThreadLocalManager
import io.github.iceofsummer.privateremark.mapper.transaction.Transactional
import io.github.iceofsummer.privateremark.util.ObjectReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * 服务增强. 添加事务管理和动态切换实现类.
 */
class ServiceEnhanceInvocationHandler(private val serviceReference: ObjectReference) : InvocationHandler {

    private lateinit var transactionalMethod: Set<String>

    private var lastClass: Class<*> = serviceReference.value.javaClass

    init {
        flush(serviceReference.value.javaClass)
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any {
        if (lastClass != serviceReference.value.javaClass) {
            flush(serviceReference.value.javaClass)
        }
        var newTransaction = false
        if (transactionalMethod.contains(methodToHash(method))) {
            if (!SqlSessionThreadLocalManager.isTransactionEnabled()) {
                SqlSessionThreadLocalManager.enableTransaction()
                newTransaction = true
            }
        }
        var isExceptionThrow = false

        try {
            return method.invoke(serviceReference.value, *args)
        } catch (e: Exception) {
            isExceptionThrow = true
            throw e
        } finally {
            SqlSessionThreadLocalManager.getSqlSessionHolder()?.let {
                if (it.refCount == 0) {
                    SqlSessionThreadLocalManager.clearBind()
                    try {
                        if (isExceptionThrow) {
                            it.sqlSession.rollback()
                        } else {
                            it.sqlSession.commit()
                        }
                    } finally {
                        it.sqlSession.close()
                    }
                }
            } ?: let {
                // 避免开了事务但实际没有执行数据库操作.
                if (newTransaction) {
                    SqlSessionThreadLocalManager.clearBind()
                }
            }
        }
    }


    private fun flush(clazz: Class<Any>) {
        val set = mutableSetOf<String>()
        for (method in clazz.methods) {
            val annotation = method.getAnnotation(Transactional::class.java)
            if (annotation != null) {
                set.add(methodToHash(method))
            }
        }
        this.transactionalMethod = set
    }

    private fun methodToHash(method: Method): String {
        val builder = StringBuilder()
        builder.append(method.name).append('(')
        for (parameterType in method.parameterTypes) {
            builder.append(parameterType.name).append(',')
        }
        builder.append(')')
        return builder.toString()
    }
}