package io.github.iceofsummer.privateremark.svc.factory

interface ServiceHolder {

    /**
     * 获取对应的实例
     */
    fun getInstance(): Any

    /**
     * 修改当前使用的实例(如果可以)
     */
    fun setInstance(value: Any) {
        // do nothing
    }

}