package io.github.iceofsummer.privateremark.svc

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import io.github.iceofsummer.privateremark.bean.ParentIndicator
import io.github.iceofsummer.privateremark.bean.Remark


interface RemarkService {

    /**
     * 保存备注
     * @param content 备注内容
     * @param editor 编辑器
     * @param psi psi, optional
     * @return 创建的备注
     */
    fun saveRemark(content: String, editor: Editor, psi: PsiFile?): Remark

    /**
     * 保存备注
     */
    fun saveRemark(file: VirtualFile, remark: Remark)

    /**
     * 获取文件上的备注
     */
    fun resolveRemarks(file: VirtualFile): Set<Remark>

    /**
     * 获取某个文件上的备注
     * @param file 文件路径
     * @param lineNumber 行号
     * @return 该文件上的备注
     */
    fun resolveFileRemark(file: VirtualFile, lineNumber: Int): Remark?

    /**
     * 移动文件备注
     * @param file 文件路径
     * @param lineNumber 旧行号
     * @param remark 新行号
     */
    fun updateFileRemark(file: VirtualFile, lineNumber: Int, newRemark: Remark)

    /**
     * 更新(替换)某个文件中所有备注
     */
    fun updateFileRemarks(file: VirtualFile, remarks: MutableSet<Remark>)

    /**
     * 将某个备注标记为无效，表示该备注需要重新确定位置
     * @param file 文件路径
     * @param lineNumber 行号
     */
    fun invalidFileRemark(file: VirtualFile, lineNumber: Int)

    /**
     * 获取并删除失效的备注
     * @param file 文件路径
     */
    fun moveInvalidRemark(file: VirtualFile): Set<Remark>?

    /**
     * 清除失效标记
     */
    fun updateInvalidRemark(file: VirtualFile, invalid: MutableSet<Remark>)

    /**
     * 标注这组备注需要手动修复
     */
    fun markRequireManualFix(file: VirtualFile, invalid: MutableSet<Remark>)

    /**
     * 克隆一个备注
     */
    fun cloneRemark(remark: Remark): Remark {
        var indicator: ParentIndicator? = null

        remark.parentIndicator?.let { ind ->
            indicator = ParentIndicator(ind.classname, ind.name)
        }

        return Remark(
            remark.startOffsetInParent,
            remark.lineNumber,
            remark.content,
            indicator,
            remark.content
        )
    }

}