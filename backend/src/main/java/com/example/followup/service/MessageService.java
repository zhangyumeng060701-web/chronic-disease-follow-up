/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.MessageCreateRequest;
import com.example.followup.dto.request.MessageQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Message;

/**
 * MessageService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface MessageService {

    /**
     * 查询listMessages。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<Message> listMessages(MessageQuery query);

    /**
     * 新增createMessage。
     *
     * @param request 参数说明
     * @return 返回值
     */
    Message createMessage(MessageCreateRequest request);

    /**
     * 执行markRead操作。
     *
     * @param id 参数说明
     */
    void markRead(Long id);
}
