// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ClinicalSuggestionList from '@/views/clinical/ClinicalSuggestionList.vue'
import { getSuggestionList, confirmSuggestion, rejectSuggestion } from '@/api/clinical'
import { ElMessage, ElMessageBox } from 'element-plus'

vi.mock('@/api/clinical', () => ({ getSuggestionList: vi.fn(), confirmSuggestion: vi.fn(), rejectSuggestion: vi.fn() }))
vi.mock('element-plus', () => ({ ElMessage: { success: vi.fn(), error: vi.fn() }, ElMessageBox: { confirm: vi.fn() } }))

describe('AI suggestion page', () => {
  beforeEach(() => { vi.clearAllMocks(); getSuggestionList.mockResolvedValue({ data: { records: [{ id: 1, status: 'PENDING' }], total: 1 } }) })
  it('loads pending suggestions on mount', async () => {
    mount(ClinicalSuggestionList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises()
    expect(getSuggestionList).toHaveBeenCalledWith(expect.objectContaining({ status: 'PENDING' }))
  })
  it('confirms and rejects suggestions then reloads', async () => {
    ElMessageBox.confirm.mockResolvedValue(); confirmSuggestion.mockResolvedValue(); rejectSuggestion.mockResolvedValue()
    const wrapper = mount(ClinicalSuggestionList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises()
    await wrapper.vm.handleConfirm({ id: 7 }); await wrapper.vm.handleReject({ id: 8 }); await flushPromises()
    expect(confirmSuggestion).toHaveBeenCalledWith(7); expect(rejectSuggestion).toHaveBeenCalledWith(8)
    expect(ElMessage.success).toHaveBeenCalledWith('建议已落库')
    expect(ElMessage.success).toHaveBeenCalledWith('建议已驳回')
  })
})
