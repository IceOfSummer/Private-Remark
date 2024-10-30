package io.github.iceofsummer.privateremark.svc.factory

import io.github.iceofsummer.privateremark.util.ObjectReference

/**
 * 动态替换实现类的服务。
 */
class DynamicServiceHolder(
    /**
     * 具体实现的引用
     */
    private var reference: ObjectReference,
    /**
     * 服务的代理类
     * @see ServiceEnhanceInvocationHandler
     */
    private var proxiedObject: Any
) : ServiceHolder {

    override fun getInstance(): Any {
        return proxiedObject
    }

    override fun setInstance(value: Any) {
        reference.value = value
    }


}
