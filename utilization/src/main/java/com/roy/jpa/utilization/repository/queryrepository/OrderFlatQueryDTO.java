package com.roy.jpa.utilization.repository.queryrepository;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderFlatQueryDTO {
    // Order의 데이터
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private Address address;
    private OrderStatus orderStatus;
    // OrderItem Entity의 데이터
    private String itemName;
    private int orderPrice;
    private int count;
}

