package io.github.iceofsummer.privateremark.svc.factory

import com.intellij.openapi.components.service
import io.github.iceofsummer.privateremark.bridge.bean.PersistenceType
import io.github.iceofsummer.privateremark.bridge.conf.IDESettingsStorage
import io.github.iceofsummer.privateremark.mapper.DatasourceManager
import io.github.iceofsummer.privateremark.mapper.inter.RemarkHolderMapper
import io.github.iceofsummer.privateremark.mapper.inter.RemarkMapper
import io.github.iceofsummer.privateremark.svc.RemarkServiceV2
import io.github.iceofsummer.privateremark.svc.impl.InMemoryRemarkServiceImpl
import io.github.iceofsummer.privateremark.svc.impl.LocalStorageRemarkServiceImpl
import io.github.iceofsummer.privateremark.util.ObjectReference
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * 服务管理.
 */
object ServiceManager {


    /**
     * k: factory
     * v: last instance
     */
    private val instanceCache = mutableMapOf<KClass<*>, ServiceHolder>()


    init {
        reloadService()
    }

    private fun enhanceObj(reference: ObjectReference, serviceInterface: Array<Class<*>>): Any {
        return Proxy.newProxyInstance(ServiceManager::class.java.classLoader, serviceInterface, ServiceEnhanceInvocationHandler(reference))
    }

    private fun loadOrReloadService() {
        val config = service<IDESettingsStorage>().state
        // TODO: 将初始化分散开.
        instanceCache.compute(RemarkServiceV2::class) { k, v ->
            val instance = when (config.persistenceType) {
                PersistenceType.IN_MEMORY -> InMemoryRemarkServiceImpl()
                PersistenceType.LOCAL_FILE_SYSTEM -> LocalStorageRemarkServiceImpl().apply {
                    val sqlSessionTemplate =
                        DatasourceManager.getSqlSessionTemplate(config.localFileSystemDirectory)

                    setRemarkMapper(sqlSessionTemplate.getMapper(RemarkMapper::class.java))
                    setRemarkHolderMapper(sqlSessionTemplate.getMapper(RemarkHolderMapper::class.java))
                }
                PersistenceType.NONE -> TODO()
            }

            if (v == null) {
                val reference = ObjectReference(instance)
                return@compute ServiceHolder(reference, enhanceObj(reference, arrayOf(RemarkServiceV2::class.java)))
            }
            v.reference.value = instance
            return@compute v
        }
    }



    /**
     * 获取服务
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getService(clazz: KClass<T>): T {
        val serviceHolder = instanceCache[clazz] ?: throw IllegalArgumentException("No such service")

        return serviceHolder.proxiedObject as T
    }

    /**
     * 重新加载所有服务, 当配置变动时应该调用该方法修改相关实现类.
     */
    fun reloadService() {
        loadOrReloadService()
    }

}