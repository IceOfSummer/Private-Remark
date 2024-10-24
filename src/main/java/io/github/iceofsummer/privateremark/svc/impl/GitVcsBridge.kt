package io.github.iceofsummer.privateremark.svc.impl

import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import io.github.iceofsummer.privateremark.bean.po.RemarkVcs
import io.github.iceofsummer.privateremark.svc.VcsBridge

class GitVcsBridge(private val repository: GitRepository) : VcsBridge {

    override fun getReversion(virtualFile: VirtualFile): String? {
        if (virtualFile.isDirectory) {
            return null
        }
        return repository.currentRevision
    }

    override fun getType(): RemarkVcs.VcsType {
        return RemarkVcs.VcsType.GIT
    }


}