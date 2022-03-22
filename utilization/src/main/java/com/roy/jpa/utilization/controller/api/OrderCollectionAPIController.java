package com.roy.jpa.utilization.controller.api;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.Order;
import com.roy.jpa.utilization.domain.OrderItem;
import com.roy.jpa.utilization.domain.OrderStatus;
import com.roy.jpa.utilization.repository.OrderRepository;
import com.roy.jpa.utilization.repository.queryrepository.OrdersQueryDTO;
import com.roy.jpa.utilization.repository.queryrepository.OrdersQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
public class OrderCollectionAPIController {

    private final OrderRepository orderRepository;
    private final OrdersQueryRepository ordersQueryRepository;

    @GetMapping(value = "", headers = "X-API-VERSION=1")
    public List<Order> getOrdersV1() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(i -> {
            i.getMember().getName();
            i.getDelivery().getAddress();
            List<OrderItem> orderItems = i.getOrderItems();
            orderItems.forEach(j -> j.getItem().getName());
        });
        return orders;
    }

    @GetMapping(value = "", headers = "X-API-VERSION=2")
    public List<OrderDTO> getOrdersV2() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderDTO::of)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "", headers = "X-API-VERSION=3")
    public List<OrderDTO> getOrdersV3() {
        List<Order> orders = orderRepository.fetchAllByFetchJoin();
        return orders.stream()
                .map(OrderDTO::of)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "", headers = "X-API-VERSION=4")
    public List<OrderDTO> getOrdersV4(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllByFetchJoinWithPagination(offset, limit);
        return orders.stream()
                .map(OrderDTO::of)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "", headers = "X-API-VERSION=5")
    public List<OrdersQueryDTO> getOrdersV5() {
        return ordersQueryRepository.findOrderQueryDTOs();
    }

    @GetMapping(value = "", headers = "X-API-VERSION=6")
    public List<OrdersQueryDTO> getOrdersV6() {
        return ordersQueryRepository.findOrderQueryDTOsV6();
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class OrderDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems;
        public static OrderDTO of(Order order) {
            return OrderDTO.builder()
                    .orderId(order.getId())
                    .name(order.getMember().getName())
                    .orderDate(order.getOrderDate())
                    .orderStatus(order.getStatus())
                    .address(order.getDelivery().getAddress())
                    .orderItems(order.getOrderItems().stream().map(OrderItemDTO::of).collect(Collectors.toList()))
                    .build();
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    static class OrderItemDTO {
        private String itemName;
        private int orderPrice;
        private int count;
        public static OrderItemDTO of(OrderItem orderItem) {
            return OrderItemDTO.builder()
                    .itemName(orderItem.getItem().getName())
                    .orderPrice(orderItem.getOrderPrice())
                    .count(orderItem.getCount())
                    .build();
        }
    }

}
