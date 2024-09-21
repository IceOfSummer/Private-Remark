package io.github.iceofsummer.privateremark.bridge

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.ui.popup.JBPopupFactory
import io.github.iceofsummer.privateremark.ui.RemarkDisplayPanel
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
                @Suppress("UNCHECKED_CAST")
                val remarkInlineInlayRendererInlay = inlay as Inlay<RemarkInlineInlayRenderer>
                renderer.onHovered(inlay)

                this.lastHoveredInlay = remarkInlineInlayRendererInlay
            } else {
                this.lastHoveredInlay = null
            }
        }
    }

    override fun mouseClicked(event: EditorMouseEvent) {
        val inlay = event.inlay ?: return
        val renderer = inlay.renderer
        if (renderer !is RemarkInlineInlayRenderer) {
            return
        }
        val displayPanel = RemarkDisplayPanel()
        displayPanel.contentPanel.text = renderer.content

        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(displayPanel.root, null)
            .setCancelOnClickOutside(true)
            .createPopup()
            .showInBestPositionFor(inlay.editor)
    }
}