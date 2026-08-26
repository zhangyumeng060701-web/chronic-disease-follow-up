package com.example.followup.controller;

import com.example.followup.dto.request.FollowUpTemplateQuery;
import com.example.followup.dto.response.FollowUpTemplateVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.FollowUpTemplate;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.FollowUpTemplateService;
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
@RequestMapping("/api/follow-up-templates")
@Api(tags = "随访模板")
public class FollowUpTemplateController {

    @Autowired
    private FollowUpTemplateService templateService;

    @GetMapping
    @ApiOperation(value = "分页查询随访模板")
    public Result<PageResponse<FollowUpTemplateVO>> list(@Valid FollowUpTemplateQuery query) {
        return Result.success(templateService.listTemplates(query));
    }

    @PostMapping
    @ApiOperation(value = "新增随访模板")
    public Result<FollowUpTemplateVO> create(@RequestBody FollowUpTemplate template) {
        requireAdmin();
        return Result.success(templateService.createTemplate(template));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新随访模板")
    public Result<FollowUpTemplateVO> update(@PathVariable Long id, @RequestBody FollowUpTemplate template) {
        requireAdmin();
        return Result.success(templateService.updateTemplate(id, template));
    }

    @PutMapping("/{id}/toggle")
    @ApiOperation(value = "启用/停用随访模板")
    public Result<Void> toggle(@PathVariable Long id) {
        requireAdmin();
        templateService.toggleTemplate(id);
        return Result.success();
    }

    private void requireAdmin() {
        if (!SecurityUtils.isAdmin()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
