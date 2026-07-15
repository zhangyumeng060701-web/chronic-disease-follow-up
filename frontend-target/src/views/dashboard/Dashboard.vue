<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover">
          <div class="card-value">{{ card.value }}</div>
          <div class="card-title">{{ card.title }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const cards = [
  { title: '管理患者总数', value: '--' },
  { title: '本月随访完成率', value: '--' },
  { title: '当前高危患者', value: '--' },
  { title: '当前失访患者', value: '--' }
]
</script>

<style scoped>
.card-value { font-size: 28px; font-weight: 600; color: #303133; }
.card-title { font-size: 14px; color: #909399; margin-top: 8px; }
</style>