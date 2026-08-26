package com.example.followup.service;

import com.example.followup.dto.request.QuestionnaireQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Questionnaire;

public interface QuestionnaireService {
    PageResponse<Questionnaire> listQuestionnaires(QuestionnaireQuery query);
    Questionnaire createQuestionnaire(Questionnaire questionnaire);
    Questionnaire updateQuestionnaire(Long id, Questionnaire questionnaire);
    void toggleQuestionnaire(Long id);
}
