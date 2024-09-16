package io.github.iceofsummer.privateremark.mapper

import io.github.iceofsummer.privateremark.mapper.common.MetadataKeys
import io.github.iceofsummer.privateremark.mapper.common.DatabaseVersionManager
import io.github.iceofsummer.privateremark.mapper.inter.MetadataMapper
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class DatasourceManagerTest {


    companion object {

        const val TEMP_OUTPUT_DIRECTORY = "./test-generated"

        @BeforeClass
        @JvmStatic
        fun ensureDirectory() {
            File(TEMP_OUTPUT_DIRECTORY).mkdirs()
        }

    }


    @Test
    fun testCreateDatabase() {
        val target = "$TEMP_OUTPUT_DIRECTORY/testCreateDatabase.sqlite"
        // test repo init
        val sqlSessionFactory = DatasourceManager.getSqlSessionFactory(target)

        sqlSessionFactory.openSession().use { session ->
            val metadataMapper = session.getMapper(MetadataMapper::class.java)
            val file = File(target)
            Assert.assertTrue(file.exists())

            val version = metadataMapper.selectByName(MetadataKeys.DATABASE_VERSION)
            Assert.assertNotNull(version)
            Assert.assertEquals(version!!.value.toInt(), DatabaseVersionManager.LATEST_VERSION)
        }


    }


}