package com.example.followup.service;

import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.response.AlertVO;
import com.example.followup.dto.response.PageResponse;

public interface AlertService {
    PageResponse<AlertVO> listAlerts(AlertQuery query);
    void resolveAlert(Long id);
}
