package com.mirotic.demorestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        String name = "Spring REST API";
        String description = "REST API development with Spring";

        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    @Parameters(method = "parametersForFree")
    public void free(int basePrice, int maxPrice, boolean isFree) {
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        event.update();

        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForFree() {
        return new Object[] {
            new Object[] {0, 0, true},
            new Object[] {0, 0, true},
            new Object[] {0, 0, true},
            new Object[] {0, 0, true}
        };
    }

    @Test
    @Parameters(method = "parametersForOffLine")
    public void offLine(String location, boolean isOffLine) {
        Event event = Event.builder()
                .location(location)
                .build();

        event.update();

        assertThat(event.isOffline()).isEqualTo(isOffLine);
    }

    private Object[] parametersForOffLine() {
        return new Object[] {
            new Object[] {null, false},
            new Object[] {"  ", false},
            new Object[] {"강남역", true}
        };
    }

}