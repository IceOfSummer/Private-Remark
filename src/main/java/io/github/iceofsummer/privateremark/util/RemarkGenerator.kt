package io.github.iceofsummer.privateremark.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.startOffset
import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import java.nio.file.Paths

object RemarkUtils {


    fun generateRemark(content: String, lineNumber: Int, editor: Editor): RemarkDTO {
        val doc = editor.document
        val lineEnd = doc.getLineEndOffset(lineNumber)

        // TODO: allow non project file.
        val project = editor.project ?: throw IllegalStateException("Non project files")
        val basePath = project.basePath ?: throw IllegalStateException("Could not find project root path")
        return RemarkDTO(
            -1,
            toRelativePath(basePath, editor.virtualFile.path), // TODO: to relative path
            lineNumber,
            content,
            doc.getText(
                TextRange(doc.getLineStartOffset(lineNumber), lineEnd)
            ),
            false
        )
    }

    /**
     * 尝试获取 [RemarkHolderPO]
     * @return 如果能够获取，返回一个 [RemarkHolderPO.remarkId] 为 -1 的实例
     */
    fun tryResolveRemarkHolder(editor: Editor, lineNumber: Int): RemarkHolderPO? {
        val project = editor.project ?: return null
        val psi = PsiManager.getInstance(project).findFile(editor.virtualFile) ?: return null
        val provider = psi.viewProvider

        val node = provider.findElementAt(editor.caretModel.offset)
        val parent = PsiTreeUtil.getParentOfType(node, PsiNamedElement::class.java)
        val lineEnd = editor.document.getLineEndOffset(lineNumber)

        parent?.name?.let { name ->
            return RemarkHolderPO(
                -1,
                lineEnd - parent.startOffset - 1,
                parent.javaClass.name
            )
        }
        return null
    }


    /**
     * 将绝对路径转化为相对路径
     * @throws [IllegalArgumentException] 无法找到相对路径
     */
    private fun toRelativePath(root: String, absolutePath: String): String {
        val target = Paths.get(absolutePath)
        val base = Paths.get(root)
        return base.relativize(target).toString()
    }

    fun toRelativePath(root: Project?, absolutePath: String) : String{
        root ?: return absolutePath
        val base = root.basePath ?: return absolutePath
        return toRelativePath(base, absolutePath)
    }

}