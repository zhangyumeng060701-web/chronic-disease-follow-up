// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import FollowUpPlanList from '@/views/followUp/FollowUpPlanList.vue'
import { getPlanList, deletePlan } from '@/api/plan'
import { assessPatientRisk, generateSuggestion } from '@/api/clinical'
import { getPatientList } from '@/api/patient'
import { ElMessage, ElMessageBox } from 'element-plus'

vi.mock('@/api/plan', () => ({ getPlanList: vi.fn(), createPlan: vi.fn(), updatePlan: vi.fn(), deletePlan: vi.fn() }))
vi.mock('@/api/clinical', () => ({ assessPatientRisk: vi.fn(), generateSuggestion: vi.fn() }))
vi.mock('@/api/patient', () => ({ getPatientList: vi.fn() }))
vi.mock('element-plus', () => ({ ElMessage: { success: vi.fn(), error: vi.fn() }, ElMessageBox: { confirm: vi.fn() } }))

describe('follow-up plan page', () => {
  beforeEach(() => { vi.clearAllMocks(); getPlanList.mockResolvedValue({ data: { records: [], total: 0 } }); getPatientList.mockResolvedValue({ data: { records: [] } }) })
  it('loads plans and patient options on mount', async () => {
    mount(FollowUpPlanList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises(); expect(getPlanList).toHaveBeenCalled(); expect(getPatientList).toHaveBeenCalled()
  })
  it('runs risk assessment, AI suggestion and confirmed deletion', async () => {
    assessPatientRisk.mockResolvedValue(); generateSuggestion.mockResolvedValue(); deletePlan.mockResolvedValue(); ElMessageBox.confirm.mockResolvedValue()
    const wrapper = mount(FollowUpPlanList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises()
    await wrapper.vm.handleAssess({ patientId: 9 }); await wrapper.vm.handleSuggest({ patientId: 9 }); await wrapper.vm.handleDelete({ id: 5 }); await flushPromises()
    expect(assessPatientRisk).toHaveBeenCalledWith(9); expect(generateSuggestion).toHaveBeenCalledWith(9); expect(deletePlan).toHaveBeenCalledWith(5)
  })
})
