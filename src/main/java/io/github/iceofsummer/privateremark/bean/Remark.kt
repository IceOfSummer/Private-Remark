package io.github.iceofsummer.privateremark.bean

/**
 * 用于 java 文件(暂定)的备注
 */
data class Remark(
    /**
     * 父容器指示器
     */
    var parentIndicator: ParentIndicator? = null,
    /**
     * 相对于父代码块的偏移, 如果没有父容器，则从文档开始算
     */
    var startOffsetInParent: Int = 0,
    /**
     * 行号
     */
    var lineNumber: Int = 0,
    /**
     * 备注内容
     */
    var content: String = "",
)

data class ParentIndicator(
    /**
     * 类名
     */
    var classname: String = "",
    /**
     * 名称
     */
    var name: String = "",
)