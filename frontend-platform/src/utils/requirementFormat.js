export function displayText(value) {
  return value === null || value === undefined || value === '' ? '--' : value
}

export function getFiles(task) {
  return Array.isArray(task?.filesToModify) ? task.filesToModify.filter(Boolean) : []
}

export function formatAcceptanceCriteria(value) {
  if (Array.isArray(value)) return value.filter(Boolean).join('；') || '--'
  return displayText(value)
}

export function formatHistoryTime(value) {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  const parts = [
    date.getFullYear(),
    padDatePart(date.getMonth() + 1),
    padDatePart(date.getDate()),
    padDatePart(date.getHours()),
    padDatePart(date.getMinutes())
  ]
  return `${parts[0]}-${parts[1]}-${parts[2]} ${parts[3]}:${parts[4]}`
}

export function truncateText(text, maxLength) {
  return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text
}

export function maskSensitiveText(text) {
  return text
    .replace(/\b1[3-9]\d{9}\b/g, (phone) => `${phone.slice(0, 3)}****${phone.slice(7)}`)
    .replace(/\b\d{6}(?:19|20)\d{2}\d{4}\d{3}[\dXx]\b/g, (idCard) => `${idCard.slice(0, 6)}********${idCard.slice(-4)}`)
}

function padDatePart(value) {
  return `${value}`.padStart(2, '0')
}
