// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import FollowUpTaskList from '@/views/followUp/FollowUpTaskList.vue'
import { getTaskList, completeTask, cancelTask } from '@/api/followUpTask'
import { ElMessage, ElMessageBox } from 'element-plus'

vi.mock('@/api/followUpTask', () => ({ getTaskList: vi.fn(), completeTask: vi.fn(), cancelTask: vi.fn() }))
vi.mock('element-plus', () => ({ ElMessage: { success: vi.fn() }, ElMessageBox: { confirm: vi.fn() } }))

describe('follow-up task page', () => {
  beforeEach(() => { vi.clearAllMocks(); getTaskList.mockResolvedValue({ data: { records: [], total: 0 } }); ElMessageBox.confirm.mockResolvedValue() })
  it('loads tasks on mount', async () => { mount(FollowUpTaskList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises(); expect(getTaskList).toHaveBeenCalled() })
  it('completes and cancels active tasks', async () => {
    completeTask.mockResolvedValue(); cancelTask.mockResolvedValue(); const wrapper = mount(FollowUpTaskList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises()
    await wrapper.vm.handleComplete({ id: 3 }); await wrapper.vm.handleCancel({ id: 4 }); await flushPromises()
    expect(completeTask).toHaveBeenCalledWith(3); expect(cancelTask).toHaveBeenCalledWith(4)
  })
})
