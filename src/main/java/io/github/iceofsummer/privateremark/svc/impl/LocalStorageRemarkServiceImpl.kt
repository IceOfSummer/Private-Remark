package io.github.iceofsummer.privateremark.svc.impl

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkInsertDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkHolderDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.mapper.inter.RemarkHolderMapper
import io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import org.apache.ibatis.session.SqlSessionFactory

class LocalStorageRemarkServiceImpl(private val sqlSessionFactory: SqlSessionFactory) : RemarkServiceV2 {


    override fun saveRemark(remarkInsertDTO: RemarkInsertDTO): Int {
        sqlSessionFactory.openSession().use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            remarkMapper.insert(remarkInsertDTO.remark)

            val remarkHolderMapper = sqlSession.getMapper(RemarkHolderMapper::class.java)

            remarkInsertDTO.holder ?.let { holder ->
                holder.remarkId = remarkInsertDTO.remark.id
                remarkHolderMapper.insert(holder)
            }

            sqlSession.commit()
            return remarkInsertDTO.remark.id
        }
    }

    override fun resolveAllValidRemarks(path: String): List<RemarkDTO> {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            return remarkMapper.selectAllByPath(path, false).map { RemarkDTO(it) }
        }
    }

    override fun resolveAllInvalidRemarks(path: String): List<RemarkDTO> {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            return remarkMapper.selectAllByPath(path, true).map { RemarkDTO(it) }
        }
    }

    override fun getRemarkHolderById(id: Int): RemarkHolderDTO? {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkHolderMapper = sqlSession.getMapper(RemarkHolderMapper::class.java)

            return remarkHolderMapper.selectByRemarkId(id)?.let { RemarkHolderDTO(it) }
        }
    }

    override fun markRemarkInvalid(id: Int) {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            remarkMapper.update(RemarkPO().apply {
                this.id = id
                this.isInvalid = true
            })
        }
    }

    override fun fixRemark(id: Int, remarkFixDTO: RemarkFixDTO) {
        sqlSessionFactory.openSession().use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            val remarkHolderMapper = sqlSession.getMapper(RemarkHolderMapper::class.java)

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

            sqlSession.commit()
        }
    }



}