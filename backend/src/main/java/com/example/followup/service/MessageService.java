package com.example.followup.service;

import com.example.followup.dto.request.MessageCreateRequest;
import com.example.followup.dto.request.MessageQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Message;

public interface MessageService {
    PageResponse<Message> listMessages(MessageQuery query);
    Message createMessage(MessageCreateRequest request);
    void markRead(Long id);
}
