package com.roy.jpa.utilization.repository.queryrepository;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderQueryDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
}
