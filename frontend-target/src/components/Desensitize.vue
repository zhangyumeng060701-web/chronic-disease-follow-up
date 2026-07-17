<template>
  <span>{{ displayText }}</span>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
import { maskSensitiveText } from '@/utils/desensitize'

const props = defineProps({
  text: { type: String, default: '' },
  type: { type: String, required: true }
})

const userStore = useUserStore()

const displayText = computed(() => {
  if (userStore.isAdmin || !props.text) return props.text
  return maskSensitiveText(props.text, props.type)
})
</script>

