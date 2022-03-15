package com.roy.jpa.theory.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class TaxiCompany {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phone;

    @OneToMany(mappedBy = "taxiCompany")
    private List<TaxiDriver> taxiDrivers = new ArrayList<>();

}
