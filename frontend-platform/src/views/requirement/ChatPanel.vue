/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

<template>
  <div class="chat-panel">
    <aside class="history-panel">
      <div class="history-header">
        <div class="panel-title">
          <el-icon><Clock /></el-icon><span>历史需求</span>
        </div>
        <el-button
          v-if="historyList.length"
          link
          type="danger"
          :disabled="submitting"
          @click="handleClearAllHistory"
          >清空全部</el-button
        >
      </div>

      <div v-if="historyList.length" class="history-list">
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
            >删除</el-button
          >
        </article>
      </div>
      <el-empty v-else description="暂无历史需求" />
    </aside>

    <section class="main-panel">
      <section class="input-area" aria-labelledby="requirement-input-title">
        <div class="panel-title page-title">
          <el-icon><EditPen /></el-icon>
          <h1 id="requirement-input-title">需求输入</h1>
        </div>
        <el-form label-position="top">
          <el-form-item label="主需求" required>
            <el-input
              v-model="requirement"
              type="textarea"
              :rows="6"
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
            :disabled="submitting"
            @click="handleSubmit"
            >开始拆解</el-button
          >
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
            <el-icon><Operation /></el-icon>
            <h2>需求拆解结果</h2>
          </div>
          <div class="result-status">
            <el-tag type="warning">{{ useMock ? 'Mock 模式' : '真实接口' }}</el-tag>
            <el-tag v-if="result" type="info">共 {{ safeTasks.length }} 项任务</el-tag>
          </div>
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
            <el-icon><Warning /></el-icon><span>{{ result.risk }}</span>
          </p>
          <el-tabs v-model="activeTaskTab">
            <el-tab-pane
              v-for="taskType in TASK_TYPES"
              :key="taskType.type"
              :label="taskType.label"
              :name="taskType.type"
            >
              <template v-if="tasksByType[taskType.type].length">
                <TaskCard
                  v-for="(task, index) in tasksByType[taskType.type]"
                  :key="`${taskType.type}-${index}`"
                  :task="task"
                  :task-type="taskType"
                />
              </template>
              <el-empty v-else description="暂无该类任务" />
            </el-tab-pane>
          </el-tabs>
        </template>
        <el-empty
          v-else-if="!submitting && !submitError"
          description="提交需求后将在此展示拆解结果"
        />
      </section>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Clock, EditPen, Operation, Warning } from '@element-plus/icons-vue';
import { decomposeRequirement } from '../../api/ai';
import TaskCard from '../../components/requirement/TaskCard.vue';
import { groupTasksByType, TASK_TYPES } from '../../constants/taskTypes';
import { mockDecomposeRequirement } from '../../mocks/requirement';
import { displayText, formatHistoryTime } from '../../utils/requirementFormat';
import {
  createHistoryItem,
  HISTORY_STORAGE_KEY,
  MAX_HISTORY_COUNT,
  parseHistory,
} from '../../utils/requirementHistory';

const MAX_REQUIREMENT_LENGTH = 2000;
// 与 3 号完成 POST /api/ai/decompose 联调后，在 .env.local 中设置 VITE_USE_AI_MOCK=false。
const useMock = import.meta.env.VITE_USE_AI_MOCK !== 'false';

const requirement = ref('');
const result = ref(null);
const submitting = ref(false);
const submitError = ref('');
const activeTaskTab = ref('');
const activeHistoryId = ref(null);
const historyList = ref([]);

const safeTasks = computed(() => (Array.isArray(result.value?.tasks) ? result.value.tasks : []));
const tasksByType = computed(() => groupTasksByType(safeTasks.value));

onMounted(() => {
  historyList.value = parseHistory(sessionStorage.getItem(HISTORY_STORAGE_KEY));
});

async function handleSubmit() {
  const submittedRequirement = requirement.value.trim();
  if (!submittedRequirement) {
    ElMessage.warning('请输入维护需求');
    return;
  }
  if (submitting.value) return;

  submitting.value = true;
  submitError.value = '';
  result.value = null;
  activeTaskTab.value = '';

  try {
    const nextResult = await requestDecompose(submittedRequirement);
    result.value = normalizeResult(nextResult);
    activeTaskTab.value = result.value.tasks[0]?.type || TASK_TYPES[0].type;
    appendHistory(submittedRequirement, result.value);
    ElMessage.success('任务拆解完成');
  } catch (error) {
    submitError.value = error?.message || '需求拆解失败，请补充描述后重试';
    ElMessage.error(submitError.value);
  } finally {
    submitting.value = false;
  }
}

function requestDecompose(submittedRequirement) {
  return useMock
    ? mockDecomposeRequirement(submittedRequirement)
    : decomposeRequirement(submittedRequirement);
}

function normalizeResult(value) {
  return {
    summary: value?.summary || '',
    tasks: Array.isArray(value?.tasks) ? value.tasks : [],
    risk: value?.risk ?? null,
  };
}

function appendHistory(submittedRequirement, nextResult) {
  const historyItem = createHistoryItem(submittedRequirement, nextResult);
  historyList.value = [historyItem, ...historyList.value].slice(0, MAX_HISTORY_COUNT);
  activeHistoryId.value = historyItem.id;
  saveHistory();
}

function selectHistory(item) {
  activeHistoryId.value = item.id;
  requirement.value = item.requirement;
  result.value = item.result;
  submitError.value = '';
  activeTaskTab.value = item.result?.tasks?.[0]?.type || '';
}

function handleNewRequirement() {
  requirement.value = '';
  result.value = null;
  submitError.value = '';
  activeTaskTab.value = '';
  activeHistoryId.value = null;
}

async function handleDeleteHistory(item) {
  try {
    await ElMessageBox.confirm('确定删除这条历史需求吗？', '提示', { type: 'warning' });
  } catch {
    return;
  }
  historyList.value = historyList.value.filter((historyItem) => historyItem.id !== item.id);
  if (activeHistoryId.value === item.id) handleNewRequirement();
  saveHistory();
  ElMessage.success('历史需求已删除');
}

async function handleClearAllHistory() {
  try {
    await ElMessageBox.confirm('确定清空全部历史需求吗？此操作不可恢复。', '提示', {
      type: 'warning',
    });
  } catch {
    return;
  }
  historyList.value = [];
  sessionStorage.removeItem(HISTORY_STORAGE_KEY);
  handleNewRequirement();
  ElMessage.success('历史需求已清空');
}

function saveHistory() {
  try {
    sessionStorage.setItem(HISTORY_STORAGE_KEY, JSON.stringify(historyList.value));
  } catch {
    ElMessage.warning('历史记录暂时无法保存到本地浏览器');
  }
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
  padding: 20px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 4px;
}
.history-panel {
  width: 300px;
  flex-shrink: 0;
}
.main-panel {
  min-width: 0;
  flex: 1;
}
.history-header,
.result-header,
.history-meta,
.result-status {
  display: flex;
  align-items: center;
}
.history-header,
.result-header {
  justify-content: space-between;
  gap: 16px;
}
.result-status {
  gap: 8px;
}
.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: var(--color-text-primary);
  font-size: 18px;
  font-weight: 600;
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
  color: var(--color-text-primary);
  font-weight: 500;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.history-meta {
  gap: 8px;
  margin-top: 8px;
}
.history-time {
  color: var(--color-text-secondary);
  font-size: 12px;
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
  line-height: 1.6;
}
.result-risk {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 16px;
  color: var(--color-warning);
  font-size: 12px;
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
