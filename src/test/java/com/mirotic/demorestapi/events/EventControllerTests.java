package com.mirotic.demorestapi.events;

import com.mirotic.demorestapi.common.BaseControllerTests;
import com.mirotic.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventControllerTests extends BaseControllerTests {

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("이벤트 정상적으로 생성")
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
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event"),
                                fieldWithPath("free").description("free of new event"),
                                fieldWithPath("offline").description("offline of new event"),
                                fieldWithPath("eventStatus").description("eventStatus of new event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("요구되지 않는 파라미터로 이벤트 생성 요청시 에러 발생")
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
    @TestDescription("이벤트 생성시 입력 값이 비어있는 경우 에러 발생")
    public void createEvent_BadRequest_EmptyInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("이벤트 생성시 입력 값이 잘못된 경우 에러 발생")
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
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        IntStream.range(0, 30).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andDo(document("get-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("prev").description("link to prev page"),
                                linkWithRel("next").description("link to next page")
                        ),
                        requestParameters(
                                parameterWithName("page").description("page to retrieve, begin with and default is 0").optional(),
                                parameterWithName("size").description("size of the page to retrieve, default 10").optional(),
                                parameterWithName("sort").description("sort of the page to retrieve, default id desc").optional()
                        ),
                        responseFields(
                                fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("The number of this page"),
                                fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("The size of this page"),
                                fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("The total number of pages"),
                                fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("The total number of results"),
                                fieldWithPath("_embedded.eventList[].id").description("id of event"),
                                fieldWithPath("_embedded.eventList[].name").description("name of event"),
                                fieldWithPath("_embedded.eventList[].description").description("description of event"),
                                fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("_embedded.eventList[].beginEventDateTime").description("date time of begin event of event"),
                                fieldWithPath("_embedded.eventList[].endEventDateTime").description("date time of end event of event"),
                                fieldWithPath("_embedded.eventList[].location").description("location of event"),
                                fieldWithPath("_embedded.eventList[].basePrice").description("base price of event"),
                                fieldWithPath("_embedded.eventList[].maxPrice").description("max price of event"),
                                fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("limit of enrollment of event"),
                                fieldWithPath("_embedded.eventList[].free").description("free of event"),
                                fieldWithPath("_embedded.eventList[].offline").description("offline of event"),
                                fieldWithPath("_embedded.eventList[].eventStatus").description("eventStatus of event"),
                                fieldWithPath("_embedded.eventList[]._links.self.href").description("link to self"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.first.href").description("link to first page"),
                                fieldWithPath("_links.last.href").description("link to last page"),
                                fieldWithPath("_links.prev.href").description("link to prev page"),
                                fieldWithPath("_links.next.href").description("link to next page")
                        )
                ));
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("test")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 5, 7, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 5, 8, 12, 30))
                .beginEventDateTime(LocalDateTime.of(2019, 5, 13, 12, 0))
                .endEventDateTime(LocalDateTime.of(2019, 5, 17, 18, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .offline(true)
                .free(false)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return eventRepository.save(event);
    }

    @Test
    @TestDescription("이벤트 하나 상세 조회하기")
    public void getEvent() throws Exception {
        Event event = generateEvent(111);

        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andDo(document("get-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of event"),
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of event"),
                                fieldWithPath("free").description("free of event"),
                                fieldWithPath("offline").description("offline of event"),
                                fieldWithPath("eventStatus").description("eventStatus of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 조회의 경우 에러 발생")
    public void getEvent_NotFound() throws Exception {
        mockMvc.perform(get("/api/events/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @TestDescription("정상적으로 이벤트 수정")
    public void updateEvent() throws Exception {
        Event event = generateEvent(111);

        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String requestName = "Spring REST API";
        eventDto.setName(requestName);

        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(requestName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of event")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of event"),
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event of event"),
                                fieldWithPath("endEventDateTime").description("date time of end event of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of event"),
                                fieldWithPath("free").description("free of event"),
                                fieldWithPath("offline").description("offline of event"),
                                fieldWithPath("eventStatus").description("eventStatus of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @TestDescription("이벤트 수정시 입력값이 비어있는 경우 에러 발생")
    public void updateEvent_BadRequest_EmptyInput() throws Exception {
        Event event = generateEvent(111);

        EventDto eventDto = new EventDto();

        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("이벤트 수정시 입력값이 잘못된 경우 에러 발생")
    public void updateEvent_BadRequest_WrongInput() throws Exception {
        Event event = generateEvent(111);

        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBeginEventDateTime(LocalDateTime.of(2019, 7, 7, 12, 30));

        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정의 경우 에러 발생")
    public void updateEvent_NotFound() throws Exception {
        Event event = generateEvent(111);

        EventDto eventDto = modelMapper.map(event, EventDto.class);

        mockMvc.perform(put("/api/events/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
