package io.github.iceofsummer.privateremark.mapper.common

import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory

object SqlSessionThreadLocalManager {

    private val sqlSessionLocal = ThreadLocal<SqlSessionHolder?>()

    private val isTransactionEnabledLocal = ThreadLocal.withInitial { false }

    fun isTransactionEnabled(): Boolean {
        return isTransactionEnabledLocal.get()
    }

    fun enableTransaction() {
        isTransactionEnabledLocal.set(true)
    }

    fun getSqlSession(sqlSessionFactory: SqlSessionFactory): SqlSession {
        if (isTransactionEnabled()) {
            val old = sqlSessionLocal.get()
            if (old != null) {
                old.refCount++
                return old.sqlSession
            }
            val session = sqlSessionFactory.openSession(false)
            sqlSessionLocal.set(SqlSessionHolder(session, 1))
            return session
        }
        return sqlSessionFactory.openSession(true)
    }


    fun clearBind() {
        sqlSessionLocal.remove()
        isTransactionEnabledLocal.remove()
    }

    /**
     * 获取 [SqlSessionHolder]
     */
    fun getSqlSessionHolder(): SqlSessionHolder? {
        return sqlSessionLocal.get()
    }

}