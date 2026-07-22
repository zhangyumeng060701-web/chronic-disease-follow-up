import request from './request'

export function decomposeRequirement(requirement) {
  return request.post('/ai/decompose', { requirement })
}
