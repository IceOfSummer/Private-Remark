package io.github.iceofsummer.privateremark.svc.impl

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import io.github.iceofsummer.privateremark.bean.ParentIndicator
import io.github.iceofsummer.privateremark.bean.Remark
import io.github.iceofsummer.privateremark.svc.RemarkService
import java.util.Collections

object RemarkServiceImpl: RemarkService {

    private val temporary: MutableMap<String, MutableList<Remark>> = HashMap()

    private fun createRemarkObj(content: String, editor: Editor, psi: PsiFile?): Remark {
        var indicator: ParentIndicator? = null
        if (psi != null) {
            val provider = psi.getViewProvider()

            val node = provider.findElementAt(editor.caretModel.offset)
            val parent = PsiTreeUtil.getParentOfType(node, PsiNamedElement::class.java)
            parent?.name?.let { name ->
                indicator = ParentIndicator(parent.javaClass.name, name)
            }
        }
        return Remark(
            indicator,
            editor.caretModel.offset,
            editor.document.getLineNumber(editor.caretModel.offset),
            content
        )
    }


    override fun saveRemark(content: String, editor: Editor, psi: PsiFile?) {
        val remark = createRemarkObj(content, editor, psi)
        println(remark)
        // TODO("Save the remark. ")
        temporary.getOrPut(editor.virtualFile.url, {
            ArrayList()
        }).add(remark)
    }

    override fun resolveFileRemark(filepath: String): List<Remark> {
        return temporary.getOrDefault(filepath, Collections.emptyList())
    }


}