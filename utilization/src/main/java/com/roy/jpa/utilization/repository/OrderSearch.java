package com.roy.jpa.utilization.repository;

import com.roy.jpa.utilization.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;
}
