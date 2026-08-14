export function maskName(value = '') {
  if (!value) return ''
  return value[0] + '*'.repeat(Math.max(value.length - 1, 0))
}

export function maskPhone(value = '') {
  return value.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
}

export function maskIdCard(value = '') {
  return value.replace(/^(.{6}).{8}(.{4})$/, '$1********$2')
}

export function maskAddress(value = '') {
  if (!value) return ''
  const match = value.match(/^(.+?[\u533a\u53bf])/)
  return match ? `${match[1]}****` : '****'
}

export function maskSensitiveText(value = '', type = '') {
  const rules = {
    name: maskName,
    phone: maskPhone,
    idCard: maskIdCard,
    address: maskAddress
  }
  return (rules[type] || (() => value))(value)
}

