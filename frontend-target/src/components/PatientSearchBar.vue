<template>
  <el-form :model="model" inline @submit.prevent>
    <el-form-item label="姓名">
      <el-input v-model="model.name" placeholder="请输入" clearable @input="debouncedSearch" />
    </el-form-item>
    <el-form-item label="慢病类型">
      <el-select v-model="model.diseaseType" placeholder="请选择" clearable @change="emitSearch">
        <el-option
          v-for="(label, value) in DISEASE_TYPES"
          :key="value"
          :label="label"
          :value="value"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="emitSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive, watch } from 'vue';
import { DISEASE_TYPES } from '@/constants/domain';
import { useDebounce } from '@/composables/useDebounce';

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
});

const emit = defineEmits(['update:modelValue', 'search', 'reset']);

const model = reactive({ name: '', diseaseType: '' });

watch(
  () => props.modelValue,
  (value) => {
    model.name = value?.name || '';
    model.diseaseType = value?.diseaseType || '';
  },
  { immediate: true, deep: true },
);

watch(model, () => emit('update:modelValue', { ...model }), { deep: true });

const debouncedSearch = useDebounce(() => emit('search'), 300);

function emitSearch() {
  emit('search');
}

function handleReset() {
  model.name = '';
  model.diseaseType = '';
  emit('reset');
}
</script>
