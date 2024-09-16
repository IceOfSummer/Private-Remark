package io.github.iceofsummer.privateremark.mapper.common

import io.github.iceofsummer.privateremark.mapper.inter.MetadataMapper
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.sql.Connection

class DatabaseVersionManager(private val sqlSessionFactory: SqlSessionFactory) {

    private val log = LoggerFactory.getLogger(DatabaseVersionManager::class.java)

    companion object {
        const val LATEST_VERSION = 0
    }

    private fun readClasspathResource(classpathResourcePath: String): String {
        val resource = DatabaseVersionManager::class.java.classLoader.getResourceAsStream(classpathResourcePath)
            ?: throw IllegalArgumentException("classpath resource could not found: $classpathResourcePath")

        resource.use { reader ->
            ByteArrayOutputStream().use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (reader.read(buffer).also { length = it } != -1) {
                    output.write(buffer, 0, length)
                }
                return output.toString(StandardCharsets.UTF_8.name())
            }
        }
    }


    private fun executeSQL(connection: Connection, path: String) {
        val sql = readClasspathResource(path)

        log.info("Trying to exec sql file '$sql' with database url '${connection.metaData.url}'")
        connection.createStatement().use { statement ->
            // ENHANCE: use better sql parser.
            for (s in sql.split(';')) {
                if (s.isEmpty()) {
                    continue
                }
                statement.addBatch(s)
            }
            statement.executeBatch()
        }
    }


    fun doPatch() {
        sqlSessionFactory.openSession().use { session ->
            var version: Int
            val metadataMapper = session.getMapper(MetadataMapper::class.java)
            if (metadataMapper.tableCount() == 0) {
                version = 0
                executeSQL(session.connection, "sql/init.sql")
            } else {
                val metadataPO = metadataMapper.selectByName(MetadataKeys.DATABASE_VERSION)
                    ?: throw IllegalStateException("Broken database! Can not resolve database version!")
                version = metadataPO.value.toInt()
            }
            while (version++ < LATEST_VERSION) {
                // update database version in patch file.
                executeSQL(session.connection, "sql/patch-$version.sql")
            }
        }
    }




}