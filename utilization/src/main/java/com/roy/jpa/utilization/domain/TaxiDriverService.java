package com.roy.jpa.utilization.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaxiDriverService {

    private final TaxiDriverRepository repository;

    public void saveTaxiDriver(TaxiDriver inputTaxiDriver) {
        repository.persist(inputTaxiDriver);
    }

    public TaxiDriver updateTaxiDriver(TaxiDriver.UpdateDTO dto) {
        TaxiDriver storedTaxiDriver = repository.get(dto.getId());
        storedTaxiDriver.update(dto);
        return repository.get(dto.getId());
    }

}
