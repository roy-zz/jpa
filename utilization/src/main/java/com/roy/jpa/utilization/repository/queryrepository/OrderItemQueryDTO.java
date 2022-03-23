package com.roy.jpa.utilization.repository.queryrepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderItemQueryDTO {
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
    public OrderItemQueryDTO (OrderFlatQueryDTO dto) {
        this.orderId = dto.getOrderId();
        this.itemName = dto.getItemName();
        this.orderPrice = dto.getOrderPrice();
        this.count = dto.getCount();
    }
}
