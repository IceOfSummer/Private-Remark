package io.github.iceofsummer.privateremark.svc.impl

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkInsertDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkHolderDTO
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import java.util.Collections


/**
 * 将备注保存到内存中，用于测试使用.
 */
class InMemoryRemarkServiceImpl: RemarkServiceV2 {

    private val remarksMap: MutableMap<String, MutableList<RemarkDTO>> = HashMap()

    private val invalidRemarksMap: MutableMap<String, MutableList<RemarkDTO>> = HashMap()


    override fun saveRemark(remarkInsertDTO: RemarkInsertDTO): Int {
        val id = System.currentTimeMillis().toInt()
        val remarks = remarksMap.getOrPut(remarkInsertDTO.remark.path) { mutableListOf() }
        remarks.add(remarkInsertDTO.remark)
        remarkInsertDTO.remark.id = id
        return id
    }

    override fun resolveAllValidRemarks(path: String): List<RemarkDTO> {
        return remarksMap[path] ?: Collections.emptyList()
    }

    override fun resolveAllInvalidRemarks(path: String): List<RemarkDTO> {
        return invalidRemarksMap[path] ?: Collections.emptyList()
    }

    override fun getRemarkHolderById(id: Int): RemarkHolderDTO? {
        return null
    }

    override fun markRemarkInvalid(id: Int) {
        TODO("Not yet implemented")
    }

    override fun fixRemark(id: Int, remarkFixDTO: RemarkFixDTO) {
        TODO("Not yet implemented")
    }




}