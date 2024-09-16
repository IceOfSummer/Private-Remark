package io.github.iceofsummer.privateremark.mapper.inter

import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import org.apache.ibatis.annotations.Param

/**
 * @see io.github.iceofsummer.privateremark.bean.po.RemarkPO
 */
interface RemarkMapper {

    /**
     * 根据 [RemarkPO.path] 查询所有的 [RemarkPO]
     */
    fun selectAllByPath(@Param("path") path: String): List<RemarkPO>

    /**
     * 插入
     */
    fun insert(@Param("entity") remark: RemarkPO): Int

    /**
     * 更新. 必须提供 [RemarkPO.id]。
     */
    fun update(@Param("entity") remark: RemarkPO): Int

}