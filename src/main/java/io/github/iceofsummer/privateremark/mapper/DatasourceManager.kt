package io.github.iceofsummer.privateremark.mapper

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.iceofsummer.privateremark.mapper.common.DatabaseVersionManager
import io.github.iceofsummer.privateremark.mapper.common.SqlSessionTemplate
import io.github.iceofsummer.privateremark.mapper.inter.MetadataMapper
import io.github.iceofsummer.privateremark.mapper.inter.RemarkHolderMapper
import io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.io.File
import javax.sql.DataSource
import org.slf4j.LoggerFactory


object DatasourceManager {

    private val log = LoggerFactory.getLogger(DatasourceManager::class.java)

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /**
     * 数据库连接。也许需要一个连接池来管理?
     */
    private val datasourceMapping: MutableMap<String, DatasourceResource> = mutableMapOf()

    private fun createConfiguration(localDatabaseFilePath: String, dataSource: DataSource): Configuration {

        val transactionFactory: TransactionFactory = JdbcTransactionFactory()
        val configuration = Configuration(Environment(localDatabaseFilePath, transactionFactory, dataSource))

        configuration.isUseGeneratedKeys = true
        configuration.addMapper(MetadataMapper::class.java)
        configuration.addMapper(RemarkMapper::class.java)
        configuration.addMapper(RemarkHolderMapper::class.java)
        return configuration
    }

    /**
     * 创建一个数据源
     */
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
        log.info("Created a datasource file in $localDatabaseFilePath")
        return HikariDataSource(config)
    }


    private fun getOrCreateResource(localDatabaseFilePath: String): DatasourceResource {
        val file = File(localDatabaseFilePath)
        val dbFile: String = if (file.exists() && file.isDirectory) {
            "$localDatabaseFilePath/db.sqlite"
        } else {
            localDatabaseFilePath
        }

        return datasourceMapping.getOrPut(dbFile) {
            val dataSource = createDatasource(dbFile)

            val sqlSessionFactory = SqlSessionFactoryBuilder().build(createConfiguration(dbFile, dataSource))
            DatabaseVersionManager(sqlSessionFactory).doPatch()
            return@getOrPut DatasourceResource(sqlSessionFactory, null)
        }
    }

    fun getSqlSessionFactory(localDatabaseFilePath: String): SqlSessionFactory {
        return getOrCreateResource(localDatabaseFilePath).sqlSessionFactory
    }


    fun getSqlSessionTemplate(localDatabaseFilePath: String): SqlSessionTemplate {
        val resource = getOrCreateResource(localDatabaseFilePath)
        resource.sqlSessionTemplate?.let { return it }

        val sqlSessionTemplate = SqlSessionTemplate(resource.sqlSessionFactory)
        resource.sqlSessionTemplate = sqlSessionTemplate
        return sqlSessionTemplate
    }

}