import { afterEach, vi } from 'vitest'
import { config } from '@vue/test-utils'

class ResizeObserverMock {
  observe() {}
  unobserve() {}
  disconnect() {}
}

globalThis.ResizeObserver = ResizeObserverMock
window.matchMedia = window.matchMedia || (() => ({
  matches: false, addListener() {}, removeListener() {},
  addEventListener() {}, removeEventListener() {}, dispatchEvent() { return false }
}))
HTMLCanvasElement.prototype.getContext = vi.fn(() => ({}))

config.global.stubs = {
  'el-row': { template: '<div><slot /></div>' },
  'el-col': { template: '<div><slot /></div>' },
  'el-card': { template: '<section><slot name="header"/><slot /></section>' },
  'el-table': { template: '<div><slot /></div>' },
  'el-table-column': true,
  'el-container': { template: '<div><slot /></div>' },
  'el-aside': { template: '<aside><slot /></aside>' },
  'el-header': { template: '<header><slot /></header>' },
  'el-main': { template: '<main><slot /></main>' },
  'el-menu': { template: '<nav><slot /></nav>' },
  'el-menu-item': { template: '<div><slot /></div>' },
  'el-sub-menu': { template: '<div><slot name="title"/><slot /></div>' },
  'el-icon': { template: '<i><slot /></i>' },
  'el-button': { template: '<button @click="$emit(\'click\')"><slot /></button>' },
  'router-view': true
}

afterEach(() => localStorage.clear())
