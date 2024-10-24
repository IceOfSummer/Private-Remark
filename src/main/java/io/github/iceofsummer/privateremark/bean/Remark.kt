package io.github.iceofsummer.privateremark.bean

import io.github.iceofsummer.privateremark.bean.po.RemarkVcs

/**
 * 用于 java 文件(暂定)的备注
 */
data class Remark(
    /**
     * 唯一 id.
     */
    var id: String,
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
    /**
     * 父容器指示器
     */
    var parentIndicator: ParentIndicator? = null,
    /**
     * 当前行的内容
     */
    var lineContent: String = "",
    /**
     * 版本控制，用于备注失效时提供 review 功能
     */
    var vcs: RemarkVcs? = null
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