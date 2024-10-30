package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.iceofsummer.privateremark.bean.PrivateRemarkProject
import javax.swing.JComponent

class CreateOrUpdateProjectDialogUI(private val onOk: (PrivateRemarkProject) -> Unit) : DialogWrapper(false) {

    init {
        title = "Create Or Update Project"
        init()
    }

    private lateinit var state: PrivateRemarkProject

    override fun createCenterPanel(): JComponent {
        state = PrivateRemarkProject("", "")
        return panel {
            row("Project Name") {
                textField().bindText(state::name)
            }
            row("Description") {
                textField().bindText(state::description)
            }
        }
    }


    override fun doOKAction() {
        onOk(state)
        super.doOKAction()
    }

}