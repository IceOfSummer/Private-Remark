package io.github.iceofsummer.privateremark.svc.impl

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.mapper.inter.RemarkHolderMapper
import io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import org.apache.ibatis.session.SqlSessionFactory
import java.util.*

class LocalStorageRemarkServiceImpl(private val sqlSessionFactory: SqlSessionFactory) : RemarkServiceV2 {


    override fun saveRemark(remarkDTO: RemarkDTO): RemarkPO {
        sqlSessionFactory.openSession().use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            val insert = remarkMapper.insert(remarkDTO.remarkPO)
            remarkDTO.remarkPO.id = insert

            val remarkHolderMapper = sqlSession.getMapper(RemarkHolderMapper::class.java)

            remarkDTO.holder ?.let { holder ->
                remarkHolderMapper.insert(holder)
            }

            sqlSession.commit()
            return remarkDTO.remarkPO
        }
    }

    override fun resolveAllRemarks(path: String): List<RemarkPO> {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            return remarkMapper.selectAllByPath(path)
        }
    }

    override fun resolveAllInvalidRemarks(path: String): List<RemarkPO> {
        return Collections.emptyList()
    }

    override fun getRemarkHolderById(id: Int): RemarkHolderPO? {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkHolderMapper = sqlSession.getMapper(RemarkHolderMapper::class.java)

            return remarkHolderMapper.selectByRemarkId(id)
        }
    }

    override fun markRemarkInvalid(id: Int) {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            remarkMapper.update(RemarkPO().apply {
                this.id = id
                this.isInvalid = false
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