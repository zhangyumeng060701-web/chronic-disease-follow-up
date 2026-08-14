export const TASK_TYPES = [
  { type: 'FRONTEND', label: '前端任务', tagType: 'success' },
  { type: 'BACKEND', label: '后端任务', tagType: 'primary' },
  { type: 'DATABASE', label: '数据库', tagType: 'warning' },
  { type: 'TEST', label: '测试任务', tagType: 'info' },
  { type: 'SECURITY', label: '安全检查', tagType: 'danger' }
]

export function groupTasksByType(tasks = []) {
  const safeTasks = Array.isArray(tasks) ? tasks : []
  return TASK_TYPES.reduce((groups, taskType) => {
    groups[taskType.type] = safeTasks.filter((task) => task?.type === taskType.type)
    return groups
  }, {})
}
