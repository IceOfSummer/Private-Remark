package io.github.iceofsummer.privateremark.svc

import io.github.iceofsummer.privateremark.bean.dto.RemarkDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkInsertDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkFixDTO
import io.github.iceofsummer.privateremark.bean.dto.RemarkHolderDTO
import io.github.iceofsummer.privateremark.bean.po.RemarkPO

interface RemarkServiceV2 {

    /**
     * 保存备注.
     * @param remarkInsertDTO 备注数据. 不需要设置 [RemarkPO.id]
     * @return 新备注的id
     */
    fun saveRemark(remarkInsertDTO: RemarkInsertDTO): Int

    /**
     * 获取所有有效的备注
     */
    fun resolveAllValidRemarks(path: String): List<RemarkDTO>

    /**
     * 获取所有失效的备注
     */
    fun resolveAllInvalidRemarks(path: String): List<RemarkDTO>

    /**
     * 根据 [RemarkPO.id] 获取 [RemarkHolderDTO]
     */
    fun getRemarkHolderById(id: Int): RemarkHolderDTO?

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