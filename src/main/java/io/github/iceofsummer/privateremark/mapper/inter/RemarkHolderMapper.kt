package io.github.iceofsummer.privateremark.mapper.inter

import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import org.apache.ibatis.annotations.Param

/**
 * @see io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
 */
interface RemarkHolderMapper {

    /**
     * 插入
     * @return affected rows
     */
    fun insert(@Param("entity") remarkHolderPO: RemarkHolderPO): Int

    /**
     * 根据 [RemarkHolderPO.remarkId] 获取 [RemarkHolderPO]
     */
    fun selectByRemarkId(@Param("id") remarkId: Int): RemarkHolderPO?

    /**
     * 更新
     */
    fun update(@Param("entity") remarkHolder: RemarkHolderPO): Int

}