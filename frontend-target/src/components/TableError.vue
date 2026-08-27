<template>
  <el-alert
    v-if="error"
    :title="error"
    type="error"
    :closable="false"
    show-icon
    class="table-error"
  >
    <template #default>
      <el-button size="small" @click="emit('retry')">重试</el-button>
    </template>
  </el-alert>
</template>

<script setup>
defineProps({
  error: { type: String, default: '' }
})

const emit = defineEmits(['retry'])
</script>

<style scoped>
.table-error {
  margin-bottom: 12px;
}
</style>
