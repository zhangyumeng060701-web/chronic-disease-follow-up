package com.example.followup.controller;

import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.FollowUp;
import com.example.followup.service.FollowUpService;
import com.example.followup.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/follow-ups")
@Api(tags = "随访记录管理")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;
    @Autowired
    private OperationLogService operationLogService;

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
    public Result<Void> add(@Valid @RequestBody FollowUp followUp, HttpServletRequest request) {
        followUpService.addFollowUp(followUp);
        String username = (String) request.getAttribute("username");
        operationLogService.log(username, "新增随访记录", "FollowUp", followUp.getId());
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑随访记录")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FollowUp followUp, HttpServletRequest request) {
        followUp.setId(id);
        followUpService.updateFollowUp(followUp);
        String username = (String) request.getAttribute("username");
        operationLogService.log(username, "编辑随访记录", "FollowUp", id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除随访记录")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        followUpService.deleteFollowUp(id);
        String username = (String) request.getAttribute("username");
        operationLogService.log(username, "删除随访记录", "FollowUp", id);
        return Result.success();
    }

    @GetMapping("/overdue")
    @ApiOperation("查询逾期未随访的患者")
    public Result<?> getOverdue() {
        return Result.success(followUpService.listOverdueFollowUps());
    }
}
