package io.github.iceofsummer.privateremark.bean.po

/**
 * PK: [RemarkPO.file] -> [RemarkPO.id]
 * @see io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
 */
data class RemarkPO (
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
    var currentLineContent: String
) {
    /**
     * Only used for serializer.
     */
    constructor() :this(-1, "", -1, "", "")
}
