package io.github.iceofsummer.privateremark.mapper.common

import org.apache.ibatis.cursor.Cursor
import org.apache.ibatis.executor.BatchResult
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.Connection


/**
 * prototype: [SqlSessionTemplate](https://github.com/mybatis/spring/blob/a84c1605a32451a43c933f16807b734c10803434/src/main/java/org/mybatis/spring/SqlSessionTemplate.java#L329)
 */
class SqlSessionTemplate(private val sqlSessionFactory: SqlSessionFactory) : SqlSession {


    private val proxiedSqlSession: SqlSession


    init {
        proxiedSqlSession =
            Proxy.newProxyInstance(this.javaClass.classLoader, arrayOf(SqlSession::class.java), ProxiedSqlSession()) as SqlSession
    }

    inner class ProxiedSqlSession : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any {
            val session = SqlSessionThreadLocalManager.getSqlSession(sqlSessionFactory)

            try {
                val result = method.invoke(session, *args)
                return result
            } finally {
                SqlSessionThreadLocalManager.getSqlSessionHolder()?.let {
                    it.refCount--
                } ?: let {
                    // no transaction.
                    session.close()
                }
            }
        }
    }


    override fun close() {
        throw UnsupportedOperationException("Manual close is not allowed!")
    }

    override fun <T : Any?> selectOne(statement: String?): T {
        return proxiedSqlSession.selectOne(statement)
    }

    override fun <T : Any?> selectOne(statement: String?, parameter: Any?): T {
        return proxiedSqlSession.selectOne(statement, parameter)
    }

    override fun <E : Any?> selectList(statement: String?): MutableList<E> {
        return proxiedSqlSession.selectList(statement)
    }

    override fun <E : Any?> selectList(statement: String?, parameter: Any?): MutableList<E> {
        return proxiedSqlSession.selectList(statement, parameter)
    }

    override fun <E : Any?> selectList(statement: String?, parameter: Any?, rowBounds: RowBounds?): MutableList<E> {
        return proxiedSqlSession.selectList(statement, parameter, rowBounds)
    }

    override fun <K : Any?, V : Any?> selectMap(statement: String?, mapKey: String?): MutableMap<K, V> {
        return proxiedSqlSession.selectMap(statement, mapKey)
    }

    override fun <K : Any?, V : Any?> selectMap(
        statement: String?,
        parameter: Any?,
        mapKey: String?
    ): MutableMap<K, V> {
        return proxiedSqlSession.selectMap(statement, parameter, mapKey)
    }

    override fun <K : Any?, V : Any?> selectMap(
        statement: String?,
        parameter: Any?,
        mapKey: String?,
        rowBounds: RowBounds?
    ): MutableMap<K, V> {
        return proxiedSqlSession.selectMap(statement, parameter, mapKey, rowBounds)
    }

    override fun <T : Any?> selectCursor(statement: String?): Cursor<T> {
        return proxiedSqlSession.selectCursor(statement)
    }

    override fun <T : Any?> selectCursor(statement: String?, parameter: Any?): Cursor<T> {
        return proxiedSqlSession.selectCursor(statement, parameter)
    }

    override fun <T : Any?> selectCursor(statement: String?, parameter: Any?, rowBounds: RowBounds?): Cursor<T> {
        return proxiedSqlSession.selectCursor(statement, parameter, rowBounds)
    }

    override fun select(statement: String?, parameter: Any?, handler: ResultHandler<*>?) {
        return proxiedSqlSession.select(statement, parameter, handler)
    }

    override fun select(statement: String?, handler: ResultHandler<*>?) {
        return proxiedSqlSession.select(statement, handler)
    }

    override fun select(statement: String?, parameter: Any?, rowBounds: RowBounds?, handler: ResultHandler<*>?) {
        return proxiedSqlSession.select(statement, parameter, rowBounds, handler)
    }

    override fun insert(statement: String?): Int {
        return proxiedSqlSession.insert(statement)
    }

    override fun insert(statement: String?, parameter: Any?): Int {
        return proxiedSqlSession.insert(statement, parameter)
    }

    override fun update(statement: String?): Int {
        return proxiedSqlSession.update(statement)
    }

    override fun update(statement: String?, parameter: Any?): Int {
        return proxiedSqlSession.update(statement, parameter)
    }

    override fun delete(statement: String?): Int {
        return proxiedSqlSession.delete(statement)
    }

    override fun delete(statement: String?, parameter: Any?): Int {
        return proxiedSqlSession.delete(statement, parameter)
    }

    override fun commit() {
        throw UnsupportedOperationException("Manual commit is not allowed!")
    }

    override fun commit(force: Boolean) {
        throw UnsupportedOperationException("Manual commit is not allowed!")
    }

    override fun rollback() {
        throw UnsupportedOperationException("Manual rollback is not allowed!")
    }

    override fun rollback(force: Boolean) {
        throw UnsupportedOperationException("Manual rollback is not allowed!")
    }

    override fun flushStatements(): MutableList<BatchResult> {
        return proxiedSqlSession.flushStatements()
    }

    override fun clearCache() {
        return proxiedSqlSession.clearCache()
    }

    override fun getConfiguration(): Configuration {
        return this.sqlSessionFactory.getConfiguration();
    }

    override fun <T : Any?> getMapper(type: Class<T>?): T {
        return configuration.getMapper(type, this);
    }

    override fun getConnection(): Connection {
        return proxiedSqlSession.connection
    }


}