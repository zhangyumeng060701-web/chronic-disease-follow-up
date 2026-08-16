package com.example.followup.controller;

import com.example.followup.annotation.OperationLog;
import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.FollowUp;
import com.example.followup.service.FollowUpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/follow-ups")
@Api(tags = "随访记录管理")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;

    @GetMapping
    @ApiOperation("分页查询随访记录")
    public Result<PageResponse<FollowUpVO>> list(@Valid FollowUpQuery query) {
        return Result.success(followUpService.listFollowUps(query));
    }

    @GetMapping("/{id}")
    @ApiOperation("获取随访记录详情")
    public Result<FollowUp> getById(@PathVariable Long id) {
        return Result.success(followUpService.getFollowUpById(id));
    }

    @PostMapping
    @ApiOperation("新增随访记录")
    @OperationLog(operation = "新增随访记录", targetType = "FollowUp")
    public Result<Void> add(@Valid @RequestBody FollowUp followUp) {
        followUpService.addFollowUp(followUp);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑随访记录")
    @OperationLog(operation = "编辑随访记录", targetType = "FollowUp")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FollowUp followUp) {
        followUp.setId(id);
        followUpService.updateFollowUp(followUp);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除随访记录")
    @OperationLog(operation = "删除随访记录", targetType = "FollowUp")
    public Result<Void> delete(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return Result.success();
    }

    @GetMapping("/overdue")
    @ApiOperation("查询逾期未随访的患者")
    public Result<?> getOverdue() {
        return Result.success(followUpService.listOverdueFollowUps());
    }
}
