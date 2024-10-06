package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTreeTable
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.ui.treeStructure.treetable.TreeTable
import java.io.File

class PrivateRemarkConfigurable : Configurable {

    private var state: State? = null


    override fun createComponent(): JComponent {
        val remarkSettings = service<PrivateRemarkSettings>()
        val model = remarkSettings.state.copy()
        val root = panel {
            lateinit var fileSystemRadio: Cell<JBRadioButton>
            group("Basic Config") {
                buttonsGroup("Persistence Type") {
                    row {
                        radioButton("None", PersistenceType.NONE)
                    }
                    row {
                        fileSystemRadio = radioButton("Local file system", PersistenceType.LOCAL_FILE_SYSTEM)
                    }
                    row {
                        radioButton("In memory", PersistenceType.IN_MEMORY)
                    }
                }.bind(model::persistenceType)
            }

            group("Persistence Config") {
                row("Persistence root") {
                    val fileSelector = textFieldWithBrowseButton(
                        "Select Directory",
                        null,
                        FileChooserDescriptorFactory.createSingleLocalFileDescriptor(),
                        { vf -> vf.path }
                    ).bindText(model::localFileSystemDirectory)

                    fileSelector.validation(object : DialogValidation {
                        override fun validate(): ValidationInfo? {
                            if (fileSelector.component.text.isEmpty()) {
                                return ValidationInfo("Should not be empty")
                            }
                            if (!File(fileSelector.component.text).isDirectory) {
                                return ValidationInfo("Should be a directory")
                            }
                            return null
                        }
                    })
                }
            }.visibleIf(object : ComponentPredicate() {
                override fun addListener(listener: (Boolean) -> Unit) {
                    fileSystemRadio.onChanged { radio -> listener(radio.isSelected) }
                }
                override fun invoke(): Boolean = model.persistenceType == PersistenceType.LOCAL_FILE_SYSTEM
            })
        }
        TreeTable
        root.registerValidators {}
        state = State(model, root)
        return root
    }

    private fun checkDatabase(pathLike: String) {

    }

    override fun isModified(): Boolean {
        val state = state ?: return false
        val panel = state.panel
        val modified = panel.isModified()
        if (modified) {
            return panel.validateAll().isEmpty()
        }
        return false
    }

    override fun apply() {
        val state = state ?: return
        state.panel.apply()

        val remarkSettings = service<PrivateRemarkSettings>()

        val persistenceState = remarkSettings.state
        val model = state.persistenceConfig

        persistenceState.persistenceType = model.persistenceType
        persistenceState.localFileSystemDirectory = model.localFileSystemDirectory
    }

    override fun getDisplayName(): String {
        return "Private Remark Settings"
    }

    override fun disposeUIResources() {
        state = null
    }


}

enum class DBStatus {
    // 运行中
    RUNNING,
    // 没有已经创建的数据库
    NONE,
    // 已经存在创建的数据库
    EXIST
}

data class State(
    var persistenceConfig: PrivateRemarkState,
    var panel: DialogPanel,
    var dbStatus: DBStatus = DBStatus.RUNNING
)

