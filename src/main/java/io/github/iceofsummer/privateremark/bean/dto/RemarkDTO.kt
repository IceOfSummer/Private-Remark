package io.github.iceofsummer.privateremark.bean.dto

import io.github.iceofsummer.privateremark.bean.ParentIndicator
import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.bean.po.RemarkVcs

data class RemarkDTO(
    var remarkPO: RemarkPO,
    var vcs: RemarkVcs? = null,
    var holder: RemarkHolderPO? = null
)