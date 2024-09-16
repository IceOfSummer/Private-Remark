package io.github.iceofsummer.privateremark.core.dev;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl;
import org.jetbrains.annotations.NotNull;

public class DevClearAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
//        InMemoryRemarkServiceImpl.INSTANCE.clearRemarks();
    }

    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}
