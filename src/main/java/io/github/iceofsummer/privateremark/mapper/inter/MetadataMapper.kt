package io.github.iceofsummer.privateremark.mapper.inter

import io.github.iceofsummer.privateremark.bean.po.MetadataPO
import io.github.iceofsummer.privateremark.mapper.common.MetadataKeys
import org.apache.ibatis.annotations.Param

/**
 * @see io.github.iceofsummer.privateremark.bean.po.MetadataPO
 */
interface MetadataMapper {

    /**
     * 根据名称获取元数据
     */
    fun selectByName(@Param("key") key: MetadataKeys): MetadataPO?

    /**
     * 返回数据库表的数据量
     */
    fun tableCount(): Int


}