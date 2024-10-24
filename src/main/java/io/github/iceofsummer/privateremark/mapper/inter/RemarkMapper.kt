package io.github.iceofsummer.privateremark.mapper.inter

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import org.apache.ibatis.annotations.Param

/**
 * @see io.github.iceofsummer.privateremark.bean.po.RemarkPO
 */
interface RemarkMapper {

    /**
     * 根据 [RemarkPO.path] 查询所有的 [RemarkPO]
     * @param path 路径
     * @param invalid 是否仅选择失效或有效的备注。传空表示全选
     */
    fun selectAllByPath(@Param("path") path: String, @Param("invalid") invalid: Boolean?): List<RemarkPO>

    /**
     * 插入
     */
    fun insert(@Param("entity") remark: RemarkDTO): Int

    /**
     * 更新. 必须提供 [RemarkPO.id]。
     */
    fun update(@Param("entity") remark: RemarkPO): Int

}