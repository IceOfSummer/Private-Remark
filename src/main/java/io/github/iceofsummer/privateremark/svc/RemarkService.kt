package io.github.iceofsummer.privateremark.svc

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import io.github.iceofsummer.privateremark.bean.Remark


interface RemarkService {

    /**
     * 保存备注
     * @param content 备注内容
     * @param editor 编辑器
     * @param psi psi, optional
     */
    fun saveRemark(content: String, editor: Editor, psi: PsiFile?)

    /**
     * 获取某个文件上的备注
     * @param filepath 文件路径
     * @return 该文件上的备注
     */
    fun resolveFileRemark(filepath: String): List<Remark>

}