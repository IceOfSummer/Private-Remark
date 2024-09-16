package io.github.iceofsummer.privateremark.svc

import com.intellij.openapi.vfs.VirtualFile
import java.lang.ProcessHandle.Info

interface VcsService {

    /**
     * 获取文件当前的版本号
     * @return 当前版本号。如果没有 VCS 则返回空。
     */
    fun resolveCurrentVersion(virtualFile: VirtualFile): Info?




}