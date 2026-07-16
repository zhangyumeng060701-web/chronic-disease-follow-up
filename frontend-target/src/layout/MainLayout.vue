<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">慢病随访系统</div>
      <el-menu :default-active="activeMenu" router background-color="#304156"
               text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/patients">
          <el-icon><UserFilled /></el-icon><span>患者管理</span>
        </el-menu-item>
        <el-menu-item index="/follow-ups">
          <el-icon><Document /></el-icon><span>随访记录</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <el-icon><Bell /></el-icon><span>预警中心</span>
        </el-menu-item>
        <el-sub-menu index="system" v-if="userStore.isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon><span>系统管理</span>
          </template>
          <el-menu-item index="/system/users">用户管理</el-menu-item>
          <el-menu-item index="/system/logs">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="header-title">{{ route.meta.title }}</span>
        <div class="header-right">
          <span class="username">{{ userStore.username }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { DataAnalysis, UserFilled, Document, Bell, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background: #304156; overflow-y: auto; }
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}
.header-title { font-size: 16px; font-weight: 500; }
.header-right { display: flex; align-items: center; gap: 12px; }
.username { color: #606266; }
</style>