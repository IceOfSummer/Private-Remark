package io.github.iceofsummer.privateremark.ui

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import com.intellij.xdebugger.ui.DebuggerColors
import java.awt.Cursor
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle

class RemarkInlineInlayRenderer(private val editor: Editor, val content: String) : EditorCustomElementRenderer {

    private val width: Int = editor.contentComponent.getFontMetrics(getFont(editor)).stringWidth(content)

    /**
     * 鼠标是否聚焦
     */
    private var hovered: Boolean = false

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        return width
    }

    private fun getFont(editor: Editor): Font {
        val colorsScheme = editor.colorsScheme
        val attributes = editor.colorsScheme.getAttributes(DebuggerColors.INLINED_VALUES_EXECUTION_LINE)
        val fontStyle = attributes?.fontType ?: Font.PLAIN
        return UIUtil.getFontWithFallback(colorsScheme.getFont(EditorFontType.forJavaStyle(fontStyle)))
    }

    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
        g.color = if (hovered) JBColor.BLUE else JBColor.GRAY
        g.font = getFont(inlay.editor)

        g.drawString(content, targetRegion.x, targetRegion.y + inlay.editor.ascent)
    }

    fun onHovered(inlay: Inlay<RemarkInlineInlayRenderer>) {
        this.hovered = true
        editor.contentComponent.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        inlay.update()
    }

    fun onMouseExit(inlay: Inlay<RemarkInlineInlayRenderer>) {
        this.hovered = false
        editor.contentComponent.cursor = Cursor.getDefaultCursor()
        inlay.update()
    }


}