package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.request.MessageCreateRequest;
import com.example.followup.dto.request.MessageQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.Message;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.MessageMapper;
import com.example.followup.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public PageResponse<Message> listMessages(MessageQuery query) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getRecipientType())) {
            wrapper.eq(Message::getRecipientType, query.getRecipientType());
        }
        if (StringUtils.hasText(query.getChannel())) {
            wrapper.eq(Message::getChannel, query.getChannel());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Message::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> page = new Page<>(query.getPage(), query.getSize());
        messageMapper.selectPage(page, wrapper);
        return PageResponseUtil.of(page, page.getRecords(), query.getPage(), query.getSize());
    }

    @Override
    public Message createMessage(MessageCreateRequest request) {
        Message message = new Message();
        message.setRecipientType(request.getRecipientType());
        message.setRecipientId(request.getRecipientId());
        message.setChannel(request.getChannel());
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setTemplateCode(request.getTemplateCode());
        message.setStatus(DomainConstants.MESSAGE_STATUS_SENT);
        messageMapper.insert(message);
        log.info("createMessage id={} channel={} recipient={}/{}",
                message.getId(), message.getChannel(), message.getRecipientType(), message.getRecipientId());
        return message;
    }

    @Override
    public void markRead(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "消息不存在");
        }
        message.setStatus(DomainConstants.MESSAGE_STATUS_READ);
        message.setReadTime(LocalDateTime.now());
        messageMapper.updateById(message);
    }
}
