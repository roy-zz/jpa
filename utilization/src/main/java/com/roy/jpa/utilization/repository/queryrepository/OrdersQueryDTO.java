package com.roy.jpa.utilization.repository.queryrepository;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "orderId")
public class OrdersQueryDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDTO> orderItems;

    public OrdersQueryDTO(OrderFlatQueryDTO dto) {
        this.orderId = dto.getOrderId();
        this.name = dto.getName();
        this.orderDate = dto.getOrderDate();
        this.orderStatus = dto.getOrderStatus();
        this.address = dto.getAddress();
    }

    public OrdersQueryDTO(OrdersQueryDTO queryDTO, List<OrderItemQueryDTO> itemQueryDTOs) {
        this.orderId = queryDTO.getOrderId();
        this.name = queryDTO.getName();
        this.orderDate = queryDTO.getOrderDate();
        this.orderStatus = queryDTO.getOrderStatus();
        this.address = queryDTO.getAddress();
        this.orderItems = itemQueryDTOs;
    }

}
