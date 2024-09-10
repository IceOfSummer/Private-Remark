package io.github.iceofsummer.privateremark.core.dev

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl

class DevDisplayAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun actionPerformed(e: AnActionEvent) {
        println(InMemoryRemarkServiceImpl.remarksMap)
        println(InMemoryRemarkServiceImpl.invalidRemarks)
    }

    override fun update(e: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}