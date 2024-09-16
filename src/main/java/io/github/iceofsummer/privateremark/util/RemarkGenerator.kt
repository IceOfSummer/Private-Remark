package io.github.iceofsummer.privateremark.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.bean.po.RemarkVcs
import io.github.iceofsummer.privateremark.svc.factory.VcsBridgeFactory
import java.nio.file.Paths

object RemarkUtils {


    fun generateRemarkPO(content: String, lineNumber: Int, editor: Editor): RemarkPO {
        val doc = editor.document
        val lineEnd = doc.getLineEndOffset(lineNumber)

        // TODO: allow non project file.
        val project = editor.project ?: throw IllegalStateException("Non project files")
        val basePath = project.basePath ?: throw IllegalStateException("Could not find project root path")
        return RemarkPO(
            -1,
            toRelativePath(basePath, editor.virtualFile.path), // TODO: to relative path
            lineNumber,
            content,
            doc.getText(
                TextRange(doc.getLineStartOffset(lineNumber), lineEnd)
            )
        )
    }

    private fun tryBuildRemarkVcs(editor: Editor): RemarkVcs? {
        val project = editor.project ?: return null
        val vcs = VcsBridgeFactory.getInstance(project, editor.virtualFile) ?: return null
        val reversion = vcs.getReversion(editor.virtualFile) ?: return null
        return RemarkVcs(-1, vcs.getType(), reversion)
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

    /**
     * 将绝对路径转化为相对路径
     * @throws [IllegalArgumentException] 无法找到相对路径
     */
    fun toRelativePath(root: String, absolutePath: String): String {
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