package io.github.iceofsummer.privateremark.bean

data class RemarkVcs (
    /**
     * vcs 类型
     */
    var type: VcsType,
    /**
     * 版本号
     */
    var version: String
)

enum class VcsType {
    GIT,
    SVN
}