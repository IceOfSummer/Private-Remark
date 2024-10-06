package io.github.iceofsummer.privateremark.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.iceofsummer.privateremark.dao.repo.MetadataDao
import javax.sql.DataSource
import kotlin.reflect.KClass


object RepositoryManager {

    /**
     * 数据库连接。也许需要一个连接池来管理?
     */
    private val datasource: MutableMap<String, DataSource> = mutableMapOf()


    private fun createDatasource(localDatabaseFilePath: String): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite:$localDatabaseFilePath"
            // No datasource release before project close.
            minimumIdle = 0
            maximumPoolSize = 2
            idleTimeout = 120 * 1000
            addDataSourceProperty("cachePrepStmts", "true");
            addDataSourceProperty("prepStmtCacheSize", "50");
            addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
        }
        return HikariDataSource(config)
    }

    fun <T : Repository> getDataSource(localDatabaseFilePath: String, clazz: KClass<T>): Repository {
        val dataSource = datasource.getOrPut(localDatabaseFilePath, { createDatasource(localDatabaseFilePath) })
        val repo = initRepo(clazz)
        repo.init(dataSource)
        return repo
    }

    private fun <T : Repository> initRepo(
        clazz: KClass<T>
    ): MetadataDao {
        // no reflect for now.
        when (clazz) {
            MetadataDao::class -> return MetadataDao()
            else -> throw IllegalArgumentException("No such dao, class: $clazz")
        }
    }


}