package io.github.iceofsummer.privateremark.svc

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkHolderPO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO
import io.github.iceofsummer.privateremark.svc.factory.MultiImplementOnRuntime
import io.github.iceofsummer.privateremark.svc.factory.RemarkServiceFactory

@MultiImplementOnRuntime(RemarkServiceFactory::class)
interface RemarkServiceV2 {

    /**
     * 保存备注.
     * @param remarkDTO 备注数据. 不需要设置 [RemarkPO.id]
     * @return [RemarkPO] 保存后的数据，会自动设置 [RemarkPO.id]
     */
    fun saveRemark(remarkDTO: RemarkDTO): RemarkPO

    /**
     * 获取所有的备注
     */
    fun resolveAllRemarks(path: String): List<RemarkPO>

    /**
     * 获取所有失效的备注
     */
    fun resolveAllInvalidRemarks(path: String): List<RemarkPO>

    /**
     * 根据 [RemarkPO.id] 获取 [RemarkHolderPO]
     */
    fun getRemarkHolderById(id: Int): RemarkHolderPO?

    /**
     * 标记备注失效且无法自动修复
     */
    fun markRemarkInvalid(id: Int)

    /**
     * 修复备注位置
     * @param id [RemarkPO.id]
     * @param remarkFixDTO 新位置的相关参数
     */
    fun fixRemark(id: Int, remarkFixDTO: RemarkFixDTO)

}