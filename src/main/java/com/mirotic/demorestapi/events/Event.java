package com.mirotic.demorestapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mirotic.demorestapi.accounts.Account;
import com.mirotic.demorestapi.accounts.AccountSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    private LocalDateTime beginEnrollmentDateTime;

    private LocalDateTime closeEnrollmentDateTime;

    private LocalDateTime beginEventDateTime;

    private LocalDateTime endEventDateTime;

    private String location; // optional

    private int basePrice; // optional

    private int maxPrice; // optional

    private int limitOfEnrollment;

    private boolean offline;

    private boolean free;

    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        }

        if (this.location != null && !this.location.isBlank()) {
            this.offline = true;
        }
    }

    public boolean isManager(Account currentUser) {
        return this.manager != null && this.manager.equals(currentUser);
    }

}
