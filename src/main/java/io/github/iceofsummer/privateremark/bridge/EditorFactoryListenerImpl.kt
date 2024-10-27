package io.github.iceofsummer.privateremark.bridge

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import io.github.iceofsummer.privateremark.core.RemarkInlayCoordinator
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import io.github.iceofsummer.privateremark.svc.factory.ServiceManager
import io.github.iceofsummer.privateremark.util.RemarkUtils

/**
 * 监听编辑器打开和关闭事件，当打开时显示所有备注，关闭时保存最后的位置。
 */
class EditorFactoryListenerImpl : EditorFactoryListener {

    private val remarkService = ServiceManager.getService(RemarkServiceV2::class)


    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val file = editor.virtualFile
        val remarks = remarkService.resolveAllValidRemarks(RemarkUtils.toRelativePath(editor.project, file.path))
        if (remarks.isEmpty()) {
            return
        }

        RemarkInlayCoordinator.displayRemarks(editor, editor.document, remarks)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        RemarkInlayCoordinator.clearDisplayedRemarks(event.editor)
    }


}