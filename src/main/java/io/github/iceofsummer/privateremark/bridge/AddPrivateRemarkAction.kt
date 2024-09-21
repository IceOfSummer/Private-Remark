package io.github.iceofsummer.privateremark.bridge

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import io.github.iceofsummer.privateremark.core.RemarkInlayCoordinator
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import io.github.iceofsummer.privateremark.ui.EditRemarkPopup
import java.awt.event.ActionEvent

/**
 * 处理右键添加备注事件
 */
class AddPrivateRemarkAction : AnAction() {

    private val remarkService: RemarkService = ServiceFactory.getService(RemarkService::class)

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor =
            CommonDataKeys.EDITOR.getData(event.getDataContext()) ?: return
        val editRemarkPopup = EditRemarkPopup()
        val instance = JBPopupFactory.getInstance()
        val balloon = instance.createDialogBalloonBuilder(editRemarkPopup.root, "Add Private Remark")
            .setCloseButtonEnabled(true)
            .setHideOnClickOutside(true)
            .setHideOnCloseClick(true)
            .createBalloon()

        editRemarkPopup.cancelBtn.addActionListener { _: ActionEvent? ->
            balloon.hide()
        }

        editRemarkPopup.saveBtn.addActionListener { _: ActionEvent? ->
            val created = remarkService.saveRemark(
                editRemarkPopup.remark.text,
                editor,
                event.getData(CommonDataKeys.PSI_FILE)
            )
            RemarkInlayCoordinator.displayRemark(editor, created, editor.document)
            balloon.hide()
        }

        balloon.show(instance.guessBestPopupLocation(editor), Balloon.Position.below)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }


}