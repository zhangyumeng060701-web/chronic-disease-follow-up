/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import { describe, expect, it } from 'vitest';
import { createHistoryItem, normalizeHistoryItem, parseHistory } from '../utils/requirementHistory';

describe('requirement history', () => {
  it('returns an empty list for invalid storage data', () => {
    expect(parseHistory('{invalid json')).toEqual([]);
    expect(parseHistory(JSON.stringify({ id: 1 }))).toEqual([]);
  });

  it('normalizes legacy history and masks sensitive content', () => {
    const [item] = parseHistory(
      JSON.stringify([
        {
          id: 1,
          requirement: '联系患者 13812345678',
          title: '患者 13812345678 的需求',
          createTime: '2026-07-15',
        },
      ]),
    );

    expect(item.requirement).toBe('联系患者 138****5678');
    expect(item.title).toBe('患者 138****5678 的需求');
    expect(item.createdAt).toBe('2026-07-15');
  });

  it('rejects incomplete history items', () => {
    expect(normalizeHistoryItem(null)).toBeNull();
    expect(normalizeHistoryItem({ id: 1 })).toBeNull();
  });

  it('masks the persisted result as well as the requirement', () => {
    const item = createHistoryItem(
      '查询患者 13812345678',
      {
        summary: '查询患者 13812345678',
        tasks: [
          {
            type: 'FRONTEND',
            title: '展示 13812345678',
            description: '处理 13812345678',
            filesToModify: [],
            apiEndpoint: null,
            acceptanceCriteria: '不显示 13812345678',
          },
        ],
        risk: null,
      },
      new Date('2026-07-15T00:00:00.000Z'),
    );

    expect(JSON.stringify(item)).not.toContain('13812345678');
    expect(item.result.tasks[0].title).toContain('138****5678');
  });
});
