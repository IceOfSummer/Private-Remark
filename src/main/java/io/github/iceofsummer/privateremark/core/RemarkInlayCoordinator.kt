package io.github.iceofsummer.privateremark.core

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationProvider
import io.github.iceofsummer.privateremark.bean.Remark
import io.github.iceofsummer.privateremark.bridge.RemarkInlayListenerService
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl
import io.github.iceofsummer.privateremark.ui.RemarkInlineInlayRenderer


private typealias InlayMapValue = MutableList<Pair<Remark, Inlay<out EditorCustomElementRenderer>>>
private typealias InlayMap = MutableMap<String, InlayMapValue>
private val VirtualFile.hashKey: String
    get() = url


/**
 * 用于协调备注的展示与管理.
 */
object RemarkInlayCoordinator {


    private val inlays: InlayMap = mutableMapOf()


    /**
     * 展示备注，并记录状态
     */
    fun displayRemark(editor: Editor,
                             remark: Remark,
                             document: Document
    ) {
        val inlay = editor.inlayModel.addAfterLineEndElement(
            document.getLineEndOffset(remark.lineNumber),
            true,
            RemarkInlineInlayRenderer(editor, remark.content)
        );
        if (inlay != null) {
            val loadedInlays = inlays[editor.virtualFile.hashKey]
            val pair = Pair(remark, inlay)
            if (loadedInlays != null) {
                loadedInlays.add(pair)
            } else {
                inlays[editor.virtualFile.hashKey] = mutableListOf(pair)
                editor.addEditorMouseListener(service<RemarkInlayListenerService>())
                editor.addEditorMouseMotionListener(service<RemarkInlayListenerService>())
            }
        } else {
            throw IllegalStateException("Inlay is null!")
        }
    }


    /**
     * 获取并删除的备注 Inlay
     */
    fun getAndDeleteInlays(virtualFile: VirtualFile): InlayMapValue? {
        return inlays.remove(virtualFile.hashKey)
    }


}

