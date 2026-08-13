import { maskSensitiveText, truncateText } from './requirementFormat'

export const HISTORY_STORAGE_KEY = 'platform_requirement_history'
export const MAX_HISTORY_COUNT = 20

export function createHistoryItem(requirement, result, now = new Date()) {
  const maskedRequirement = maskSensitiveText(requirement)
  return {
    id: `${now.getTime()}-${Math.random().toString(16).slice(2)}`,
    title: maskSensitiveText(result?.summary || truncateText(maskedRequirement, 24)),
    requirement: maskedRequirement,
    createdAt: now.toISOString(),
    status: '已生成',
    result: maskResult(result)
  }
}

export function parseHistory(rawHistory) {
  if (!rawHistory) return []
  try {
    const parsedHistory = JSON.parse(rawHistory)
    return Array.isArray(parsedHistory)
      ? parsedHistory.map(normalizeHistoryItem).filter(Boolean).slice(0, MAX_HISTORY_COUNT)
      : []
  } catch {
    return []
  }
}

export function normalizeHistoryItem(item) {
  if (!item || typeof item !== 'object' || !item.id || !item.requirement) return null
  const requirement = maskSensitiveText(item.requirement)
  return {
    id: String(item.id),
    title: maskSensitiveText(item.title || truncateText(requirement, 24)),
    requirement,
    createdAt: item.createdAt || item.createTime || '',
    status: item.status || '已生成',
    result: maskResult(item.result)
  }
}

function maskResult(result) {
  if (!result || typeof result !== 'object') return null
  return {
    ...result,
    summary: maskSensitiveText(result.summary || ''),
    risk: result.risk ? maskSensitiveText(result.risk) : result.risk,
    tasks: Array.isArray(result.tasks)
      ? result.tasks.map((task) => ({
          ...task,
          title: maskSensitiveText(task.title || ''),
          description: maskSensitiveText(task.description || ''),
          acceptanceCriteria: task.acceptanceCriteria
            ? maskSensitiveText(task.acceptanceCriteria)
            : task.acceptanceCriteria
        }))
      : []
  }
}
