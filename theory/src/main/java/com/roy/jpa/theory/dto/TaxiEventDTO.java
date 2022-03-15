package com.roy.jpa.theory.dto;

public class TaxiEventDTO {
    private Long id;
    private Integer cost;

    public TaxiEventDTO(Long id, Integer cost) {
        this.id = id;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "TaxiEventDTO{" +
                "id=" + id +
                ", cost=" + cost +
                '}';
    }
}
