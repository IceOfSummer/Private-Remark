package io.github.iceofsummer.privateremark.ui

import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import javax.swing.JLabel

class InvalidRemarkToolWindow(private val virtualFile: VirtualFile) : SimpleToolWindowPanel(false) {


    init {
        val splitter = Splitter(false, 0.3f, 0.05f, 0.95f).apply {
            firstComponent = JBScrollPane(JLabel("<a>Load file for vcs2</a>"))
            secondComponent = JBScrollPane(JLabel("<a>Load file for vcs2</a>"))
        }
        setContent(splitter)
    }

}