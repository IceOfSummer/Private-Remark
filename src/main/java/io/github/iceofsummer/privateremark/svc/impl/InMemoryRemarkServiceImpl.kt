package io.github.iceofsummer.privateremark.svc.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import io.github.iceofsummer.privateremark.bean.Remark
import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import java.util.Collections


private val VirtualFile.hashKey: String
    get() = url


/**
 * 将备注保存到内存中，用于测试使用.
 */
class InMemoryRemarkServiceImpl: RemarkServiceV2 {

    private val remarksMap: MutableMap<String, MutableList<RemarkPO>> = HashMap()

    private val invalidRemarksMap: MutableMap<String, MutableList<RemarkPO>> = HashMap()


    override fun saveRemark(remarkDTO: RemarkDTO): RemarkPO {
        val id = System.currentTimeMillis().toInt()
        val remarks = remarksMap.getOrPut(remarkDTO.remarkPO.path) { mutableListOf() }
        remarks.add(remarkDTO.remarkPO)
        remarkDTO.remarkPO.id = id
        return remarkDTO.remarkPO
    }

    override fun resolveAllRemarks(path: String): List<RemarkPO> {
        return remarksMap[path] ?: Collections.emptyList()
    }

    override fun resolveAllInvalidRemarks(path: String): List<RemarkPO> {
        return invalidRemarksMap[path] ?: Collections.emptyList()
    }



}