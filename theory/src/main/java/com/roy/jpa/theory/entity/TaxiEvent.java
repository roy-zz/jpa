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

    @AttributeOverrides({
            @AttributeOverride(
                    name = "startDateTime",
                    column = @Column(name = "event_start_datetime", columnDefinition = "COMMENT '고객 승차 일시'")),
            @AttributeOverride(
                    name = "endDateTime",
                    column = @Column(name = "event_end_datetime", columnDefinition = "COMMENT '고객 하차 일시'"))
    })
    private Period eventPeriod;

    @AttributeOverrides({
            @AttributeOverride(
                    name = "startDateTime",
                    column = @Column(name = "trip_start_datetime", columnDefinition = "COMMENT '고객 승차 일시'")),
            @AttributeOverride(
                    name = "endDateTime",
                    column = @Column(name = "trip_end_datetime", columnDefinition = "COMMENT '고객 하차 일시'"))
    })
    private Period tripPeriod;

}
