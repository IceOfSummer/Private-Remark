package io.github.iceofsummer.privateremark.svc.factory

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.AbstractVcs
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vfs.VirtualFile
import git4idea.GitVcs
import git4idea.repo.GitRepositoryManager
import io.github.iceofsummer.privateremark.svc.VcsBridge
import io.github.iceofsummer.privateremark.svc.impl.GitVcsBridge
import io.github.iceofsummer.privateremark.svc.impl.SvnVcsBridge
import org.jetbrains.idea.svn.SvnVcs

object VcsBridgeFactory {

    fun getInstance(project: Project, virtualFile: VirtualFile): VcsBridge? {
        val vcs = ProjectLevelVcsManager.getInstance(project).getVcsFor(virtualFile)
        if (vcs is GitVcs) {
            return GitVcsBridge(GitRepositoryManager.getInstance(project).getRepositoryForFile(virtualFile) ?: return null)
        } else if (vcs is SvnVcs) {
            return SvnVcsBridge(vcs)
        }
        return null
    }

}