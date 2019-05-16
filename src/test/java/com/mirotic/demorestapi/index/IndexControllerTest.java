package com.mirotic.demorestapi.index;

import com.mirotic.demorestapi.common.BaseControllerTests;
import org.junit.Test;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest extends BaseControllerTests {

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists());
    }
}
