package com.roy.jpa.utilization.service;

import com.roy.jpa.utilization.domain.TaxiDriver;
import com.roy.jpa.utilization.domain.TaxiDriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaxiDriverTest {

    @Autowired
    private TaxiDriverService service;

    @Test
    void mergeTest() {
        TaxiDriver taxiDriver1 = new TaxiDriver();
        taxiDriver1.setName("Roy");
        taxiDriver1.setPhone("01011112222");
        taxiDriver1.setLicense("333344445555");
        service.saveTaxiDriver(taxiDriver1);

        long storedTaxiDriverId = taxiDriver1.getId();

        TaxiDriver.UpdateDTO dto = TaxiDriver.UpdateDTO.builder()
                .id(storedTaxiDriverId)
                .name("Perry")
                .license("새로운 자격증 번호")
                .build();
        TaxiDriver storedTaxiDriver = service.updateTaxiDriver(dto);

        System.out.println("storedTaxiDriver 이름: " + storedTaxiDriver.getName());
        System.out.println("storedTaxiDriver 휴대폰 번호: " + storedTaxiDriver.getPhone());
        System.out.println("storedTaxiDriver 자격증 번호: " + storedTaxiDriver.getLicense());
    }

}
