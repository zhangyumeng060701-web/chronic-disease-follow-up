<template>
  <el-container class="platform-layout">
    <el-header class="platform-header">
      <div class="platform-brand">
        <el-icon>
          <Operation />
        </el-icon>
        <span>慢病随访智能化维护平台</span>
      </div>
      <div class="platform-user">
        <span class="platform-user-name">
          <el-icon>
            <User />
          </el-icon>
          开发维护人员
        </span>
        <el-button link type="primary" @click="handleLogout">
          <el-icon>
            <SwitchButton />
          </el-icon>
          退出
        </el-button>
      </div>
    </el-header>

    <el-container class="platform-body">
      <el-aside class="platform-sidebar" width="220px">
        <el-menu
          router
          class="platform-menu"
          :default-active="activeMenu"
          active-text-color="#409EFF"
          background-color="#FFFFFF"
          text-color="#303133"
        >
          <el-menu-item index="/requirement">
            <el-icon>
              <EditPen />
            </el-icon>
            <span>需求输入</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="platform-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EditPen, Operation, SwitchButton, User } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)

function handleLogout() {
  ElMessage.info('退出功能暂未接入')
}
</script>

<style scoped>
.platform-layout {
  min-height: 100vh;
  background: var(--color-bg);
}

.platform-header {
  height: var(--layout-header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
}

.platform-brand,
.platform-user,
.platform-user-name {
  display: flex;
  align-items: center;
}

.platform-brand {
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.platform-brand .el-icon {
  color: var(--color-primary);
}

.platform-user {
  gap: 16px;
}

.platform-user-name {
  gap: 6px;
  color: var(--color-text-regular);
}

.platform-body {
  min-height: calc(100vh - var(--layout-header-height));
}

.platform-sidebar {
  background: var(--color-bg-card);
  border-right: 1px solid var(--color-border);
}

.platform-menu {
  border-right: none;
}

.platform-main {
  min-height: calc(100vh - var(--layout-header-height));
  padding: var(--layout-main-padding);
  background: var(--color-bg);
}

:deep(.el-menu-item.is-active) {
  background: var(--color-primary-light);
}
</style>
