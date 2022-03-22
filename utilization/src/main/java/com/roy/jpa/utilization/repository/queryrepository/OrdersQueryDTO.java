package com.roy.jpa.utilization.repository.queryrepository;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "orderId")
public class OrdersQueryDTO {
    private final Long orderId;
    private final String name;
    private final LocalDateTime orderDate;
    private final OrderStatus orderStatus;
    private final Address address;
    private List<OrderItemQueryDTO> orderItems;
}
