package io.github.iceofsummer.privateremark.bean.dto

import io.github.iceofsummer.privateremark.bean.po.RemarkVcs

data class RemarkInsertDTO(
    var remark: RemarkDTO,
    var vcs: RemarkVcs? = null,
    var holder: RemarkHolderDTO? = null
)