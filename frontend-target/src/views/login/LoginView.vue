<template>
  <div class="login-page">
    <section class="login-visual">
      <div class="visual-content">
        <div class="visual-brand">
          <span class="brand-box"></span>
          <span>慢性病随访管理系统</span>
        </div>
        <div class="visual-copy">
          <span class="kicker">CLINICAL FOLLOW-UP</span>
          <h1>随访闭环<br />风险可控</h1>
        </div>
        <div class="module-list">
          <span>患者档案</span>
          <span>随访记录</span>
          <span>风险预警</span>
          <span>统计看板</span>
        </div>
      </div>
      <div class="visual-footer">
        <span>CHRONIC CARE</span>
        <span>v1.0</span>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-inner">
        <div class="login-heading">
          <span class="login-kicker">SECURE ACCESS</span>
          <h2>登录系统</h2>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin"
              >登录</el-button
            >
          </el-form-item>
        </el-form>
        <div class="login-meta">
          <span>默认账号</span>
          <code>admin / 123456</code>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { User, Lock } from '@element-plus/icons-vue';
import { login } from '@/api/auth';
import { useUserStore } from '@/store/user';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);
const loading = ref(false);

const form = reactive({ username: '', password: '' });
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

async function handleLogin() {
  try {
    await formRef.value.validate();
  } catch {
    return;
  }

  loading.value = true;
  try {
    const res = await login(form);
    userStore.setLogin({
      token: res.data.token,
      role: res.data.role,
      username: form.username,
      realName: res.data.realName,
    });
    router.push('/dashboard');
  } catch {
    // 错误提示已在请求拦截器统一处理
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(380px, 0.9fr);
  min-height: 100vh;
  background: var(--color-bg);
}

.login-visual {
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 48px 56px;
  overflow: hidden;
  color: var(--color-text-primary);
  background: #fafbfc;
  border-right: 1px solid var(--color-border);
}

.visual-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: center;
  max-width: 560px;
}

.visual-content::after {
  content: '';
  position: absolute;
  right: -24px;
  left: -24px;
  height: 1px;
  background: rgba(15, 164, 127, 0.42);
  animation: scan-line 5s ease-in-out infinite;
  pointer-events: none;
}

@keyframes scan-line {
  0%,
  100% {
    top: 18%;
    opacity: 0.35;
  }
  50% {
    top: 82%;
    opacity: 0.9;
  }
}

.visual-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 84px;
  font-size: 14px;
  font-weight: 600;
}

.brand-box {
  position: relative;
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  background: var(--color-primary);
  border-radius: 4px;
}

.brand-box::before,
.brand-box::after {
  content: '';
  position: absolute;
  background: rgba(255, 255, 255, 0.9);
}

.brand-box::before {
  left: 7px;
  right: 7px;
  top: 6px;
  height: 1px;
  box-shadow: 0 7px 0 rgba(255, 255, 255, 0.9);
}

.brand-box::after {
  left: 6px;
  top: 7px;
  bottom: 7px;
  width: 1px;
  box-shadow: 7px 0 0 rgba(255, 255, 255, 0.9);
}

.visual-copy .kicker {
  display: inline-block;
  margin-bottom: 18px;
  color: var(--color-primary);
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 600;
}

.visual-copy h1 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: clamp(34px, 4vw, 52px);
  font-weight: 600;
  line-height: 1.16;
}

.module-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 42px;
}

.module-list span {
  padding: 7px 12px;
  color: var(--color-text-regular);
  font-size: 12px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: #ffffff;
}

.visual-footer {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  padding-top: 18px;
  color: var(--color-text-secondary);
  font-family: var(--font-mono);
  font-size: 11px;
  border-top: 1px solid var(--color-border);
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: var(--color-bg);
}

.login-inner {
  width: 100%;
  max-width: 380px;
  padding: 36px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(16, 24, 40, 0.06);
}

.login-heading {
  margin-bottom: 28px;
}

.login-kicker {
  display: block;
  margin-bottom: 10px;
  color: var(--color-primary);
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 600;
}

.login-heading h2 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 24px;
  font-weight: 600;
}

.login-btn {
  width: 100%;
  height: 40px;
}

.login-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
  color: var(--color-text-secondary);
  font-size: 12px;
}

.login-meta code {
  padding: 4px 8px;
  color: var(--color-text-regular);
  font-family: var(--font-mono);
  font-size: 11px;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: 4px;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-visual {
    display: none;
  }

  .login-panel {
    min-height: 100vh;
    padding: 20px;
  }

  .login-inner {
    max-width: 420px;
    padding: 28px 24px;
  }
}
</style>
