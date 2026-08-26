import { describe, expect, it, vi } from 'vitest'
import { useTable } from '@/composables/useTable'

describe('useTable', () => {
  it('loads records and pagination', async () => {
    const fetcher = vi.fn().mockResolvedValue({
      data: { records: [{ id: 1 }], total: 1, page: 1, size: 20 }
    })
    const table = useTable({ fetcher })

    await table.load()

    expect(table.tableData.value).toEqual([{ id: 1 }])
    expect(table.pagination.total).toBe(1)
    expect(table.error.value).toBe('')
  })

  it('handles fetch errors', async () => {
    const fetcher = vi.fn().mockRejectedValue(new Error('加载失败'))
    const table = useTable({ fetcher })

    await table.load()

    expect(table.tableData.value).toEqual([])
    expect(table.error.value).toBe('加载失败')
  })

  it('search resets page to 1', async () => {
    const fetcher = vi.fn().mockResolvedValue({ data: { records: [], total: 0 } })
    const table = useTable({ fetcher })
    table.pagination.page = 3

    await table.search()

    expect(table.pagination.page).toBe(1)
  })
})
