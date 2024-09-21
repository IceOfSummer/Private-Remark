package io.github.iceofsummer.privateremark.bridge

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import javax.swing.JComponent
import javax.swing.JLabel

/**
 * 创建一个 ToolWindow，显示所有失效的备注。
 */
class InvalidRemarkToolWindowFactory : ToolWindowFactory {


    private fun createContent() : JComponent {
        val toolWindow = SimpleToolWindowPanel(false)

        val splitter = Splitter(false, 0.3f, 0.05f, 0.95f).apply {
            firstComponent = JBScrollPane(JLabel("<a>Load file for vcs2</a>"))
            secondComponent = JBScrollPane(JLabel("<a>Load file for vcs2</a>"))
        }
        toolWindow.setContent(splitter)
        return toolWindow
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindowPanel = createContent()
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(myToolWindowPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

}