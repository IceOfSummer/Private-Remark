package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import io.github.iceofsummer.privateremark.bridge.bean.PersistenceType
import java.io.File

class PrivateRemarkSettingsUI : Disposable {


    val settingState: SettingState

    private var loadingSubStorageControl: LoadingDecorator? = null

    private var root: DialogPanel? = null

    init {
        val ideSettingsStorage = service<IDESettingsStorage>()
        val managedSettings = ideSettingsStorage.state.copy()
        settingState = SettingState(managedSettings, SettingState.DBStatus.NONE)
    }


    /**
     * 获取组件，第一次调用将会创建，后续调用将会获取上一次创建的实例
     */
    fun component(): DialogPanel {
        var currentRoot = root
        if (currentRoot != null) {
            return currentRoot
        }

        val managedSettings = settingState.managedSettings

        currentRoot = panel {
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
                        radioButton("In memory", PersistenceType.IN_MEMORY).comment("In memory is only used for a simple trial.")
                    }
                }.bind(managedSettings::persistenceType)
            }

            group("Persistence Config") {
                row("Persistence root") {
                    val fileSelector = textFieldWithBrowseButton(
                        "Select Directory",
                        null,
                        FileChooserDescriptorFactory.createSingleLocalFileDescriptor(),
                        { vf -> vf.path }
                    )
                        .bindText(managedSettings::localFileSystemDirectory)
                        .comment("Where should the data be stored?")

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
                override fun invoke(): Boolean = managedSettings.persistenceType == PersistenceType.LOCAL_FILE_SYSTEM
            })
//  TODO
//            group("Sub Storage Root") {
//
//                row {
//                    val child = DefaultListModel<String>().apply {}
//
//                    val list = JBList(child)
//
//                    val toolbarDecorator = ToolbarDecorator.createDecorator(list)
//                        .setRemoveAction {
//                            println(list.selectedIndex)
//                        }
//                        .setAddAction {
//                            println("Add")
//                        }
//                        .disableUpDownActions()
//                        .setToolbarPosition(ActionToolbarPosition.TOP)
//
//                    val panel = JBLoadingPanel(BorderLayout(), this@PrivateRemarkSettingsUI)
//                    panel.add(toolbarDecorator.createPanel(), BorderLayout.NORTH)
//
//
//                    cell(panel).align(AlignX.FILL)
//                }
//            }
        }

        currentRoot.registerValidators {}
        this.root = currentRoot
        return currentRoot
    }

    override fun dispose() {
        loadingSubStorageControl?.stopLoading()
    }

//    inner class AddSubStorageRootAction : AnActionButtonRunnable {
//        override fun run(t: AnActionButton?) {
//            val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
//            descriptor.title = "Select Child Directory"
//            val file = FileChooser.chooseFile(descriptor, null, null)
//
//
//        }
//    }

}