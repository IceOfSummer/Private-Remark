package io.github.iceofsummer.privateremark.dao

import java.sql.Connection
import javax.sql.DataSource


/**
 * repository
 */
interface Repository {

    /**
     * 初始化数据库连接
     */
    fun init(datasource: DataSource)

}