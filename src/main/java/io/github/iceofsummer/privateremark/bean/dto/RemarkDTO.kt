package io.github.iceofsummer.privateremark.bean.dto

import io.github.iceofsummer.privateremark.bean.po.RemarkPO

data class RemarkDTO(
    /**
     * 唯一 id.
     */
    var id: Int,
    /**
     * 该备注在哪个文件上, 相对路径
     */
    var path: String,
    /**
     * 行号
     */
    var lineNumber: Int,
    /**
     * 备注内容
     */
    var content: String,
    /**
     * 当前行的内容
     */
    var currentLineContent: String,
    /**
     * 备注是否失效. 当该值为 true 时，表示备注无缝自动修复.
     */
    var isInvalid: Boolean
) {

    constructor(remarkPO: RemarkPO) : this(
        remarkPO.id,
        remarkPO.path!!,
        remarkPO.lineNumber!!,
        remarkPO.content!!,
        remarkPO.currentLineContent!!,
        remarkPO.isInvalid!!
    )

}