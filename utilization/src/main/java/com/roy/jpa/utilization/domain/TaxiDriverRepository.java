package com.roy.jpa.utilization.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class TaxiDriverRepository {
    private final EntityManager entityManager;

    public TaxiDriver get(Long id) {
        return entityManager.find(TaxiDriver.class, id);
    }

    public void persist(TaxiDriver taxiDriver) {
        entityManager.persist(taxiDriver);
    }

}



