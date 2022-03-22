package com.roy.jpa.utilization.repository.queryrepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemQueryDTO {
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
}
