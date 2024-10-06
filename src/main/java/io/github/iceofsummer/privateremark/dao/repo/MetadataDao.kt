package io.github.iceofsummer.privateremark.dao.repo

import io.github.iceofsummer.privateremark.bean.po.MetadataPO
import io.github.iceofsummer.privateremark.dao.Repository
import java.sql.ResultSet
import javax.sql.DataSource

class MetadataDao : Repository {

    companion object {
        const val TABLE_NAME = "metadata"
    }

    private lateinit var datasource: DataSource

    override fun init(datasource: DataSource) {
        this.datasource = datasource
    }

    private fun resultSetToEntity(resultSet: ResultSet): MetadataPO {
        return MetadataPO(resultSet.getString(0), resultSet.getString(1))
    }


    fun queryById(name: String): MetadataPO? {
        datasource.connection.use { conn ->
            val statement = conn.prepareStatement("SELECT value FROM $TABLE_NAME where name = ?")

            statement.setString(0, name)

            val rs = statement.executeQuery()

            // ignore extra content
            if (rs.next()) {
                return resultSetToEntity(rs)
            }
            return null
        }
    }

    fun updateOrInsert(metadataPO: MetadataPO): Int {
        datasource.connection.use { conn ->
            val statement = conn.prepareStatement("UPDATE $TABLE_NAME SET value = ? WHERE name = ?")
            statement.setString(0, metadataPO.value)
            statement.setString(1, metadataPO.name)

            return statement.executeUpdate()
        }
    }



}