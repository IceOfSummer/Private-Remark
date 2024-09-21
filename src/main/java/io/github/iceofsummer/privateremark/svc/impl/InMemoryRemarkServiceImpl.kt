package io.github.iceofsummer.privateremark.svc.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import io.github.iceofsummer.privateremark.bean.Remark
import io.github.iceofsummer.privateremark.svc.RemarkService
import io.github.iceofsummer.privateremark.util.RemarkGenerator
import java.util.Collections


private val VirtualFile.hashKey: String
    get() = url


/**
 * 将备注保存到内存中，用于测试使用.
 */
object InMemoryRemarkServiceImpl: RemarkService {

    val remarksMap: MutableMap<String, MutableSet<Remark>> = HashMap()

    val invalidRemarks: MutableMap<String, MutableSet<Remark>> = HashMap()


    override fun saveRemark(content: String, editor: Editor, psi: PsiFile?): Remark {
        val remark = RemarkGenerator.generateRemark(content, editor.document.getLineNumber(editor.caretModel.offset), editor, psi)
        remarksMap.getOrPut(editor.virtualFile.url, {
            mutableSetOf()
        }).add(remark)
        return remark
    }


    override fun saveRemark(file: VirtualFile, remark: Remark) {
        remarksMap.getOrPut(file.hashKey, {
            mutableSetOf()
        }).add(remark)
    }

    override fun resolveRemarks(file: VirtualFile): Set<Remark> {
        return remarksMap.get(file.hashKey) ?: emptySet()
    }

    override fun resolveInvalidRemarks(file: VirtualFile): Set<Remark> {
        return invalidRemarks.get(file.hashKey) ?: emptySet()
    }

    override fun resolveFileRemark(file: VirtualFile, lineNumber: Int): Remark? {
        return remarksMap.getOrDefault(file.hashKey, Collections.emptyList()).find { r -> r.lineNumber == lineNumber }
    }

    override fun updateFileRemark(file: VirtualFile, lineNumber: Int, newRemark: Remark) {
        val arr = remarksMap[file.hashKey] ?: return
        if (arr.removeIf { it.lineNumber == lineNumber }) {
            arr.add(newRemark)
        }
    }

    override fun updateFileRemarks(file: VirtualFile, remarks: MutableSet<Remark>) {
        remarksMap[file.hashKey] = remarks
    }

    override fun invalidFileRemark(file: VirtualFile, lineNumber: Int) {
        val arr = remarksMap[file.hashKey] ?: return
        val iterator = arr.iterator()

        var target: Remark? = null
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.lineNumber == lineNumber) {
                iterator.remove()
                target = next
                break
            }
        }
        if (target == null) {
            return
        }
        invalidRemarks.getOrPut(file.hashKey, { mutableSetOf() }).add(target)
    }

    override fun moveInvalidRemark(file: VirtualFile): Set<Remark>? {
        // TODO really need delete original?
        return invalidRemarks[file.hashKey]
    }

    override fun updateInvalidRemark(file: VirtualFile, invalid: MutableSet<Remark>) {
        invalidRemarks[file.hashKey] = invalid
    }

    override fun markRequireManualFix(file: VirtualFile, invalid: MutableSet<Remark>) {
        // TODO("Not yet implemented")
    }

    fun clearRemarks() {
        remarksMap.clear()
    }



}