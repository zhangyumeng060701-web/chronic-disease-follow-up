<template>
  <div class="patient-app">
    <section v-if="!token" class="login-page">
      <div class="brand">
        <span class="brand-mark"></span>
        <div>
          <h1>慢病随访患者端</h1>
          <p>CHRONIC CARE</p>
        </div>
      </div>
      <el-form class="login-form" :model="loginForm" label-position="top">
        <el-form-item label="手机号">
          <el-input v-model="loginForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="loginForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loginLoading" @click="handleLogin"
          >登录</el-button
        >
      </el-form>
      <p class="demo-hint">演示账号：13800138000 / 110101199001011234</p>
    </section>

    <template v-else>
      <header class="app-header">
        <div>
          <strong>{{ patientName }}</strong>
          <span>患者端</span>
        </div>
        <el-button text type="primary" @click="handleLogout">退出</el-button>
      </header>

      <main class="app-main">
        <section v-if="activeTab === 'home'">
          <div class="risk-banner">
            <div>
              <span>我的风险等级</span>
              <strong>{{ riskLabel(riskLevel) }}</strong>
            </div>
            <p>{{ riskEvidence }}</p>
          </div>
          <div class="summary-grid">
            <div class="summary-item">
              <span>随访计划</span>
              <strong>{{ plans.length }}</strong>
            </div>
            <div class="summary-item">
              <span>未读消息</span>
              <strong>{{ unreadCount }}</strong>
            </div>
            <div class="summary-item">
              <span>自测记录</span>
              <strong>{{ vitals.length }}</strong>
            </div>
          </div>
          <el-card class="section-card">
            <template #header>最近随访</template>
            <div v-if="followUps.length" class="timeline">
              <div v-for="item in followUps.slice(0, 3)" :key="item.id" class="timeline-item">
                <span>{{ item.followUpDate }}</span>
                <span>{{ item.followUpType }}</span>
              </div>
            </div>
            <el-empty v-else description="暂无随访记录" :image-size="60" />
          </el-card>
        </section>

        <section v-else-if="activeTab === 'plans'">
          <el-card v-for="plan in plans" :key="plan.id" class="section-card plan-card">
            <div class="plan-head">
              <span>{{ riskLabel(plan.riskLevel) }}</span>
              <span>{{ plan.followUpType }}</span>
            </div>
            <h3>下次随访：{{ plan.nextFollowUpDate }}</h3>
            <p>频率：每 {{ plan.followUpFrequencyDays }} 天</p>
          </el-card>
          <el-empty v-if="!plans.length" description="暂无随访计划" />
        </section>

        <section v-else-if="activeTab === 'vitals'">
          <el-card class="section-card">
            <template #header>上报血压血糖</template>
            <el-form label-position="top" :model="vitalForm">
              <el-form-item label="指标类型">
                <el-select v-model="vitalForm.metricType">
                  <el-option label="收缩压" value="SYSTOLIC_BP" />
                  <el-option label="舒张压" value="DIASTOLIC_BP" />
                  <el-option label="空腹血糖" value="FASTING_GLUCOSE" />
                  <el-option label="餐后血糖" value="POSTPRANDIAL_GLUCOSE" />
                </el-select>
              </el-form-item>
              <el-form-item label="测量值">
                <el-input-number
                  v-model="vitalForm.metricValue"
                  :min="0"
                  :max="500"
                  :precision="1"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="测量时间">
                <el-date-picker
                  v-model="vitalForm.measuredAt"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
              <el-button type="primary" :loading="vitalLoading" @click="submitVital"
                >提交</el-button
              >
            </el-form>
          </el-card>

          <el-card class="section-card">
            <template #header>自测记录</template>
            <el-table :data="vitals" size="small">
              <el-table-column prop="metricType" label="类型" />
              <el-table-column prop="metricValue" label="数值" />
              <el-table-column prop="measuredAt" label="时间" />
            </el-table>
          </el-card>
        </section>

        <section v-else-if="activeTab === 'questionnaire'">
          <el-card v-for="q in questionnaires" :key="q.id" class="section-card">
            <template #header>{{ q.title }}</template>
            <p v-if="q.description" class="muted">{{ q.description }}</p>
            <el-form label-position="top">
              <el-form-item
                v-for="question in parseQuestions(q.content)"
                :key="question.key"
                :label="question.label"
              >
                <el-radio-group v-model="answers[q.id + '.' + question.key]">
                  <el-radio v-for="option in question.options" :key="option" :label="option">{{
                    option
                  }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>
            <el-button type="primary" size="small" @click="submitQuestionnaire(q)"
              >提交问卷</el-button
            >
          </el-card>
          <el-empty v-if="!questionnaires.length" description="暂无问卷" />
        </section>

        <section v-else-if="activeTab === 'messages'">
          <el-card
            v-for="message in messages"
            :key="message.id"
            class="section-card message-card"
            :class="{ unread: message.status === 'PENDING' }"
            @click="markRead(message)"
          >
            <div class="message-head">
              <strong>{{ message.title }}</strong>
              <span>{{ message.createTime }}</span>
            </div>
            <p>{{ message.content }}</p>
          </el-card>
          <el-empty v-if="!messages.length" description="暂无消息" />
        </section>

        <section v-else-if="activeTab === 'followup'">
          <el-card class="section-card">
            <template #header>患者端自报随访</template>
            <el-form label-position="top" :model="followUpForm">
              <el-form-item label="随访日期">
                <el-date-picker
                  v-model="followUpForm.followUpDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="症状描述">
                <el-input v-model="followUpForm.symptoms" type="textarea" :rows="2" />
              </el-form-item>
              <el-form-item label="用药情况">
                <el-select v-model="followUpForm.medicationAdherence">
                  <el-option label="规律用药" value="规律" />
                  <el-option label="间断用药" value="间断" />
                  <el-option label="未用药" value="不服药" />
                </el-select>
              </el-form-item>
              <el-button type="primary" :loading="followUpLoading" @click="submitFollowUp"
                >提交</el-button
              >
            </el-form>
          </el-card>

          <el-card class="section-card">
            <template #header>随访记录</template>
            <el-table :data="followUps" size="small">
              <el-table-column prop="followUpDate" label="日期" />
              <el-table-column prop="followUpType" label="类型" />
              <el-table-column prop="sourceType" label="来源" />
            </el-table>
          </el-card>
        </section>
      </main>

      <nav class="tab-bar">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </nav>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import request from './api/request';
import { ElMessage } from 'element-plus';

const token = ref(sessionStorage.getItem('patient_token') || '');
const patientName = ref(sessionStorage.getItem('patient_name') || '');
const activeTab = ref('home');
const loginLoading = ref(false);
const vitalLoading = ref(false);
const followUpLoading = ref(false);

const loginForm = reactive({ phone: '', idCard: '' });
const vitalForm = reactive({ metricType: 'SYSTOLIC_BP', metricValue: 120, measuredAt: '' });
const followUpForm = reactive({ followUpDate: '', symptoms: '', medicationAdherence: '规律' });

const plans = ref([]);
const vitals = ref([]);
const questionnaires = ref([]);
const messages = ref([]);
const followUps = ref([]);
const unreadCount = ref(0);
const riskLevel = ref('');
const riskEvidence = ref('');
const answers = reactive({});

const tabs = [
  { key: 'home', label: '首页' },
  { key: 'plans', label: '计划' },
  { key: 'followup', label: '随访' },
  { key: 'vitals', label: '上报' },
  { key: 'questionnaire', label: '问卷' },
  { key: 'messages', label: '消息' },
];

async function handleLogin() {
  if (!loginForm.phone || !loginForm.idCard) {
    ElMessage.warning('请输入手机号和身份证号');
    return;
  }
  loginLoading.value = true;
  try {
    const res = await request.post('/patient/login', loginForm);
    sessionStorage.setItem('patient_token', res.data.token);
    sessionStorage.setItem('patient_name', res.data.name);
    token.value = res.data.token;
    patientName.value = res.data.name;
    activeTab.value = 'home';
    loadAll();
  } finally {
    loginLoading.value = false;
  }
}

function handleLogout() {
  sessionStorage.removeItem('patient_token');
  sessionStorage.removeItem('patient_name');
  token.value = '';
}

async function loadAll() {
  await Promise.all([
    loadPlans(),
    loadVitals(),
    loadQuestionnaires(),
    loadMessages(),
    loadFollowUps(),
    loadRiskLevel(),
  ]);
}

async function loadRiskLevel() {
  const res = await request.get('/patient/risk-level');
  riskLevel.value = res.data?.riskLevel || 'STABLE';
  riskEvidence.value = res.data?.evidence || '暂无评估';
}

async function loadPlans() {
  const res = await request.get('/patient/plans');
  plans.value = res.data || [];
}

async function loadVitals() {
  const res = await request.get('/patient/vitals');
  vitals.value = res.data || [];
}

async function loadQuestionnaires() {
  const res = await request.get('/patient/questionnaires');
  questionnaires.value = res.data || [];
}

async function loadMessages() {
  const res = await request.get('/patient/messages');
  messages.value = res.data || [];
  unreadCount.value = messages.value.filter((item) => item.status === 'PENDING').length;
}

async function loadFollowUps() {
  const res = await request.get('/patient/follow-ups');
  followUps.value = res.data || [];
}

async function submitVital() {
  vitalLoading.value = true;
  try {
    await request.post('/patient/vitals', {
      ...vitalForm,
      measuredAt: vitalForm.measuredAt || new Date().toISOString().slice(0, 19).replace('T', ' '),
    });
    ElMessage.success('指标已上报');
    vitalForm.metricValue = 120;
    await loadVitals();
  } finally {
    vitalLoading.value = false;
  }
}

async function submitQuestionnaire(q) {
  const result = {};
  for (const question of parseQuestions(q.content)) {
    result[question.key] = answers[`${q.id}.${question.key}`] || '';
  }
  await request.post(`/patient/questionnaires/${q.id}/submit`, { answers: result });
  ElMessage.success('问卷已提交');
}

async function submitFollowUp() {
  followUpLoading.value = true;
  try {
    await request.post('/patient/follow-ups', {
      followUpDate: followUpForm.followUpDate || new Date().toISOString().slice(0, 10),
      symptoms: followUpForm.symptoms,
      medicationAdherence: followUpForm.medicationAdherence,
    });
    ElMessage.success('随访已提交');
    await loadFollowUps();
  } finally {
    followUpLoading.value = false;
  }
}

async function markRead(message) {
  if (message.status === 'PENDING') {
    await request.put(`/patient/messages/${message.id}/read`);
    await loadMessages();
  }
}

function parseQuestions(content) {
  try {
    return JSON.parse(content || '[]');
  } catch {
    return [];
  }
}

function riskLabel(level) {
  return { LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险', STABLE: '稳定' }[level] || level;
}

onMounted(() => {
  if (token.value) loadAll();
});
</script>

<style>
:root {
  --brand: #0fa47f;
  --ink: #1a1d21;
  --muted: #6b7280;
  --bg: #f7f8fa;
  --border: #e5e7eb;
}

* {
  box-sizing: border-box;
}
body {
  margin: 0;
  color: var(--ink);
  background: var(--bg);
  font-family: 'PingFang SC', 'HarmonyOS Sans SC', 'Microsoft YaHei', sans-serif;
}

.patient-app {
  max-width: 520px;
  min-height: 100vh;
  margin: 0 auto;
  background: var(--bg);
}

.login-page {
  display: flex;
  min-height: 100vh;
  flex-direction: column;
  justify-content: center;
  padding: 28px 24px;
  background: #ffffff;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 36px;
}

.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 6px;
  background: var(--brand);
}

.brand h1 {
  margin: 0;
  font-size: 22px;
}
.brand p {
  margin: 4px 0 0;
  color: var(--muted);
  font-size: 11px;
}

.login-form {
  margin-bottom: 12px;
}
.login-btn {
  width: 100%;
  height: 42px;
}
.demo-hint {
  color: var(--muted);
  font-size: 12px;
  text-align: center;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #ffffff;
  border-bottom: 1px solid var(--border);
}

.app-header strong {
  display: block;
  font-size: 17px;
}
.app-header span {
  color: var(--muted);
  font-size: 12px;
}

.app-main {
  padding: 14px 12px 76px;
}
.section-card {
  margin-bottom: 12px;
  border-radius: 8px;
}
.muted {
  color: var(--muted);
  font-size: 12px;
}

.risk-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding: 14px 16px;
  color: #ffffff;
  background: var(--brand);
  border-radius: 8px;
}

.risk-banner span {
  display: block;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}
.risk-banner strong {
  display: block;
  margin-top: 4px;
  font-size: 20px;
}
.risk-banner p {
  margin: 0;
  color: rgba(255, 255, 255, 0.85);
  font-size: 12px;
  text-align: right;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 12px;
}

.summary-item {
  padding: 14px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 8px;
}

.summary-item span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}
.summary-item strong {
  display: block;
  margin-top: 6px;
  font-size: 24px;
  color: var(--brand);
}

.plan-card h3 {
  margin: 10px 0 6px;
  font-size: 16px;
}
.plan-card p {
  margin: 0;
  color: var(--muted);
  font-size: 13px;
}
.plan-head {
  display: flex;
  justify-content: space-between;
  color: var(--brand);
  font-size: 13px;
  font-weight: 600;
}

.timeline-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--border);
  font-size: 13px;
}

.message-card {
  cursor: pointer;
}
.message-card.unread {
  border-color: rgba(15, 164, 127, 0.5);
}
.message-head {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  font-size: 13px;
}
.message-head span {
  color: var(--muted);
}
.message-card p {
  margin: 8px 0 0;
  color: var(--muted);
  font-size: 13px;
}

.tab-bar {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  max-width: 520px;
  margin: 0 auto;
  padding: 6px 0 env(safe-area-inset-bottom);
  background: #ffffff;
  border-top: 1px solid var(--border);
}

.tab-bar button {
  padding: 8px 2px;
  color: var(--muted);
  font-size: 12px;
  border: 0;
  background: transparent;
}

.tab-bar button.active {
  color: var(--brand);
  font-weight: 600;
}
</style>
