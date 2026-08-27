// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import FollowUpTemplateList from '@/views/followUp/FollowUpTemplateList.vue'
import { getTemplateList, toggleTemplate } from '@/api/template'
import { ElMessage } from 'element-plus'

vi.mock('@/api/template', () => ({ getTemplateList: vi.fn(), createTemplate: vi.fn(), updateTemplate: vi.fn(), toggleTemplate: vi.fn() }))
vi.mock('element-plus', () => ({ ElMessage: { success: vi.fn(), error: vi.fn() } }))

describe('follow-up template page', () => {
  beforeEach(() => { vi.clearAllMocks(); getTemplateList.mockResolvedValue({ data: { records: [], total: 0 } }) })
  it('loads templates on mount', async () => { mount(FollowUpTemplateList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises(); expect(getTemplateList).toHaveBeenCalled() })
  it('toggles template status and reloads', async () => {
    toggleTemplate.mockResolvedValue(); const wrapper = mount(FollowUpTemplateList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises()
    await wrapper.vm.handleToggle({ id: 6, isActive: 1 }); await flushPromises()
    expect(toggleTemplate).toHaveBeenCalledWith(6); expect(ElMessage.success).toHaveBeenCalledWith('模板已停用')
  })
})
