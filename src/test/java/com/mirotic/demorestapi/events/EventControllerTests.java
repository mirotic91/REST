package com.mirotic.demorestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mirotic.demorestapi.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 생성")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 5, 7, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 5, 8, 12, 30))
                .beginEventDateTime(LocalDateTime.of(2019, 5, 13, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 5, 17, 18, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists());
    }

    @Test
    @TestDescription("요구되지 않는 파라미터로 요청시 에러 발생")
    public void createEvent_BadRequest_UnknownProperties() throws Exception {
        Event event = Event.builder()
                .id(111)
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 5, 7, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 5, 8, 12, 30))
                .beginEventDateTime(LocalDateTime.of(2019, 5, 13, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 5, 17, 18, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .free(true)
                .eventStatus(EventStatus.ENDED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우 에러 발생")
    public void createEvent_BadRequest_EmptyInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우 에러 발생")
    public void createEvent_BadRequest_WrongInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 5, 7, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 5, 6, 12, 30))
                .beginEventDateTime(LocalDateTime.of(2019, 5, 5, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 5, 4, 18, 0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

}
