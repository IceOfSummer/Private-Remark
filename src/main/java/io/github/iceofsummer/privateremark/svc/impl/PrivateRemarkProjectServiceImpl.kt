package io.github.iceofsummer.privateremark.svc.impl

import io.github.iceofsummer.privateremark.bean.PrivateRemarkProjectRootConfig
import io.github.iceofsummer.privateremark.svc.PrivateRemarkProjectService
import io.github.iceofsummer.privateremark.util.JSON
import java.io.File

class PrivateRemarkProjectServiceImpl : PrivateRemarkProjectService {

    companion object {
        const val CONFIG_FILE_NAME = "private-remark-config.json"
    }

    override fun readConfig(directoryPath: String): PrivateRemarkProjectRootConfig? {
        val file = File("$directoryPath/$CONFIG_FILE_NAME")
        if (!file.exists()) {
            return null
        }
        val text = file.readText()
        return JSON.gson.fromJson(text, PrivateRemarkProjectRootConfig::class.java)
    }

    override fun saveConfig(directoryPath: String, config: PrivateRemarkProjectRootConfig) {
        val file = File("$directoryPath/$CONFIG_FILE_NAME")
        file.writeText(JSON.gson.toJson(config))
    }

    override fun applyConfig(config: PrivateRemarkProjectRootConfig) {
        TODO("Not yet implemented")
    }

}