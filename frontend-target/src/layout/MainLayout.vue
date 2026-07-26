<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">鎱㈢梾闅忚绯荤粺</div>
      <el-menu :default-active="activeMenu" router background-color="#304156"
               text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>宸ヤ綔鍙?/span>
        </el-menu-item>
        <el-menu-item index="/patients">
          <el-icon><UserFilled /></el-icon><span>鎮ｈ€呯鐞?/span>
        </el-menu-item>
        <el-menu-item index="/follow-ups">
          <el-icon><Document /></el-icon><span>闅忚璁板綍</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <el-icon><Bell /></el-icon><span>棰勮涓績</span>
        </el-menu-item>
        <el-sub-menu index="system" v-if="userStore.isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon><span>绯荤粺绠＄悊</span>
          </template>
          <el-menu-item index="/system/users">鐢ㄦ埛绠＄悊</el-menu-item>
          <el-menu-item index="/system/logs">鎿嶄綔鏃ュ織</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="header-title">{{ route.meta.title }}</span>
        <div class="header-right">
          <span class="username">{{ userStore.realName || userStore.username }}</span>
          <el-button text @click="handleLogout">閫€鍑?/el-button>
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