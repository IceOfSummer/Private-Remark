package io.github.iceofsummer.privateremark.core

import com.intellij.openapi.editor.EditorLinePainter
import com.intellij.openapi.editor.LineExtensionInfo
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.impl.RemarkServiceImpl
import java.awt.Font
import java.util.*

class RemarkPainter : EditorLinePainter() {

    private val remarkService: RemarkService = RemarkServiceImpl


    override fun getLineExtensions(
        project: Project,
        file: VirtualFile,
        lineNumber: Int
    ): MutableCollection<LineExtensionInfo>? {
        val remarks = remarkService.resolveFileRemark(file.url)
        val r = remarks.find { r -> r.lineNumber == lineNumber }
        if (r == null) {
            return null
        }
        return Collections.singletonList(
            LineExtensionInfo(
                r.content,
                TextAttributes(
                    null,
                    null,
                    JBColor.BLUE,
                    null,
                    Font.PLAIN
                )
            )
        )
    }


}