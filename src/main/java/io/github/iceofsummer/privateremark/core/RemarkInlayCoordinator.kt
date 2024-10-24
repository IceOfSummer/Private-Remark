package io.github.iceofsummer.privateremark.core

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNamedElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.DocumentUtil
import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.bridge.RemarkInlayListenerService
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import io.github.iceofsummer.privateremark.ui.RemarkInlineInlayRenderer
import io.github.iceofsummer.privateremark.util.RemarkUtils


private typealias InlayMapValue = MutableList<Pair<RemarkDTO, Inlay<out EditorCustomElementRenderer>>>
private typealias InlayMap = MutableMap<String, InlayMapValue>
private val VirtualFile.hashKey: String
    get() = url


/**
 * 用于协调备注的展示与管理.
 */
object RemarkInlayCoordinator {


    private val inlays: InlayMap = mutableMapOf()


    fun displayRemarks(editor: Editor, document: Document, remarks: List<RemarkDTO>) {
        for (remark in remarks) {
            displayRemark(editor, document, remark)
        }
    }

    /**
     * 展示备注，并记录状态
     */
    fun displayRemark(editor: Editor,
                      document: Document,
                      uncheckedRemark: RemarkDTO
    ) {
        val validRemark = ensureRemarkValid(uncheckedRemark, editor) ?: let {
            val service = ServiceFactory.getService(RemarkServiceV2::class)
            service.markRemarkInvalid(uncheckedRemark.id)
            return
        }

        val lineNumber = validRemark.lineNumber
        val content = validRemark.content
        val inlay = editor.inlayModel.addAfterLineEndElement(
            document.getLineEndOffset(lineNumber),
            true,
            RemarkInlineInlayRenderer(editor, content)
        )
        if (inlay != null) {
            val loadedInlays = inlays[editor.virtualFile.hashKey]
            val pair = Pair(validRemark, inlay)
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
     * 确保备注能够在合适的位置展示.
     * @return 返回一个位置一定正确的备注。返回空表示该备注失效.
     */
    private fun ensureRemarkValid(
        uncheckedRemark: RemarkDTO,
        editor: Editor
    ): RemarkDTO? {
        if (isValidRemark(uncheckedRemark, editor)) {
            return uncheckedRemark
        }

        return tryFixRemark(editor, uncheckedRemark) ?: let {
            val remarkService = ServiceFactory.getService(RemarkServiceV2::class)
            remarkService.markRemarkInvalid(uncheckedRemark.id)
            return null
        }
    }

    /**
     * 尝试修复备注.
     * @return 修复后的备注. 如果不能修复，返回空
     */
    private fun tryFixRemark(editor: Editor, invalidRemarkPO: RemarkDTO): RemarkDTO? {
        val project = editor.project ?: return null
        val psi = PsiManager.getInstance(project).findFile(editor.virtualFile) ?: return null
        val remarkService = ServiceFactory.getService(RemarkServiceV2::class)
        val remarkHolderPO = remarkService.getRemarkHolderById(invalidRemarkPO.id)

        if (remarkHolderPO == null) {
            remarkService.markRemarkInvalid(invalidRemarkPO.id)
            return null
        }

        val document = editor.document
        val holder = findTargetHolder(remarkHolderPO.parentIdentifierName, psi) ?: return null
        val oldOffset = holder.startOffset + remarkHolderPO.offsetInParent
        if (oldOffset >= psi.endOffset) {
            return null
        }
        val newLineNumber = document.getLineNumber(oldOffset)

        val newLineContent = document.getText(DocumentUtil.getLineTextRange(document, newLineNumber))
        val newOffset =  document.getLineEndOffset(newLineNumber) - holder.startOffset
        remarkService.fixRemark(
            invalidRemarkPO.id,
            RemarkFixDTO(
                newLineNumber,
                newLineContent,
                newOffset
            )
        )
        return invalidRemarkPO.copy(lineNumber = newLineNumber, currentLineContent = newLineContent)
    }

    private fun isValidRemark(uncheckedRemark: RemarkDTO, editor: Editor): Boolean {
        if (uncheckedRemark.lineNumber >= editor.document.lineCount || uncheckedRemark.lineNumber < 0) {
            return false
        }
        val currentLineText = editor.document.getText(DocumentUtil.getLineTextRange(editor.document, uncheckedRemark.lineNumber))
        return currentLineText == uncheckedRemark.currentLineContent
    }


    /**
     * 清除已经显示的备注，并且保存造成的更改.
     */
    fun clearDisplayedRemarks(editor: Editor) {
        val removed = inlays.remove(editor.virtualFile.hashKey) ?: return
        val remarkServiceV2 = ServiceFactory.getService(RemarkServiceV2::class)

        for (data in removed) {
            checkChanged(editor, data.first, data.second)?.let {
                remarkServiceV2.fixRemark(data.first.id, it)
            }
        }
    }

    /**
     * 检测备注位置是否变动，如果变动了则返回修复后的位置.
     */
    private fun checkChanged(editor: Editor, remark: RemarkDTO, inlay: Inlay<out EditorCustomElementRenderer>): RemarkFixDTO? {
        var changed = false
        val doc = editor.document

        val newLineNumber = doc.getLineNumber(inlay.offset)
        if (newLineNumber != remark.lineNumber) {
            changed = true
        }

        val newLineContent = doc.getText(
            TextRange(doc.getLineStartOffset(newLineNumber), doc.getLineEndOffset(newLineNumber))
        )
        if (newLineContent != remark.currentLineContent) {
            changed = true
        }


        if (changed) {
            return RemarkFixDTO(
                newLineNumber,
                newLineContent,
                RemarkUtils.tryResolveRemarkHolder(editor, newLineNumber)?.offsetInParent
            )
        }
        return null
    }


    private fun findTargetHolder(className: String, root: PsiElement): PsiElement? {
        if (root.javaClass.name == className && root is PsiNamedElement && root.name != null) {
            return root
        }
        for (child in root.children) {
            val r = findTargetHolder(className, child)
            if (r != null) {
                return r
            }
        }
        return null
    }


}

