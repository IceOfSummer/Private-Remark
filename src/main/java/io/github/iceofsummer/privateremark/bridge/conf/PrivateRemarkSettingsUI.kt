package io.github.iceofsummer.privateremark.bridge.conf

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLoadingPanel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.ui.table.JBTable
import io.github.iceofsummer.privateremark.bean.PrivateRemarkProject
import io.github.iceofsummer.privateremark.bridge.bean.PersistenceType
import java.awt.BorderLayout
import java.io.File
import javax.swing.table.AbstractTableModel

class PrivateRemarkSettingsUI(
    private val loadProjects: (rootDirectory: String) -> MutableList<PrivateRemarkProject>
) : Disposable {


    val settingState: SettingState

    var projects: MutableList<PrivateRemarkProject> = mutableListOf()

    private var isProjectsModified: Boolean = false

    private var root: DialogPanel? = null

    init {
        val ideSettingsStorage = service<IDESettingsStorage>()
        val managedSettings = ideSettingsStorage.state.copy()
        settingState = SettingState(managedSettings, SettingState.DBStatus.NONE)
    }

    fun isModified(): Boolean {
        val panel = root ?: return false
        val modified = panel.isModified()
        if (modified && panel.validateAll().isEmpty()) {
            return true
        }
        return isProjectsModified
    }

    fun resetModifiedState() {
        isProjectsModified = false
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

            group("Private Remark Projects") {

                row {
                    cell(createProjectTable()).align(AlignX.FILL)
                }
            }
        }

        currentRoot.registerValidators {}
        this.root = currentRoot
        return currentRoot
    }



    private fun createProjectTable(): JBLoadingPanel {
        projects = loadProjects(settingState.managedSettings.localFileSystemDirectory)


        val list = JBTable(object : AbstractTableModel() {
            override fun getRowCount(): Int {
                return projects.size
            }

            override fun getColumnCount(): Int {
                return 2
            }

            override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
                if (columnIndex == 0) {
                    return projects[rowIndex].name
                }
                return projects[rowIndex].description
            }

            override fun getColumnName(column: Int): String {
                return if (column == 0) {
                    "Name"
                } else {
                    "Description"
                }
            }

        })

        val toolbarDecorator = ToolbarDecorator.createDecorator(list)
            .setRemoveAction {
                isProjectsModified = true
                val jbTable = it.contextComponent as JBTable
                projects.removeAt(jbTable.selectedRow)
                jbTable.updateUI()
            }
            .setAddAction {
                CreateOrUpdateProjectDialogUI({ project ->
                    val jbTable = it.contextComponent as JBTable
                    projects.add(project)
                    isProjectsModified = true
                    jbTable.updateUI()
                }).show()
            }
            .disableUpDownActions()
            .setToolbarPosition(ActionToolbarPosition.TOP)

        val panel = JBLoadingPanel(BorderLayout(), this)
        panel.add(toolbarDecorator.createPanel(), BorderLayout.NORTH)
        return panel
    }


    override fun dispose() {}


}