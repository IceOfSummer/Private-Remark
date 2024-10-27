package io.github.iceofsummer.privateremark.mapper.common

import org.apache.ibatis.session.SqlSession

data class SqlSessionHolder(
    var sqlSession: SqlSession,
    var refCount: Int
)
