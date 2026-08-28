/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

const MOCK_TASKS = [
  {
    type: 'FRONTEND',
    title: '随访列表页增加日期范围选择器',
    description: '在搜索栏增加日期范围选择器，并保持查询条件与分页联动。',
    filesToModify: ['frontend-target/src/views/followUp/FollowUpList.vue'],
    apiEndpoint: 'GET /api/follow-ups?startDate=xxx&endDate=xxx',
    acceptanceCriteria: '选择日期范围后查询，表格只显示范围内记录。',
  },
  {
    type: 'BACKEND',
    title: '随访查询接口支持日期范围参数',
    description: '确认随访查询接收 startDate 和 endDate 参数并传递到查询条件。',
    filesToModify: ['backend/src/main/java/.../FollowUpController.java'],
    apiEndpoint: 'GET /api/follow-ups',
    acceptanceCriteria: '传入 startDate/endDate 后，接口仅返回范围内的随访记录。',
  },
  {
    type: 'DATABASE',
    title: '确认随访日期字段索引',
    description: '检查随访记录表 followUpDate 查询条件是否具备合适索引。',
    filesToModify: [],
    apiEndpoint: null,
    acceptanceCriteria: '日期范围查询在常规数据量下响应稳定。',
  },
  {
    type: 'TEST',
    title: '补充日期范围筛选测试',
    description: '覆盖仅开始日期、仅结束日期和完整日期范围三类查询。',
    filesToModify: ['backend/src/test/java/.../FollowUpControllerTest.java'],
    apiEndpoint: 'GET /api/follow-ups',
    acceptanceCriteria: '日期范围筛选相关测试全部通过，边界日期场景有覆盖。',
  },
  {
    type: 'SECURITY',
    title: '校验查询权限与参数边界',
    description: '确认未登录请求会被拒绝，非法日期参数不会泄露内部错误。',
    filesToModify: [],
    apiEndpoint: 'GET /api/follow-ups',
    acceptanceCriteria: '未携带 token 返回 401，非法参数响应不包含堆栈或内部路径。',
  },
];

export function createMockTasks() {
  return MOCK_TASKS.map((task) => ({ ...task, filesToModify: [...task.filesToModify] }));
}

export function mockDecomposeRequirement(requirement, delay = 800) {
  return new Promise((resolve, reject) => {
    window.setTimeout(() => {
      if (requirement.includes('模拟失败')) {
        reject(new Error('Mock 模式已模拟拆解失败，请修改需求后重试'));
        return;
      }

      resolve({
        summary: `需求拆解：${truncateText(requirement, 30)}`,
        tasks: createMockTasks(),
        risk: '日期格式需要统一为 YYYY-MM-DD，筛选条件需覆盖边界日期。',
      });
    }, delay);
  });
}

function truncateText(text, maxLength) {
  return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text;
}
