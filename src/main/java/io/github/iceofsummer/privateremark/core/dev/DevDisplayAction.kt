package io.github.iceofsummer.privateremark.core.dev

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import io.github.iceofsummer.privateremark.ui.InvalidRemarkToolWindow
import java.util.function.Supplier
import git4idea.repo.GitRepositoryManager;

class DevDisplayAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun actionPerformed(e: AnActionEvent) {
//        val toolWindowManager = ToolWindowManager.getInstance(e.project ?: return)
//        val toolWindow = toolWindowManager.registerToolWindow("Invalid", {
//            stripeTitle = Supplier<String> { "test" };
//        })
//
//        val contentManager = toolWindow.contentManager
//        val createContent = contentManager.factory.createContent(InvalidRemarkToolWindow(), "TestTab", true)
//        contentManager.addContent(createContent)
    }

    override fun update(e: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}