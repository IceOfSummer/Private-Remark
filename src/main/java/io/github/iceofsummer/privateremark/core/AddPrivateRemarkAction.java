package io.github.iceofsummer.privateremark.core;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import io.github.iceofsummer.privateremark.svc.RemarkService;
import io.github.iceofsummer.privateremark.svc.impl.RemarkServiceImpl;
import io.github.iceofsummer.privateremark.ui.EditRemarkPopup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Action class to demonstrate how to interact with the IntelliJ Platform.
 * The only action this class performs is to provide the user with a popup dialog as feedback.
 * Typically this class is instantiated by the IntelliJ Platform framework based on declarations
 * in the plugin.xml file. But when added at runtime this class is instantiated by an action group.
 */
public class AddPrivateRemarkAction extends AnAction {

    private final RemarkService remarkService = RemarkServiceImpl.INSTANCE;


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * This default constructor is used by the IntelliJ Platform framework to instantiate this class based on plugin.xml
     * declarations. Only needed in {@link AddPrivateRemarkAction} class because a second constructor is overridden.
     */
    public AddPrivateRemarkAction() {
        super();
    }

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     *
     * @param text        The text to be displayed as a menu item.
     * @param description The description of the menu item.
     * @param icon        The icon to be used with the menu item.
     */
    public AddPrivateRemarkAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
        if (editor == null) {
            return;
        }
        EditRemarkPopup editRemarkPopup = new EditRemarkPopup();
        JBPopupFactory instance = JBPopupFactory.getInstance();
        Balloon balloon = instance.createDialogBalloonBuilder(editRemarkPopup.getRoot(), "Add Private Remark")
                .setCloseButtonEnabled(true)
                .setHideOnClickOutside(true)
                .setHideOnCloseClick(true)
                .createBalloon();

        editRemarkPopup.getCancelBtn().addActionListener(e -> {
            balloon.hide();
        });

        editRemarkPopup.getSaveBtn().addActionListener(e -> {
            remarkService.saveRemark(editRemarkPopup.getRemark().getText(), editor, event.getData(CommonDataKeys.PSI_FILE));
            balloon.hide();
        });

        balloon.show(instance.guessBestPopupLocation(editor), Balloon.Position.below);
    }

    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}