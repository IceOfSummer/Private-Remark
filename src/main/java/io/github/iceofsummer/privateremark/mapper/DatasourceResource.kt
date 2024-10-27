package io.github.iceofsummer.privateremark.mapper

import io.github.iceofsummer.privateremark.mapper.common.SqlSessionTemplate
import org.apache.ibatis.session.SqlSessionFactory

data class DatasourceResource (
    var sqlSessionFactory: SqlSessionFactory,
    var sqlSessionTemplate: SqlSessionTemplate? = null
)