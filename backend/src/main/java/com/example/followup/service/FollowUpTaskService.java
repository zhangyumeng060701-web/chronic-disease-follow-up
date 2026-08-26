package com.example.followup.service;

import com.example.followup.dto.request.FollowUpTaskQuery;
import com.example.followup.dto.response.FollowUpTaskVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUpPlan;

public interface FollowUpTaskService {
    PageResponse<FollowUpTaskVO> listTasks(FollowUpTaskQuery query);
    void completeTask(Long id);
    void cancelTask(Long id);
    void createTaskFromPlan(FollowUpPlan plan);
}
