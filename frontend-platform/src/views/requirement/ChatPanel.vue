<template>
  <el-card class="requirement-card" shadow="never">
    <template #header>
      <div class="requirement-header">
        <el-icon>
          <EditPen />
        </el-icon>
        <span>需求输入</span>
      </div>
    </template>
    <el-empty description="需求输入页面将在下一产出中完成" />
  </el-card>
</template>

<script setup>
import { EditPen } from '@element-plus/icons-vue'
</script>

<style scoped>
.requirement-card {
  border: 1px solid var(--color-border);
  border-radius: 4px;
}

.requirement-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.requirement-header .el-icon {
  color: var(--color-primary);
}
</style>
