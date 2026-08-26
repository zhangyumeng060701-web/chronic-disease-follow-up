package com.example.followup.controller;

import com.example.followup.dto.request.MessageCreateRequest;
import com.example.followup.dto.request.MessageQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Message;
import com.example.followup.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@Api(tags = "消息中心")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    @ApiOperation(value = "分页查询消息")
    public Result<PageResponse<Message>> list(@Valid MessageQuery query) {
        return Result.success(messageService.listMessages(query));
    }

    @PostMapping
    @ApiOperation(value = "发送消息")
    public Result<Message> create(@Valid @RequestBody MessageCreateRequest request) {
        return Result.success(messageService.createMessage(request));
    }

    @PutMapping("/{id}/read")
    @ApiOperation(value = "标记消息已读")
    public Result<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return Result.success();
    }
}
