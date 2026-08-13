import { describe, expect, it, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'

const chartInstances = []
vi.mock('echarts', () => ({ init: vi.fn(() => { const instance={setOption:vi.fn(),resize:vi.fn(),dispose:vi.fn()}; chartInstances.push(instance); return instance }) }))
vi.mock('@/api/dashboard', () => ({
  getStatsOverview: vi.fn(), getBpTrend: vi.fn(), getGlucoseTrend: vi.fn(), getDoctorComparison: vi.fn()
}))

import * as echarts from 'echarts'
import * as api from '@/api/dashboard'
import Dashboard from '@/views/dashboard/Dashboard.vue'

describe('Dashboard组件', () => {
  beforeEach(() => { chartInstances.length=0; api.getStatsOverview.mockResolvedValue({data:{totalPatients:10,completionRate:90,highRiskCount:2,lostFollowUpCount:1}}); api.getBpTrend.mockResolvedValue({data:[{month:'01',rate:80}]}); api.getGlucoseTrend.mockResolvedValue({data:[]}); api.getDoctorComparison.mockResolvedValue({data:[]}) })
  it('挂载后请求四个接口', async () => { mount(Dashboard); await flushPromises(); expect(api.getStatsOverview).toHaveBeenCalledOnce(); expect(api.getDoctorComparison).toHaveBeenCalledOnce() })
  it('统计卡片展示接口数据', async () => { const wrapper=mount(Dashboard); await flushPromises(); expect(wrapper.text()).toContain('10'); expect(wrapper.text()).toContain('90') })
  it('初始化两个ECharts实例', async () => { mount(Dashboard); await flushPromises(); expect(echarts.init).toHaveBeenCalledTimes(2); expect(chartInstances.every(x=>x.setOption.mock.calls.length===1)).toBe(true) })
  it('空趋势数据不会崩溃', async () => { api.getBpTrend.mockResolvedValue({data:null}); const wrapper=mount(Dashboard); await flushPromises(); expect(wrapper.exists()).toBe(true) })
  it('接口失败页面仍保持挂载', async () => { api.getStatsOverview.mockRejectedValue(new Error('offline')); const wrapper=mount(Dashboard); await flushPromises(); expect(wrapper.text()).toContain('管理患者总数') })
  it('resize事件调整全部图表', async () => { mount(Dashboard); await flushPromises(); window.dispatchEvent(new Event('resize')); expect(chartInstances.every(x=>x.resize.mock.calls.length===1)).toBe(true) })
  it('卸载时释放全部图表', async () => { const wrapper=mount(Dashboard); await flushPromises(); wrapper.unmount(); expect(chartInstances.every(x=>x.dispose.mock.calls.length===1)).toBe(true) })
  it('卸载后resize不再触发已释放实例', async () => { const wrapper=mount(Dashboard); await flushPromises(); wrapper.unmount(); window.dispatchEvent(new Event('resize')); expect(chartInstances.every(x=>x.resize.mock.calls.length===0)).toBe(true) })
})
