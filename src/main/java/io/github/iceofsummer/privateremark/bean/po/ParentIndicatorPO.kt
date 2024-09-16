package io.github.iceofsummer.privateremark.bean.po

data class ParentIndicatorPO(
    /**
     * @see RemarkPO.id
     */
    var remarkId: Int = -1,
    /**
     * 相对于父代码块的偏移, 如果没有父容器，则从文档开始算
     */
    var startOffsetInParent: Int = 0,
    /**
     * 类名
     */
    var classname: String = "",
    /**
     * 名称
     */
    var name: String = "",
)
