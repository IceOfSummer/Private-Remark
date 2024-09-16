package io.github.iceofsummer.privateremark.svc.impl

import com.intellij.openapi.vfs.VirtualFile
import io.github.iceofsummer.privateremark.bean.VcsType
import io.github.iceofsummer.privateremark.svc.VcsBridge
import org.jetbrains.idea.svn.SvnVcs

class SvnVcsBridge(private val svnVcs: SvnVcs) : VcsBridge {


    override fun getReversion(virtualFile: VirtualFile): String? {
        val info = svnVcs.getInfo(virtualFile) ?: return null
        return info.revision.number.toString()
    }

    override fun getType(): VcsType {
        return VcsType.SVN
    }


}