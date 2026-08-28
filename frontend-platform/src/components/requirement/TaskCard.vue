<template>
  <el-card shadow="never" class="task-card">
    <div class="task-title-row">
      <el-tag size="small" :type="taskType.tagType">{{ taskType.label }}</el-tag>
      <h3>{{ displayText(task.title) }}</h3>
    </div>
    <p class="task-desc">{{ displayText(task.description) }}</p>
    <el-descriptions :column="1" border size="small">
      <el-descriptions-item label="涉及文件">
        <template v-if="files.length > 0">
          <code v-for="file in files" :key="file" class="code-block">{{ file }}</code>
        </template>
        <span v-else class="empty-value">--</span>
      </el-descriptions-item>
      <el-descriptions-item label="接口路径">
        <code v-if="task.apiEndpoint" class="code-block">{{ task.apiEndpoint }}</code>
        <span v-else class="empty-value">--</span>
      </el-descriptions-item>
      <el-descriptions-item label="验收标准">
        {{ formatAcceptanceCriteria(task.acceptanceCriteria) }}
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { displayText, formatAcceptanceCriteria, getFiles } from '../../utils/requirementFormat';

const props = defineProps({
  task: { type: Object, required: true },
  taskType: { type: Object, required: true },
});
const files = computed(() => getFiles(props.task));
</script>

<style scoped>
.task-card + .task-card {
  margin-top: 12px;
}
.task-title-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}
.task-title-row h3,
.task-desc {
  margin: 0;
}
.task-title-row h3 {
  color: var(--color-text-primary);
  font-size: 16px;
  font-weight: 500;
}
.task-desc {
  margin-bottom: 12px;
  line-height: 1.6;
}
.code-block {
  display: inline-block;
  max-width: 100%;
  margin: 0 6px 4px 0;
  padding: 2px 6px;
  overflow-wrap: anywhere;
  color: var(--color-text-regular);
  background: var(--color-bg);
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 12px;
}
.empty-value {
  color: var(--color-text-secondary);
}
</style>
