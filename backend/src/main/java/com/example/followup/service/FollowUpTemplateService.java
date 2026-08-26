package com.example.followup.service;

import com.example.followup.dto.request.FollowUpTemplateQuery;
import com.example.followup.dto.response.FollowUpTemplateVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUpTemplate;

public interface FollowUpTemplateService {
    PageResponse<FollowUpTemplateVO> listTemplates(FollowUpTemplateQuery query);
    FollowUpTemplateVO createTemplate(FollowUpTemplate template);
    FollowUpTemplateVO updateTemplate(Long id, FollowUpTemplate template);
    void toggleTemplate(Long id);
}
