<template>
  <el-dialog :title="title" :model-value="visible" width="600px" @update:model-value="close" @closed="emit('closed')">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="form.gender">
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input-number v-model="form.age" :min="0" :max="150" />
      </el-form-item>
      <el-form-item label="慢病类型" prop="diseaseType">
        <el-select v-model="form.diseaseType">
          <el-option v-for="(label, value) in DISEASE_TYPES" :key="value" :label="label" :value="value" />
        </el-select>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="form.idCard" />
      </el-form-item>
      <el-form-item label="住址">
        <el-input v-model="form.address" />
      </el-form-item>
      <el-form-item label="病史">
        <el-input v-model="form.medicalHistory" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="用药信息">
        <el-input v-model="form.medicationInfo" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="close">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="emit('submit')">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { DISEASE_TYPES } from '@/constants/domain'

defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  form: { type: Object, required: true },
  rules: { type: Object, default: () => ({}) },
  submitting: { type: Boolean, default: false }
})

const emit = defineEmits(['update:visible', 'submit', 'closed'])
const formRef = ref(null)

function close() {
  emit('update:visible', false)
}

defineExpose({ formRef })
</script>
