package com.roy.jpa.theory.value;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class Period {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
