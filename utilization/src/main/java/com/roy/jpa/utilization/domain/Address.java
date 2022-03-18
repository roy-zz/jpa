package com.roy.jpa.utilization.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Embeddable;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
@RequiredArgsConstructor(access = PUBLIC)
public class Address {

    private String city;
    private String street;
    private String zipcode;

}
