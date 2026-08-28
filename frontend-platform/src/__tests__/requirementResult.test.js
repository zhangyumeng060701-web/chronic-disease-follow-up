import { describe, expect, it } from 'vitest';
import { groupTasksByType, TASK_TYPES } from '../constants/taskTypes';
import { createMockTasks } from '../mocks/requirement';
import { displayText, formatAcceptanceCriteria, getFiles } from '../utils/requirementFormat';

describe('requirement result contract', () => {
  it('keeps mock tasks aligned with the documented response fields', () => {
    const allowedKeys = [
      'acceptanceCriteria',
      'apiEndpoint',
      'description',
      'filesToModify',
      'title',
      'type',
    ];

    createMockTasks().forEach((task) => {
      expect(Object.keys(task).sort()).toEqual(allowedKeys);
      expect(Array.isArray(task.filesToModify)).toBe(true);
    });
  });

  it('supports every task type from the interface contract', () => {
    expect(createMockTasks().map((task) => task.type)).toEqual(
      TASK_TYPES.map((taskType) => taskType.type),
    );
  });

  it('keeps SECURITY tasks in the same grouped structure used by the page', () => {
    const securityTask = {
      type: 'SECURITY',
      title: '校验访问权限',
      description: '确认未授权请求被拒绝',
      filesToModify: [],
      apiEndpoint: null,
      acceptanceCriteria: '未授权请求返回 401',
    };

    const groupedTasks = groupTasksByType([securityTask]);

    expect(groupedTasks.SECURITY).toEqual([securityTask]);
    expect(TASK_TYPES.some((taskType) => taskType.type === 'SECURITY')).toBe(true);
  });

  it('provides safe display fallbacks', () => {
    expect(displayText(null)).toBe('--');
    expect(getFiles({ filesToModify: null })).toEqual([]);
    expect(formatAcceptanceCriteria(null)).toBe('--');
  });
});
