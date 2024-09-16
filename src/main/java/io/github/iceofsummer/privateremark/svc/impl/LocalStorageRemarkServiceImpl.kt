package io.github.iceofsummer.privateremark.svc.impl

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile
import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import org.apache.ibatis.session.SqlSessionFactory
import java.util.*

class LocalStorageRemarkServiceImpl(private val sqlSessionFactory: SqlSessionFactory) : RemarkServiceV2 {


    override fun saveRemark(remarkDTO: RemarkDTO): RemarkPO {
        sqlSessionFactory.openSession(true).use { sqlSession ->
            val remarkMapper = sqlSession.getMapper(RemarkMapper::class.java)
            val insert = remarkMapper.insert(remarkDTO.remarkPO)
            remarkDTO.remarkPO.id = insert
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


}