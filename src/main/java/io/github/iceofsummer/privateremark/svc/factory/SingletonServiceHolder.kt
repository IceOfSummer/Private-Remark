package io.github.iceofsummer.privateremark.svc.factory

class SingletonServiceHolder(private val value: Any) : ServiceHolder {
    override fun getInstance(): Any {
        return value
    }
}