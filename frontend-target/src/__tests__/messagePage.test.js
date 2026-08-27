// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import MessageList from '@/views/message/MessageList.vue'
import { getMessageList, createMessage } from '@/api/message'
import { ElMessage } from 'element-plus'

vi.mock('@/api/message', () => ({ getMessageList: vi.fn(), createMessage: vi.fn() }))
vi.mock('element-plus', () => ({ ElMessage: { success: vi.fn(), error: vi.fn() } }))

describe('message center page', () => {
  beforeEach(() => { vi.clearAllMocks(); getMessageList.mockResolvedValue({ data: { records: [], total: 0 } }) })
  it('loads the message list on mount', async () => {
    mount(MessageList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises(); expect(getMessageList).toHaveBeenCalled()
  })
  it('validates and sends the composed message', async () => {
    createMessage.mockResolvedValue(); const wrapper = mount(MessageList, { global: { stubs: { 'el-form': true, 'el-form-item': true, 'el-select': true, 'el-option': true, 'el-button': true, 'el-table': true, 'el-table-column': true, 'el-tag': true, 'el-pagination': true, 'el-dialog': true, 'el-input': true, 'el-input-number': true, 'el-date-picker': true, 'el-alert': true } } }); await flushPromises()
    wrapper.vm.formRef = { validate: vi.fn().mockResolvedValue(), resetFields: vi.fn() }
    Object.assign(wrapper.vm.formData, { recipientType: 'PATIENT', recipientId: 9, channel: 'SMS', title: '复诊', content: '请按时复诊' })
    await wrapper.vm.handleSubmit(); await flushPromises()
    expect(createMessage).toHaveBeenCalledWith(expect.objectContaining({ recipientId: 9, channel: 'SMS', title: '复诊' }))
    expect(ElMessage.success).toHaveBeenCalledWith('消息已发送')
  })
})
