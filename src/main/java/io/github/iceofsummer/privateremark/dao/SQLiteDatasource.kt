package io.github.iceofsummer.privateremark.dao

import ai.grazie.utils.WeakHashMap
import java.sql.Connection
import java.util.concurrent.locks.ReadWriteLock


object SQLiteDatasource {

    /**
     * 数据库连接。也许需要一个连接池来管理?
     */
    private val connection: WeakHashMap<String, Pair<Connection, ReadWriteLock>> = WeakHashMap()





}