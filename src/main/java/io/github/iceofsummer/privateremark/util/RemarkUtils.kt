package io.github.iceofsummer.privateremark.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.startOffset
import io.github.iceofsummer.privateremark.bean.ParentIndicator
import io.github.iceofsummer.privateremark.bean.Remark

object RemarkUtils {

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
            startOffsetInParent,
            lineNumber,
            content,
            indicator,
            doc.getText(
                TextRange(doc.getLineStartOffset(lineNumber), lineEnd)
            )
        )
    }


}