package io.github.iceofsummer.privateremark.dao

import io.github.iceofsummer.privateremark.bean.RemarkPO

interface RemarkDao {

    /**
     * 查询文件上所有的备注
     */
    fun queryAllRemarksByFilepath(filepath: String): List<RemarkPO>



}