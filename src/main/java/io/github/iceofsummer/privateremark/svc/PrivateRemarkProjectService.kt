package io.github.iceofsummer.privateremark.svc

import io.github.iceofsummer.privateremark.bean.PrivateRemarkProjectRootConfig

interface PrivateRemarkProjectService {

    /**
     * 加载配置
     * @param directoryPath 目录位置
     */
    fun readConfig(directoryPath: String): PrivateRemarkProjectRootConfig?

    /**
     * 保存配置
     * @param directoryPath 目录位置
     * @param config 配置
     */
    fun saveConfig(directoryPath: String, config: PrivateRemarkProjectRootConfig)

    /**
     * 应用设置
     */
    fun applyConfig(config: PrivateRemarkProjectRootConfig)

}