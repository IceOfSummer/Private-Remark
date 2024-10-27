package io.github.iceofsummer.privateremark.svc.impl

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkInsertDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkHolderDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.mapper.inter.RemarkHolderMapper
import io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
import io.github.iceofsummer.privateremark.mapper.transaction.Transactional
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import org.apache.ibatis.session.SqlSessionFactory

class LocalStorageRemarkServiceImpl : RemarkServiceV2 {

    private lateinit var remarkMapper: RemarkMapper

    private lateinit var remarkHolderMapper: RemarkHolderMapper

    fun setRemarkMapper(remarkMapper: RemarkMapper) {
        this.remarkMapper = remarkMapper
    }

    fun setRemarkHolderMapper(remarkHolderMapper: RemarkHolderMapper) {
        this.remarkHolderMapper = remarkHolderMapper
    }

    @Transactional
    override fun saveRemark(remarkInsertDTO: RemarkInsertDTO): Int {
        remarkMapper.insert(remarkInsertDTO.remark)
        remarkInsertDTO.holder ?.let { holder ->
            holder.remarkId = remarkInsertDTO.remark.id
            remarkHolderMapper.insert(holder)
        }

        return remarkInsertDTO.remark.id
    }

    override fun resolveAllValidRemarks(path: String): List<RemarkDTO> {
        return remarkMapper.selectAllByPath(path, false).map { RemarkDTO(it) }
    }

    override fun resolveAllInvalidRemarks(path: String): List<RemarkDTO> {
        return remarkMapper.selectAllByPath(path, true).map { RemarkDTO(it) }
    }

    override fun getRemarkHolderById(id: Int): RemarkHolderDTO? {
        return remarkHolderMapper.selectByRemarkId(id)?.let { RemarkHolderDTO(it) }
    }

    override fun markRemarkInvalid(id: Int) {
        remarkMapper.update(RemarkPO().apply {
            this.id = id
            this.isInvalid = true
        })
    }

    @Transactional
    override fun fixRemark(id: Int, remarkFixDTO: RemarkFixDTO) {
        remarkMapper.update(RemarkPO().apply {
            this.id = id
            this.lineNumber = remarkFixDTO.lineNumber
            this.currentLineContent = remarkFixDTO.lineContent
        })

        remarkFixDTO.offsetInParent?.let {
            remarkHolderMapper.update(RemarkHolderPO().apply {
                this.remarkId = id
                this.offsetInParent = it
            })
        }
    }


}