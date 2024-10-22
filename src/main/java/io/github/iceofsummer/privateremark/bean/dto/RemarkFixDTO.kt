package io.github.iceofsummer.privateremark.bean.dto

/**
 * 用于修复备注位置
 */
data class RemarkFixDTO(
    var lineNumber: Int,
    val lineContent: String,
    var offsetInParent: Int?
)
