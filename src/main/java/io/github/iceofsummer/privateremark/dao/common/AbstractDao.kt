package io.github.iceofsummer.privateremark.dao.common

import javax.sql.DataSource

abstract class AbstractDao<T> {

    private val datasource: DataSource = TODO()


    protected fun executeQuery(): List<T> {
        datasource.connection.use { conn ->
            val statement = conn.createStatement()
            statement.exe
        }
    }


}