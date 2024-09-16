package io.github.iceofsummer.privateremark.bean.po

data class RemarkVcs (
    /**
     * @see RemarkPO.id
     */
    var remarkId: Int = -1,
    /**
     * vcs 类型
     */
    var type: VcsType = VcsType.NONE,
    /**
     * 版本号
     */
    var version: String = ""
) {

    enum class VcsType {
        GIT,
        SVN,
        NONE
    }

}

