package io.github.iceofsummer.privateremark.bridge

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import io.github.iceofsummer.privateremark.ui.InvalidRemarkToolWindow
import java.util.function.Function
import java.util.function.Supplier
import javax.swing.JComponent

class EditorNotificationProviderImpl : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?>? {
        val service = ServiceFactory.getService(RemarkService::class)
        val remarks = service.resolveInvalidRemarks(file)

        if (remarks.isEmpty()) {
            return null
        }
        return Function<FileEditor, JComponent?> { t ->
            EditorNotificationPanel(t, EditorNotificationPanel.Status.Warning).apply {
                text = "部分备注已经失效，需要手动进行修复"
                createActionLabel("修复", {
                    val toolWindowManager = ToolWindowManager.getInstance(project)
                    // TODO: 统一管理 toolwindow
                    val toolWindow = toolWindowManager.registerToolWindow("Invalid", {
                        stripeTitle = Supplier<String> { "test" };
                    })

                    val contentManager = toolWindow.contentManager
                    val createContent =
                        contentManager.factory.createContent(InvalidRemarkToolWindow(t.file), "TestTab", true)
                    contentManager.addContent(createContent)

                    toolWindow.show()
                })
            }
        }
    }

}