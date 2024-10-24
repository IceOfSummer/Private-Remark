package io.github.iceofsummer.privateremark.bean.dto

import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO

data class RemarkHolderDTO(
    /**
     * @see RemarkPO.id
     */
    var remarkId: Int = -1,
    /**
     * 相对于父代码块的偏移, 如果没有父容器，则从文档开始算
     */
    var offsetInParent: Int,
    /**
     * 父容器的名称
     */
    var parentIdentifierName: String
) {
    constructor(remarkHolderPO: RemarkHolderPO) : this(remarkHolderPO.remarkId, remarkHolderPO.offsetInParent!!, remarkHolderPO.parentIdentifierName!!)
}


