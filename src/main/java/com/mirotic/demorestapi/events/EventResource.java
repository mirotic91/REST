package com.mirotic.demorestapi.events;

import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Getter
public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        add(linkTo(EventController.class).withRel("query-events"));
    }
}
