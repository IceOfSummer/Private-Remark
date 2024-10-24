package io.github.iceofsummer.privateremark.bridge

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import io.github.iceofsummer.privateremark.bean.dto.RemarkHolderDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkInsertDTO
import io.github.iceofsummer.privateremark.core.RemarkInlayCoordinator
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import io.github.iceofsummer.privateremark.ui.EditRemarkPopup
import io.github.iceofsummer.privateremark.util.RemarkUtils
import java.awt.event.ActionEvent

/**
 * 处理右键添加备注事件
 */
class AddPrivateRemarkAction : AnAction() {

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
            val remarkService = ServiceFactory.getService(RemarkServiceV2::class)

            val lineNumber = editor.document.getLineNumber(editor.caretModel.offset)
            val dto = RemarkInsertDTO(
                RemarkUtils.generateRemark(
                    editRemarkPopup.remark.text,
                    lineNumber,
                    editor
                ),
                null,
                RemarkUtils.tryResolveRemarkHolder(editor, lineNumber)?.let { RemarkHolderDTO(it) }
            )
            remarkService.saveRemark(dto)
            RemarkInlayCoordinator.displayRemark(editor, editor.document, dto.remark)
            balloon.hide()
        }

        balloon.show(instance.guessBestPopupLocation(editor), Balloon.Position.below)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }


}