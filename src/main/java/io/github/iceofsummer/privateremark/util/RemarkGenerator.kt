package io.github.iceofsummer.privateremark.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.startOffset
import io.github.iceofsummer.privateremark.bean.ParentIndicator
import io.github.iceofsummer.privateremark.bean.Remark
import io.github.iceofsummer.privateremark.bean.RemarkVcs
import io.github.iceofsummer.privateremark.svc.factory.VcsBridgeFactory

object RemarkGenerator {

    fun generateRemark(content: String, lineNumber: Int, editor: Editor, psi: PsiFile?): Remark {
        var indicator: ParentIndicator? = null
        val doc = editor.document
        val lineEnd = doc.getLineEndOffset(lineNumber)
        var startOffsetInParent = editor.caretModel.offset

        if (psi != null) {
            val provider = psi.viewProvider

            val node = provider.findElementAt(editor.caretModel.offset)
            val parent = PsiTreeUtil.getParentOfType(node, PsiNamedElement::class.java)

            parent?.name?.let { name ->
                startOffsetInParent = lineEnd - parent.startOffset - 1
                indicator = ParentIndicator(
                    parent.javaClass.name,
                    name
                )
            }
        }

        return Remark(
            System.currentTimeMillis().toString() + fillZero(lineNumber),
            startOffsetInParent,
            lineNumber,
            content,
            indicator,
            doc.getText(
                TextRange(doc.getLineStartOffset(lineNumber), lineEnd)
            ),
            tryBuildRemarkVcs(editor)
        )
    }

    private fun tryBuildRemarkVcs(editor: Editor): RemarkVcs? {
        val project = editor.project ?: return null
        val vcs = VcsBridgeFactory.getInstance(project, editor.virtualFile) ?: return null
        val reversion = vcs.getReversion(editor.virtualFile) ?: return null
        return RemarkVcs(vcs.getType(), reversion)
    }

    /**
     * 如果数字长度不足 4 位，则在后面填 0
     */
    private fun fillZero(value: Int): String {
        var base = value
        while (base < 1000) {
            base *= 10;
        }
        return base.toString()
    }


}