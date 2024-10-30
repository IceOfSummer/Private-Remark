package io.github.iceofsummer.privateremark.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager

object IdeUtils {

    /**
     * 尝试获取项目
     */
    fun tryGetProject(): Project? {
        for (project in ProjectManager.getInstance().openProjects) {
            if (!project.isDisposed) {
               return project
            }
        }
        return null
    }

}