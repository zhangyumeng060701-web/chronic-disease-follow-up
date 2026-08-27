package com.example.followup.controller;

import com.example.followup.entity.Dictionary;
import com.example.followup.mapper.DictionaryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DictionaryControllerTest {
    @Mock DictionaryMapper mapper;
    @InjectMocks DictionaryController controller;

    @Test void returnsActiveDictionaryItems() throws Exception {
        Dictionary item = new Dictionary(); item.setDictCode("RED"); item.setDictLabel("红色"); item.setSortNo(1); item.setIsActive(1);
        when(mapper.selectList(any())).thenReturn(List.of(item));
        mvc().perform(get("/api/dictionaries").param("type", "ALERT_LEVEL"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].dictCode").value("RED"));
    }

    @Test void emptyDictionaryReturnsEmptyArray() throws Exception {
        when(mapper.selectList(any())).thenReturn(List.of());
        mvc().perform(get("/api/dictionaries").param("type", "UNKNOWN"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test void missingTypeIsBadRequest() throws Exception {
        mvc().perform(get("/api/dictionaries")).andExpect(status().isBadRequest());
    }

    private MockMvc mvc() { return MockMvcBuilders.standaloneSetup(controller).build(); }
}
