package io.github.iceofsummer.privateremark.svc

import com.intellij.openapi.vfs.VirtualFile
import io.github.iceofsummer.privateremark.bean.po.RemarkVcs


interface VcsBridge {

    /**
     * 获取文件当前的版本号
     * @return 当前版本号。如果没有 VCS 则返回空。
     */
    fun getReversion(virtualFile: VirtualFile): String?

    fun getType(): RemarkVcs.VcsType


}
