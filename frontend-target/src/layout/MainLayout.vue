<template>
  <el-container class="layout">
    <el-aside width="232px" class="aside">
      <div class="brand">
        <div class="brand-mark"><span></span></div>
        <div class="brand-copy">
          <strong>慢病随访系统</strong>
          <small>CHRONIC CARE</small>
        </div>
      </div>

      <el-menu :default-active="activeMenu" router class="side-menu">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/patients">
          <el-icon><UserFilled /></el-icon><span>患者管理</span>
        </el-menu-item>
        <el-menu-item index="/follow-ups">
          <el-icon><Document /></el-icon><span>随访记录</span>
        </el-menu-item>
        <el-menu-item index="/plans">
          <el-icon><Calendar /></el-icon><span>随访计划</span>
        </el-menu-item>
        <el-menu-item index="/follow-up-tasks">
          <el-icon><List /></el-icon><span>随访任务</span>
        </el-menu-item>
        <el-menu-item index="/follow-up-templates" v-if="userStore.isAdmin">
          <el-icon><Tickets /></el-icon><span>随访模板</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <el-icon><Bell /></el-icon><span>预警中心</span>
        </el-menu-item>
        <el-menu-item index="/messages">
          <el-icon><Message /></el-icon><span>消息中心</span>
        </el-menu-item>
        <el-menu-item index="/clinical-suggestions">
          <el-icon><MagicStick /></el-icon><span>AI随访建议</span>
        </el-menu-item>
        <el-sub-menu index="system" v-if="userStore.isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon><span>系统管理</span>
          </template>
          <el-menu-item index="/system/users">用户管理</el-menu-item>
          <el-menu-item index="/system/logs">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>

      <div class="aside-footer">
        <span class="pulse-dot"></span>
        <span>数据服务在线</span>
      </div>
    </el-aside>

    <el-container class="main-shell">
      <el-header class="header">
        <div class="header-context">
          <span class="context-label">基层医疗</span>
          <span class="context-sep">/</span>
          <span class="page-title">{{ route.meta.title }}</span>
        </div>
        <div class="header-actions">
          <div class="online-state">
            <span class="pulse-dot"></span>
            <span>服务在线</span>
          </div>
          <div class="user-chip">
            <span class="avatar">{{ avatarText }}</span>
            <span class="user-name">{{ userStore.realName || userStore.username }}</span>
          </div>
          <el-button text class="logout-btn" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>退出
          </el-button>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import {
  DataAnalysis,
  UserFilled,
  Document,
  Calendar,
  List,
  Tickets,
  Bell,
  Message,
  MagicStick,
  Setting,
  SwitchButton,
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const activeMenu = computed(() => route.path);
const avatarText = computed(() => {
  const source = userStore.realName || userStore.username || '管';
  return source.charAt(0);
});

function handleLogout() {
  userStore.logout();
  router.push('/login');
}
</script>

<style scoped>
.layout {
  height: 100vh;
  background: var(--color-bg);
}

.aside {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-right: 1px solid var(--color-border);
  overflow-y: auto;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 68px;
  padding: 0 18px;
  border-bottom: 1px solid var(--color-border);
}

.brand-mark {
  position: relative;
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  background: var(--color-primary);
  border-radius: 4px;
}

.brand-mark::before,
.brand-mark::after {
  content: '';
  position: absolute;
  background: rgba(255, 255, 255, 0.88);
}

.brand-mark::before {
  left: 7px;
  right: 7px;
  top: 6px;
  height: 1px;
  box-shadow: 0 7px 0 rgba(255, 255, 255, 0.88);
}

.brand-mark::after {
  left: 6px;
  top: 7px;
  bottom: 7px;
  width: 1px;
  box-shadow: 7px 0 0 rgba(255, 255, 255, 0.88);
}

.brand-mark span {
  position: absolute;
  right: 4px;
  bottom: 4px;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.22);
}

.brand-copy {
  min-width: 0;
  line-height: 1.2;
}

.brand-copy strong {
  display: block;
  color: var(--color-text-primary);
  font-size: 15px;
  font-weight: 600;
}

.brand-copy small {
  display: block;
  margin-top: 4px;
  color: var(--color-text-secondary);
  font-family: var(--font-mono);
  font-size: 10px;
  font-weight: 500;
}

.side-menu {
  flex: 1;
  padding: 12px 8px;
  border-right: none;
  background: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #4b5563;
  --el-menu-active-color: #0fa47f;
  --el-menu-hover-bg-color: #f3fbf8;
  --el-menu-item-height: 44px;
  --el-menu-sub-item-height: 40px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  margin-bottom: 2px;
  border-radius: 4px;
  font-weight: 500;
  transition:
    background-color 0.16s ease,
    color 0.16s ease;
}

:deep(.el-menu-item.is-active) {
  color: #0fa47f;
  background: #edf9f5;
  box-shadow: inset 3px 0 0 var(--color-primary);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  color: #0b8264;
  background: #f3fbf8;
}

:deep(.el-sub-menu .el-menu) {
  background: #fbfcfc;
}

.aside-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 48px;
  padding: 0 18px;
  color: var(--color-text-secondary);
  font-size: 12px;
  border-top: 1px solid var(--color-border);
}

.pulse-dot {
  width: 7px;
  height: 7px;
  flex-shrink: 0;
  border-radius: 50%;
  background: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(15, 164, 127, 0.12);
  animation: pulse-dot 2.4s ease-in-out infinite;
}

@keyframes pulse-dot {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.55;
  }
}

.main-shell {
  min-width: 0;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--layout-header-height);
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid var(--color-border);
}

.header-context {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.context-label {
  color: var(--color-text-secondary);
  font-size: 12px;
}

.context-sep {
  color: var(--color-border-strong);
}

.page-title {
  overflow: hidden;
  color: var(--color-text-primary);
  font-size: 16px;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.online-state {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 500;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 600;
  background: var(--color-primary-light);
  border: 1px solid rgba(15, 164, 127, 0.22);
  border-radius: 4px;
}

.user-name {
  color: var(--color-text-regular);
  font-weight: 500;
}

.logout-btn {
  color: var(--color-text-secondary);
}

.logout-btn:hover {
  color: var(--color-danger);
}

@media (max-width: 768px) {
  :deep(.el-aside) {
    width: 72px !important;
  }

  .brand {
    justify-content: center;
    padding: 0 12px;
  }

  .brand-copy,
  .aside-footer {
    display: none;
  }

  .side-menu {
    padding: 12px 6px;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    justify-content: center;
    padding-left: 0 !important;
    padding-right: 0 !important;
  }

  :deep(.el-menu-item span),
  :deep(.el-sub-menu__title span) {
    display: none;
  }

  .header {
    padding: 0 14px;
  }

  .context-label,
  .context-sep,
  .online-state,
  .user-name {
    display: none;
  }

  .header-actions {
    gap: 10px;
  }
}
</style>
