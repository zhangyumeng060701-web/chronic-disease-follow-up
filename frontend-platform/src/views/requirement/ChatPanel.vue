<template>
  <div class="chat-panel">
    <aside class="history-panel">
      <div class="history-header">
        <div class="panel-title">
          <el-icon>
            <Clock />
          </el-icon>
          <span>历史需求</span>
        </div>
        <el-button
          v-if="historyList.length > 0"
          link
          type="danger"
          :disabled="submitting"
          @click="handleClearAllHistory"
        >
          清空全部
        </el-button>
      </div>

      <div v-if="historyList.length > 0" class="history-list">
        <article
          v-for="item in historyList"
          :key="item.id"
          class="history-item"
          :class="{ active: item.id === activeHistoryId }"
          role="button"
          tabindex="0"
          @click="selectHistory(item)"
          @keydown.enter="selectHistory(item)"
        >
          <div class="history-item-content">
            <span class="history-title">{{ displayText(item.title) }}</span>
            <div class="history-meta">
              <time class="history-time">{{ formatHistoryTime(item.createdAt) }}</time>
              <el-tag size="small" type="success">{{ item.status }}</el-tag>
            </div>
          </div>
          <el-button
            link
            type="danger"
            size="small"
            :disabled="submitting"
            @click.stop="handleDeleteHistory(item)"
          >
            删除
          </el-button>
        </article>
      </div>

      <el-empty v-else description="暂无历史需求" />
    </aside>

    <section class="main-panel">
      <section class="input-area" aria-labelledby="requirement-input-title">
        <div class="panel-title page-title">
          <el-icon>
            <EditPen />
          </el-icon>
          <h1 id="requirement-input-title">需求输入</h1>
        </div>

        <el-form label-position="top">
          <el-form-item label="主需求" required>
            <el-input
              v-model="requirement"
              type="textarea"
              :rows="5"
              :maxlength="MAX_REQUIREMENT_LENGTH"
              show-word-limit
              resize="none"
              :disabled="submitting"
              placeholder="请输入您的需求，例如：在随访记录页面增加按随访日期范围筛选的功能，并支持查询结果分页。"
            />
          </el-form-item>
        </el-form>

        <div class="input-actions">
          <el-button
            type="primary"
            :loading="submitting"
            :disabled="!canSubmit"
            @click="handleSubmit"
          >
            开始拆解
          </el-button>
          <el-button :disabled="submitting" @click="handleNewRequirement">新建需求</el-button>
        </div>
      </section>

      <section
        v-loading="submitting"
        class="result-area"
        element-loading-text="正在拆解需求，请稍候..."
        aria-live="polite"
      >
        <div class="result-header">
          <div class="panel-title">
            <el-icon>
              <Operation />
            </el-icon>
            <h2>需求拆解结果</h2>
          </div>
          <el-tag v-if="result" type="info">共 {{ totalTaskCount }} 项任务</el-tag>
        </div>

        <el-alert
          v-if="submitting"
          class="state-alert"
          title="正在拆解需求，请稍候..."
          type="info"
          :closable="false"
          show-icon
        />
        <el-alert
          v-else-if="submitError"
          class="state-alert"
          title="需求拆解失败"
          :description="submitError"
          type="error"
          :closable="false"
          show-icon
        />

        <template v-if="result">
          <p class="result-summary">{{ displayText(result.summary) }}</p>
          <p v-if="result.risk" class="result-risk">
            <el-icon>
              <Warning />
            </el-icon>
            <span>{{ result.risk }}</span>
          </p>

          <el-tabs v-model="activeTaskTab">
            <el-tab-pane
              v-for="taskType in taskTypes"
              :key="taskType.type"
              :label="taskType.label"
              :name="taskType.type"
            >
              <div v-if="tasksByType[taskType.type].length > 0" class="task-list">
                <article
                  v-for="(task, index) in tasksByType[taskType.type]"
                  :key="`${taskType.type}-${index}`"
                  class="task-card"
                >
                  <div class="task-title-row">
                    <el-tag size="small" :type="taskType.tagType">
                      {{ taskType.label }}
                    </el-tag>
                    <el-tag size="small" :type="priorityMeta(task.priority).tagType">
                      优先级：{{ priorityMeta(task.priority).label }}
                    </el-tag>
                    <h3>{{ displayText(task.title) }}</h3>
                  </div>
                  <p class="task-desc">{{ displayText(task.description) }}</p>

                  <dl class="task-meta">
                    <div>
                      <dt>预估工时</dt>
                      <dd>{{ formatEstimatedHours(task.estimatedHours) }}</dd>
                    </div>
                    <div>
                      <dt>涉及文件</dt>
                      <dd>
                        <template v-if="getFiles(task).length > 0">
                          <span
                            v-for="file in getFiles(task)"
                            :key="file"
                            class="code-block"
                          >
                            {{ file }}
                          </span>
                        </template>
                        <span v-else class="empty-value">--</span>
                      </dd>
                    </div>
                    <div>
                      <dt>接口路径</dt>
                      <dd>
                        <span v-if="task.apiEndpoint" class="code-block">
                          {{ task.apiEndpoint }}
                        </span>
                        <span v-else class="empty-value">--</span>
                      </dd>
                    </div>
                    <div>
                      <dt>验收标准</dt>
                      <dd>{{ formatAcceptanceCriteria(task.acceptanceCriteria) }}</dd>
                    </div>
                    <div>
                      <dt>任务风险</dt>
                      <dd>{{ displayText(task.risk) }}</dd>
                    </div>
                  </dl>
                </article>
              </div>

              <el-empty v-else description="暂无该类任务" />
            </el-tab-pane>
          </el-tabs>
        </template>

        <el-empty v-else-if="!submitting && !submitError" description="暂无拆解结果" />
      </section>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, EditPen, Operation, Warning } from '@element-plus/icons-vue'
import { decomposeRequirement } from '../../api/ai'

// 第一周默认走 Mock；将其设为 false 后，才会通过 ai.js 调用正式接口。
const USE_MOCK = true
const HISTORY_STORAGE_KEY = 'platform_requirement_history'
const MAX_HISTORY_COUNT = 20
const MAX_REQUIREMENT_LENGTH = 2000

const taskTypes = [
  { type: 'FRONTEND', label: '前端任务', tagType: 'success' },
  { type: 'BACKEND', label: '后端任务', tagType: 'primary' },
  { type: 'DATABASE', label: '数据库', tagType: 'warning' },
  { type: 'TEST', label: '测试任务', tagType: 'info' },
  // Swagger 规定的第五类正式任务类型为 SECURITY，不使用自定义 DEPLOYMENT 枚举。
  { type: 'SECURITY', label: '安全检查', tagType: 'danger' }
]

const priorityLabels = {
  high: { label: '高', tagType: 'danger' },
  medium: { label: '中', tagType: 'warning' },
  low: { label: '低', tagType: 'info' }
}

const requirement = ref('')
const result = ref(null)
const submitting = ref(false)
const submitError = ref('')
const activeTaskTab = ref('')
const activeHistoryId = ref(null)
const historyList = ref([])

const canSubmit = computed(() => requirement.value.trim().length > 0 && !submitting.value)

const safeTasks = computed(() => {
  return Array.isArray(result.value?.tasks) ? result.value.tasks : []
})

const totalTaskCount = computed(() => safeTasks.value.length)

const tasksByType = computed(() => {
  return taskTypes.reduce((groups, taskType) => {
    groups[taskType.type] = safeTasks.value.filter((task) => task?.type === taskType.type)
    return groups
  }, {})
})

onMounted(loadHistory)

async function handleSubmit() {
  const submittedRequirement = requirement.value.trim()

  if (!submittedRequirement || submitting.value) {
    return
  }

  submitting.value = true
  submitError.value = ''
  result.value = null
  activeTaskTab.value = ''

  try {
    const response = await requestDecompose(submittedRequirement)
    const nextResult = normalizeApiResponse(response)

    result.value = nextResult
    activeTaskTab.value = taskTypes[0].type
    appendHistory(submittedRequirement, nextResult)
    ElMessage.success('任务拆解完成')
  } catch (error) {
    submitError.value = error?.message || '需求拆解失败，请补充描述后重试'
    ElMessage.error(submitError.value)
  } finally {
    submitting.value = false
  }
}

function handleNewRequirement() {
  requirement.value = ''
  result.value = null
  submitError.value = ''
  activeTaskTab.value = ''
  activeHistoryId.value = null
}

function selectHistory(item) {
  activeHistoryId.value = item.id
  requirement.value = item.requirement || ''
  result.value = item.result || null
  submitError.value = ''
  activeTaskTab.value = result.value ? taskTypes[0].type : ''
}

async function handleDeleteHistory(item) {
  try {
    await ElMessageBox.confirm('确定删除这条历史需求吗？', '提示', { type: 'warning' })
  } catch {
    return
  }

  historyList.value = historyList.value.filter((historyItem) => historyItem.id !== item.id)
  saveHistory()

  if (activeHistoryId.value === item.id) {
    handleNewRequirement()
  }

  ElMessage.success('历史需求已删除')
}

async function handleClearAllHistory() {
  try {
    await ElMessageBox.confirm('确定清空全部历史需求吗？此操作不可恢复。', '提示', {
      type: 'warning'
    })
  } catch {
    return
  }

  historyList.value = []
  localStorage.removeItem(HISTORY_STORAGE_KEY)
  handleNewRequirement()
  ElMessage.success('历史需求已清空')
}

function requestDecompose(submittedRequirement) {
  if (USE_MOCK) {
    return mockDecomposeRequirement(submittedRequirement)
  }

  return decomposeRequirement(submittedRequirement)
}

function mockDecomposeRequirement(submittedRequirement) {
  const delay = 600 + Math.floor(Math.random() * 401)

  return new Promise((resolve, reject) => {
    window.setTimeout(() => {
      if (submittedRequirement.includes('模拟失败')) {
        reject(new Error('Mock 模式已模拟拆解失败，请修改需求后重试'))
        return
      }

      resolve({
        code: 200,
        data: {
          summary: `需求拆解：${truncateText(submittedRequirement, 30)}`,
          tasks: createMockTasks(),
          risk: '日期格式需要统一为 YYYY-MM-DD，筛选条件需覆盖边界日期。'
        },
        message: 'success'
      })
    }, delay)
  })
}

function createMockTasks() {
  // priority、estimatedHours 与 task.risk 仅用于第一周 Mock 展示；不会作为正式请求字段发送。
  return [
    {
      type: 'FRONTEND',
      title: '随访列表页增加日期范围选择器',
      description: '在搜索栏增加日期范围选择器，并保持查询条件与分页联动。',
      priority: 'high',
      estimatedHours: 4,
      filesToModify: ['frontend-target/src/views/followUp/FollowUpList.vue'],
      apiEndpoint: 'GET /api/follow-ups?startDate=xxx&endDate=xxx',
      acceptanceCriteria: '选择日期范围后查询，表格只显示范围内记录。',
      risk: '日期控件的时区处理可能导致边界日期偏差。'
    },
    {
      type: 'BACKEND',
      title: '随访查询接口支持日期范围参数',
      description: '确认随访查询接收 startDate 和 endDate 参数并传递到查询条件。',
      priority: 'high',
      estimatedHours: 3,
      filesToModify: ['backend/src/main/java/.../FollowUpController.java'],
      apiEndpoint: 'GET /api/follow-ups',
      acceptanceCriteria: '传入 startDate/endDate 后，接口仅返回范围内的随访记录。',
      risk: '需要保持未传日期参数时的既有查询行为。'
    },
    {
      type: 'DATABASE',
      title: '确认随访日期字段索引',
      description: '检查随访记录表 followUpDate 查询条件是否具备合适索引。',
      priority: 'medium',
      estimatedHours: 2,
      filesToModify: ['backend/src/main/resources/db/migration'],
      apiEndpoint: null,
      acceptanceCriteria: '日期范围查询在常规数据量下响应稳定。',
      risk: '新增索引会增加写入开销，需要评估现有数据量。'
    },
    {
      type: 'TEST',
      title: '补充日期范围筛选测试',
      description: '覆盖仅开始日期、仅结束日期和完整日期范围三类查询。',
      priority: 'medium',
      estimatedHours: 3,
      filesToModify: ['backend/src/test/java/.../FollowUpControllerTest.java'],
      apiEndpoint: 'GET /api/follow-ups',
      acceptanceCriteria: '日期范围筛选相关测试全部通过，边界日期场景有覆盖。',
      risk: '测试数据的日期分布不足会遗漏边界条件。'
    },
    {
      type: 'SECURITY',
      title: '校验查询权限与输入边界',
      description: '确认仅授权用户可查询随访记录，并限制异常日期参数。',
      priority: 'low',
      estimatedHours: 2,
      filesToModify: [],
      apiEndpoint: 'GET /api/follow-ups',
      acceptanceCriteria: '未授权请求返回 401，异常日期参数返回 400。',
      risk: '权限校验遗漏可能导致非授权人员读取随访数据。'
    }
  ]
}

function normalizeApiResponse(response) {
  if (response && typeof response === 'object' && 'code' in response) {
    if (response.code !== 200) {
      throw new Error(response.message || '需求拆解失败')
    }

    return response.data || {}
  }

  return response || {}
}

function appendHistory(submittedRequirement, nextResult) {
  const historyItem = {
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    title: nextResult?.summary || truncateText(submittedRequirement, 24),
    requirement: maskSensitiveText(submittedRequirement),
    createdAt: new Date().toISOString(),
    status: '已生成',
    result: nextResult
  }

  historyList.value = [historyItem, ...historyList.value].slice(0, MAX_HISTORY_COUNT)
  activeHistoryId.value = historyItem.id
  saveHistory()
}

function loadHistory() {
  const rawHistory = localStorage.getItem(HISTORY_STORAGE_KEY)

  if (!rawHistory) {
    return
  }

  try {
    const parsedHistory = JSON.parse(rawHistory)
    historyList.value = Array.isArray(parsedHistory)
      ? parsedHistory.map(normalizeHistoryItem).filter(Boolean).slice(0, MAX_HISTORY_COUNT)
      : []
  } catch {
    historyList.value = []
  }
}

function normalizeHistoryItem(item) {
  if (!item || typeof item !== 'object' || !item.id || !item.requirement) {
    return null
  }

  return {
    id: String(item.id),
    title: item.title || truncateText(item.requirement, 24),
    requirement: item.requirement,
    // 兼容此前保存的 createTime，新的记录统一使用 createdAt。
    createdAt: item.createdAt || item.createTime || '',
    status: item.status || '已生成',
    result: item.result || null
  }
}

function saveHistory() {
  try {
    localStorage.setItem(HISTORY_STORAGE_KEY, JSON.stringify(historyList.value))
  } catch {
    ElMessage.warning('历史记录暂时无法保存到本地浏览器')
  }
}

function priorityMeta(priority) {
  return priorityLabels[priority] || { label: '--', tagType: 'info' }
}

function formatEstimatedHours(estimatedHours) {
  return Number.isFinite(estimatedHours) ? `${estimatedHours} 小时` : '--'
}

function getFiles(task) {
  return Array.isArray(task?.filesToModify) ? task.filesToModify.filter(Boolean) : []
}

function formatAcceptanceCriteria(value) {
  if (Array.isArray(value)) {
    return value.filter(Boolean).join('；') || '--'
  }

  return displayText(value)
}

function displayText(value) {
  return value === null || value === undefined || value === '' ? '--' : value
}

function formatHistoryTime(value) {
  if (!value) {
    return '--'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  const year = date.getFullYear()
  const month = padDatePart(date.getMonth() + 1)
  const day = padDatePart(date.getDate())
  const hour = padDatePart(date.getHours())
  const minute = padDatePart(date.getMinutes())

  return `${year}-${month}-${day} ${hour}:${minute}`
}

function padDatePart(value) {
  return `${value}`.padStart(2, '0')
}

function truncateText(text, maxLength) {
  return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text
}

function maskSensitiveText(text) {
  return text
    .replace(/\b1[3-9]\d{9}\b/g, (phone) => `${phone.slice(0, 3)}****${phone.slice(7)}`)
    .replace(/\b\d{6}(?:19|20)\d{2}\d{4}\d{3}[\dXx]\b/g, (idCard) => `${idCard.slice(0, 6)}********${idCard.slice(-4)}`)
}
</script>

<style scoped>
.chat-panel {
  display: flex;
  gap: 20px;
}

.history-panel,
.input-area,
.result-area {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 20px;
}

.history-panel {
  width: 300px;
  flex-shrink: 0;
}

.main-panel {
  flex: 1;
  min-width: 0;
}

.history-header,
.result-header,
.task-title-row,
.history-meta {
  display: flex;
  align-items: center;
}

.history-header,
.result-header {
  justify-content: space-between;
  gap: 16px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.history-header .panel-title,
.result-header .panel-title {
  margin-bottom: 0;
}

.panel-title .el-icon {
  color: var(--color-primary);
}

.panel-title h1,
.panel-title h2 {
  margin: 0;
  font: inherit;
}

.page-title {
  font-size: 20px;
}

.history-list {
  display: flex;
  max-height: 660px;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  padding: 12px;
  color: var(--color-text-regular);
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-left: 3px solid transparent;
  border-radius: 4px;
  cursor: pointer;
}

.history-item:hover,
.history-item.active {
  background: var(--color-primary-light);
}

.history-item.active {
  border-left-color: var(--color-primary);
}

.history-item:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.history-item-content {
  min-width: 0;
  flex: 1;
}

.history-title {
  display: block;
  overflow: hidden;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-meta {
  gap: 8px;
  margin-top: 8px;
}

.history-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.input-actions {
  display: flex;
  gap: 12px;
}

.result-area {
  min-height: 280px;
  margin-top: 20px;
}

.state-alert {
  margin-bottom: 16px;
}

.result-summary {
  margin: 16px 0 8px;
  color: var(--color-text-regular);
  line-height: 1.6;
}

.result-risk {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 16px;
  font-size: 12px;
  color: var(--color-warning);
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-card {
  padding: 16px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 4px;
}

.task-title-row {
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.task-title-row h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary);
}

.task-desc {
  margin: 0 0 12px;
  line-height: 1.6;
}

.task-meta {
  margin: 0;
}

.task-meta div {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.task-meta dt,
.empty-value {
  color: var(--color-text-secondary);
}

.task-meta dd {
  min-width: 0;
  margin: 0;
  color: var(--color-text-regular);
  overflow-wrap: anywhere;
}

.code-block {
  display: inline-block;
  max-width: 100%;
  margin: 0 6px 6px 0;
  padding: 2px 6px;
  overflow-wrap: anywhere;
  font-family: "Consolas", "Monaco", monospace;
  font-size: 12px;
  color: var(--color-text-regular);
  background: var(--color-bg);
  border-radius: 4px;
}

@media (max-width: 960px) {
  .chat-panel {
    flex-direction: column;
  }

  .history-panel {
    width: auto;
  }

  .history-list {
    max-height: 280px;
  }
}
</style>
