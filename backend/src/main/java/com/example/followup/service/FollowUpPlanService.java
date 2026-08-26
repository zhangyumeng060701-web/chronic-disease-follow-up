package com.example.followup.service;

import com.example.followup.dto.request.FollowUpPlanQuery;
import com.example.followup.dto.request.FollowUpPlanSaveRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.PageResponse;

public interface FollowUpPlanService {
    PageResponse<FollowUpPlanVO> listPlans(FollowUpPlanQuery query);
    FollowUpPlanVO createPlan(FollowUpPlanSaveRequest request);
    FollowUpPlanVO updatePlan(Long id, FollowUpPlanSaveRequest request);
    void deletePlan(Long id);
}
