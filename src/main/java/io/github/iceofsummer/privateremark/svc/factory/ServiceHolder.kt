package io.github.iceofsummer.privateremark.svc.factory

import io.github.iceofsummer.privateremark.util.ObjectReference

/**
 * 服务的缓存
 */
data class ServiceHolder(
    /**
     * 具体实现的引用
     */
    var reference: ObjectReference,
    /**
     * 服务的代理类
     */
    var proxiedObject: Any
)
