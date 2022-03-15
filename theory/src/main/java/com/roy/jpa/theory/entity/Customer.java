package com.roy.jpa.theory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phone;
    @OneToMany(mappedBy = "customer")
    private List<TaxiEvent> taxiEvents = new ArrayList<>();

}
