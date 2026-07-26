package com.example.followup.service;

import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUp;

public interface FollowUpService {
    PageResponse<FollowUpVO> listFollowUps(FollowUpQuery query);
    FollowUp getFollowUpById(Long id);
    void addFollowUp(FollowUp followUp);
    void updateFollowUp(FollowUp followUp);
    void deleteFollowUp(Long id);
}
