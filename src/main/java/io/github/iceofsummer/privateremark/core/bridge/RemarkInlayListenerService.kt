package io.github.iceofsummer.privateremark.core.bridge

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import io.github.iceofsummer.privateremark.ui.RemarkInlineInlayRenderer

/**
 * 当移动到备注上时，高亮并添加下划线。点击时展开备注.
 * @see com.intellij.xdebugger.impl.inline.DebuggerInlayListener
 */
@Service(Service.Level.APP)
class RemarkInlayListenerService : EditorMouseMotionListener, EditorMouseListener {

    private var lastHoveredInlay: Inlay<RemarkInlineInlayRenderer>? = null

    override fun mouseMoved(e: EditorMouseEvent) {
        val inlay = e.inlay
        val lastInlay = lastHoveredInlay
        if (lastInlay != null) {
            val renderer = lastInlay.renderer
            if (lastInlay !== inlay) {
                renderer.onMouseExit(lastInlay)
            }
            this.lastHoveredInlay = null
        }
        if (inlay != null) {
            val renderer = inlay.renderer
            if (renderer is RemarkInlineInlayRenderer) {
                val remarkInlineInlayRendererInlay = inlay as Inlay<RemarkInlineInlayRenderer>
                renderer.onHovered(inlay)

                this.lastHoveredInlay = remarkInlineInlayRendererInlay
            } else {
                this.lastHoveredInlay = null
            }
        }
    }

    override fun mouseClicked(event: EditorMouseEvent) {
        println("TODO: clicked")
    }
}