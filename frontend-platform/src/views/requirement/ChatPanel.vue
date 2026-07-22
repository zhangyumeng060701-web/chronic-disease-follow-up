<template>
  <div class="chat-panel">
    <aside class="history-panel">
      <div class="panel-title">
        <el-icon>
          <Clock />
        </el-icon>
        <span>历史需求</span>
      </div>

      <div v-if="historyList.length > 0" class="history-list">
        <button
          v-for="item in historyList"
          :key="item.id"
          class="history-item"
          :class="{ active: item.id === activeHistoryId }"
          type="button"
          @click="selectHistory(item)"
        >
          <span class="history-title">{{ displayText(item.title) }}</span>
          <span class="history-time">{{ displayText(item.createTime) }}</span>
          <el-tag size="small" type="success">{{ displayText(item.status) }}</el-tag>
        </button>
      </div>

      <el-empty v-else description="暂无历史需求" />
    </aside>

    <section class="main-panel">
      <div class="input-area">
        <div class="panel-title page-title">
          <el-icon>
            <EditPen />
          </el-icon>
          <span>需求输入</span>
        </div>

        <el-form label-position="top">
          <el-form-item label="主需求" required>
            <el-input
              v-model="requirement"
              type="textarea"
              :rows="5"
              :disabled="submitting"
              placeholder="请输入您的需求，例如：在随访页面增加血压趋势图、新增患者用药记录表、修改高危预警阈值为收缩压≥150"
            />
          </el-form-item>

          <el-form-item label="补充要求">
            <el-input
              v-model="supplementRequirement"
              type="textarea"
              :rows="3"
              :disabled="submitting"
              placeholder="可选：填写权限范围、脱敏要求、兼容性要求、指定页面或指定接口"
            />
          </el-form-item>
        </el-form>

        <div class="input-actions">
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交需求
          </el-button>
          <el-button :disabled="submitting" @click="handleClear">清空</el-button>
        </div>
      </div>

      <div class="result-area">
        <template v-if="result">
          <div class="result-header">
            <div>
              <div class="panel-title">
                <el-icon>
                  <Operation />
                </el-icon>
                <span>需求拆解结果</span>
              </div>
              <p class="result-summary">{{ displayText(result.summary) }}</p>
              <p v-if="result.risk" class="result-risk">
                <el-icon>
                  <Warning />
                </el-icon>
                <span>{{ result.risk }}</span>
              </p>
            </div>
            <el-tag type="info">共 {{ totalTaskCount }} 项任务</el-tag>
          </div>

          <el-tabs v-model="activeTaskTab">
            <el-tab-pane
              v-for="taskType in taskTypes"
              :key="taskType.type"
              :label="taskType.label"
              :name="taskType.type"
            >
              <div v-if="tasksByType[taskType.type].length > 0" class="task-list">
                <div
                  v-for="(task, index) in tasksByType[taskType.type]"
                  :key="`${taskType.type}-${index}`"
                  class="task-card"
                >
                  <div class="task-title-row">
                    <el-tag size="small" :type="taskType.tagType">
                      {{ taskType.label }}
                    </el-tag>
                    <h3>{{ displayText(task.title) }}</h3>
                  </div>
                  <p class="task-desc">{{ displayText(task.description) }}</p>

                  <dl class="task-meta">
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
                      <dd>{{ displayText(task.acceptanceCriteria) }}</dd>
                    </div>
                  </dl>
                </div>
              </div>

              <el-empty v-else description="暂无该类任务" />
            </el-tab-pane>
          </el-tabs>
        </template>

        <el-empty v-else description="暂无拆解结果" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, EditPen, Operation, Warning } from '@element-plus/icons-vue'
import { decomposeRequirement } from '../../api/ai'

// 第一周使用 Mock；切为 false 后会调用 src/api/ai.js 的正式拆解接口。
const USE_MOCK = true
const HISTORY_STORAGE_KEY = 'platform_requirement_history'
const MAX_HISTORY_COUNT = 20

const taskTypes = [
  { type: 'FRONTEND', label: '前端任务', tagType: 'success' },
  { type: 'BACKEND', label: '后端任务', tagType: 'primary' },
  { type: 'DATABASE', label: '数据库', tagType: 'warning' },
  { type: 'TEST', label: '测试任务', tagType: 'info' },
  { type: 'SECURITY', label: '安全检查', tagType: 'danger' }
]

const requirement = ref('')
const supplementRequirement = ref('')
const result = ref(null)
const submitting = ref(false)
const activeTaskTab = ref('')
const activeHistoryId = ref(null)
const historyList = ref([])

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

onMounted(() => {
  loadHistory()
})

async function handleSubmit() {
  const mergedRequirement = buildRequirement()

  if (!mergedRequirement) {
    ElMessage.warning('需求不能为空')
    return
  }

  submitting.value = true

  try {
    const response = await requestDecompose(mergedRequirement)
    const nextResult = normalizeApiResponse(response)

    result.value = nextResult
    activeTaskTab.value = taskTypes[0].type
    appendHistory(mergedRequirement, nextResult)
    ElMessage.success('任务拆解完成')
  } catch (error) {
    ElMessage.error(error?.message || '需求拆解失败，请补充描述后重试')
  } finally {
    submitting.value = false
  }
}

function handleClear() {
  requirement.value = ''
  supplementRequirement.value = ''
  result.value = null
  activeTaskTab.value = ''
  activeHistoryId.value = null
}

function selectHistory(item) {
  activeHistoryId.value = item.id
  requirement.value = item.requirement || ''
  supplementRequirement.value = ''
  result.value = item.result || null
  activeTaskTab.value = result.value ? taskTypes[0].type : ''
}

function buildRequirement() {
  const mainRequirement = requirement.value.trim()
  const supplement = supplementRequirement.value.trim()

  if (!mainRequirement) {
    return ''
  }

  if (!supplement) {
    return mainRequirement
  }

  // 正式 AI 接口只允许发送 { requirement }，补充要求必须先合并进同一个字符串。
  return `${mainRequirement}\n\n补充要求：${supplement}`
}

function requestDecompose(mergedRequirement) {
  if (USE_MOCK) {
    return mockDecomposeRequirement()
  }

  return decomposeRequirement(mergedRequirement)
}

function mockDecomposeRequirement() {
  const delay = 600 + Math.floor(Math.random() * 401)

  return new Promise((resolve) => {
    window.setTimeout(() => {
      resolve({
        code: 200,
        data: {
          summary: '随访记录页面增加日期范围筛选',
          tasks: [
            {
              type: 'FRONTEND',
              title: '随访列表页增加日期范围选择器',
              description: '在搜索栏增加日期范围选择器，并保持查询条件与分页联动。',
              filesToModify: ['frontend-target/src/views/followUp/FollowUpList.vue'],
              apiEndpoint: 'GET /api/follow-ups?startDate=xxx&endDate=xxx',
              acceptanceCriteria: '选择日期范围后查询，表格只显示范围内记录'
            },
            {
              type: 'BACKEND',
              title: '随访查询接口支持日期范围参数',
              description: '确认 FollowUpController 接收 startDate 和 endDate 参数并传递到查询条件。',
              filesToModify: ['backend/src/main/java/.../FollowUpController.java'],
              apiEndpoint: 'GET /api/follow-ups',
              acceptanceCriteria: '传入 startDate/endDate 后，接口仅返回范围内的随访记录'
            },
            {
              type: 'DATABASE',
              title: '确认随访日期字段索引',
              description: '检查随访记录表 followUpDate 查询条件是否具备合适索引。',
              filesToModify: ['backend/src/main/resources/db/migration'],
              apiEndpoint: '',
              acceptanceCriteria: '日期范围查询在常规数据量下响应稳定'
            },
            {
              type: 'TEST',
              title: '补充日期范围筛选测试',
              description: '覆盖仅开始日期、仅结束日期和完整日期范围三类查询。',
              filesToModify: ['backend/src/test/java/.../FollowUpControllerTest.java'],
              apiEndpoint: 'GET /api/follow-ups',
              acceptanceCriteria: '日期范围筛选相关测试全部通过'
            },
            {
              type: 'SECURITY',
              title: '校验查询权限与输入边界',
              description: '确认仅授权用户可查询随访记录，并限制异常日期参数。',
              filesToModify: [],
              apiEndpoint: 'GET /api/follow-ups',
              acceptanceCriteria: '未授权请求返回 401，异常日期参数返回 400'
            }
          ],
          risk: '日期格式需要统一为 YYYY-MM-DD'
        },
        message: 'success'
      })
    }, delay)
  })
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

function appendHistory(mergedRequirement, nextResult) {
  const historyItem = {
    id: `${Date.now()}`,
    title: nextResult?.summary || truncateText(mergedRequirement),
    createTime: formatLocalDateTime(new Date()),
    status: '已生成',
    requirement: maskSensitiveText(mergedRequirement),
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
      ? parsedHistory.filter(isValidHistoryItem).slice(0, MAX_HISTORY_COUNT)
      : []
  } catch {
    localStorage.removeItem(HISTORY_STORAGE_KEY)
    historyList.value = []
  }
}

function saveHistory() {
  localStorage.setItem(HISTORY_STORAGE_KEY, JSON.stringify(historyList.value))
}

function isValidHistoryItem(item) {
  return item && typeof item === 'object' && item.id && item.title && item.createTime
}

function formatLocalDateTime(date) {
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

function truncateText(text) {
  return text.length > 24 ? `${text.slice(0, 24)}...` : text
}

function maskSensitiveText(text) {
  return text
    .replace(/\b1[3-9]\d{9}\b/g, (phone) => `${phone.slice(0, 3)}****${phone.slice(7)}`)
    .replace(/\b\d{6}(?:19|20)\d{2}\d{4}\d{3}[\dXx]\b/g, (idCard) => `${idCard.slice(0, 6)}********${idCard.slice(-4)}`)
}

function getFiles(task) {
  return Array.isArray(task?.filesToModify) ? task.filesToModify.filter(Boolean) : []
}

function displayText(value) {
  return value === null || value === undefined || value === '' ? '--' : value
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

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.panel-title .el-icon {
  color: var(--color-primary);
}

.page-title {
  font-size: 20px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  text-align: left;
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

.history-title {
  width: 100%;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  word-break: break-word;
}

.history-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.input-actions {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

.result-area {
  margin-top: 20px;
}

.result-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.result-summary {
  margin: 0 0 8px;
  color: var(--color-text-regular);
}

.result-risk {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0;
  font-size: 12px;
  color: var(--color-warning);
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-card {
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 16px;
  background: var(--color-bg-card);
}

.task-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
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

.task-meta dt {
  color: var(--color-text-secondary);
}

.task-meta dd {
  min-width: 0;
  margin: 0;
  color: var(--color-text-regular);
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

.empty-value {
  color: var(--color-text-secondary);
}
</style>
