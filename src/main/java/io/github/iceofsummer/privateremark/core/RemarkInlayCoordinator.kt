package io.github.iceofsummer.privateremark.core

import com.intellij.codeInsight.inline.completion.render.InlineSuffixRenderer
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.vfs.VirtualFile
import io.github.iceofsummer.privateremark.bean.Remark


typealias InlayMapValue = MutableList<Pair<Remark, Inlay<InlineSuffixRenderer>>>
typealias InlayMap = MutableMap<String, InlayMapValue>
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
            InlineSuffixRenderer(editor, remark.content)
        );
        if (inlay != null) {
            inlays.getOrPut(editor.virtualFile.hashKey, { mutableListOf() }).add(Pair(remark, inlay))
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