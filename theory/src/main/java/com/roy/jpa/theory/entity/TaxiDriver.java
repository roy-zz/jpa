package com.roy.jpa.theory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class TaxiDriver {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxi_company_id")
    private TaxiCompany taxiCompany;

    @OneToMany(mappedBy = "taxiDriver")
    private List<TaxiEvent> taxiEvents = new ArrayList<>();

}
