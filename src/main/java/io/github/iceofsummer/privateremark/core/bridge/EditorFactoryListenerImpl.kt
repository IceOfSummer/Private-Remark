package io.github.iceofsummer.privateremark.core.bridge

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectLocator
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNamedElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.DocumentUtil
import io.github.iceofsummer.privateremark.bean.ParentIndicator
import io.github.iceofsummer.privateremark.bean.Remark
import io.github.iceofsummer.privateremark.core.RemarkInlayCoordinator
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.ServiceFactory
import io.github.iceofsummer.privateremark.util.RemarkUtils

/**
 * 监听编辑器打开和关闭事件，当打开时显示所有备注，关闭时保存最后的位置。
 */
class EditorFactoryListenerImpl : EditorFactoryListener {

    private val remarkService = ServiceFactory.getService(RemarkService::class)


    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val file = editor.virtualFile
        val remarks = remarkService.resolveRemarks(file)
        if (remarks.isEmpty()) {
            return
        }

        val resolved = resolveRequiredParam(event) ?: return
        val psi = resolved.second

        for (remark in remarks) {
//             TODO "tip user some remark can not fix automatically."
            val indicator = remark.parentIndicator
            val currentText = editor.document.getText(DocumentUtil.getLineTextRange(editor.document, remark.lineNumber))

            if (currentText != remark.lineContent) {
                // content mismatch
                if (psi == null || indicator == null) {
                    remarkService.invalidFileRemark(file, remark.lineNumber)
                    continue
                }

                val fixed = tryFixRemark(remark, indicator, psi, editor.document)
                if (fixed == null) {
                    remarkService.invalidFileRemark(file, remark.lineNumber)
                    continue
                }
                remarkService.updateFileRemark(file, remark.lineNumber, fixed)
                RemarkInlayCoordinator.displayRemark(editor, fixed, editor.document)
                continue
            }
            if (indicator == null) {
                RemarkInlayCoordinator.displayRemark(editor, remark, editor.document)
                continue
            }
            RemarkInlayCoordinator.displayRemark(editor, remark, editor.document)
        }

    }

    override fun editorReleased(event: EditorFactoryEvent) {
        val inlays = RemarkInlayCoordinator.getAndDeleteInlays(event.editor.virtualFile) ?: return
        val editor = event.editor
        val resolved = resolveRequiredParam(event) ?: return
        val psi = resolved.second

        val updated = mutableSetOf<Remark>()
        for (inlay in inlays) {
            try {
                updated.add(RemarkUtils.generateRemark(inlay.first.content, editor.document.getLineNumber(inlay.second.offset), editor, psi))
            } catch (e: IndexOutOfBoundsException) {
                // content is changed by other process.
                remarkService.invalidFileRemark(editor.virtualFile, inlay.first.lineNumber)
            }
        }
        if (updated.isNotEmpty()) {
            remarkService.updateFileRemarks(editor.virtualFile, updated)
        }
    }

    /**
     * 获取必要的参数
     */
    private fun resolveRequiredParam(event: EditorFactoryEvent): Pair<Project, PsiFile?>? {
        val file = event.editor.virtualFile
        val projectArr = ProjectLocator.getInstance().getProjectsForFile(file)
        if (projectArr.isEmpty()) {
            return null
        }
        val project = projectArr.first()!!
        val psi = PsiManager.getInstance(project).findFile(file)
        return Pair(project, psi)
    }


    /**
     * 尝试自动修复备注的位置
     * @return 如果修复成功，返回修复后的备注，否则返回空
     */
    private fun tryFixRemark(remark: Remark, indicator: ParentIndicator, psi: PsiElement, document: Document): Remark? {
        val holder = findTargetHolder(indicator.classname, indicator.name, psi) ?: return null
        val oldOffset = holder.startOffset + remark.startOffsetInParent
        if (oldOffset >= psi.endOffset) {
            return null
        }
        val newLineNumber = document.getLineNumber(oldOffset)

        val fixedRemark = remarkService.cloneRemark(remark)

        // 将脏数据保存，页面更新后统一处理.
        fixedRemark.startOffsetInParent = document.getLineEndOffset(newLineNumber) - holder.startOffset
        fixedRemark.lineNumber = newLineNumber
        fixedRemark.lineContent = document.getText(DocumentUtil.getLineTextRange(document, newLineNumber))

        return fixedRemark
    }

    /**
     * 找到备注对应的代码块
     */
    private fun findTargetHolder(className: String, name: String, root: PsiElement): PsiElement? {
        if (root.javaClass.name == className && root is PsiNamedElement && root.name != null) {
            return root
        }
        for (child in root.children) {
            val r = findTargetHolder(className, name, child)
            if (r != null) {
                return r
            }
        }
        return null
    }

}