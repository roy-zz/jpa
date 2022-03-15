package com.roy.jpa.theory.entity;

import com.roy.jpa.theory.value.Period;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class TaxiEvent {

    @Id
    @GeneratedValue
    private Long id;

    private Integer cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxi_driver_id")
    private TaxiDriver taxiDriver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "startDateTime",
                    column = @Column(name = "event_start_datetime")),
            @AttributeOverride(
                    name = "endDateTime",
                    column = @Column(name = "event_end_datetime"))
    })
    private Period eventPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "startDateTime",
                    column = @Column(name = "trip_start_datetime")),
            @AttributeOverride(
                    name = "endDateTime",
                    column = @Column(name = "trip_end_datetime"))
    })
    private Period tripPeriod;

}
