package com.roy.jpa.utilization.controller.api;

import com.roy.jpa.utilization.domain.Order;
import com.roy.jpa.utilization.repository.OrderRepository;
import com.roy.jpa.utilization.repository.queryrepository.OrderQueryDTO;
import com.roy.jpa.utilization.repository.queryrepository.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/order")
public class OrderAPIController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping(value = "", headers = "X-API-VERSION=1")
    public List<Order> getOrderV1() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(i -> {
            i.getMember().getName();
            i.getDelivery().getAddress();
        });
        return orders;
    }

    @GetMapping(value = "", headers = "X-API-VERSION=2")
    public List<Order.OrderResponseDTO> getOrderV2() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(Order.OrderResponseDTO::of)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "", headers = "X-API-VERSION=3")
    public List<Order.OrderResponseDTO> getOrderV3() {
        List<Order> orders = orderRepository.findAllByFetchJoin();
        return orders.stream()
                .map(Order.OrderResponseDTO::of)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "", headers = "X-API-VERSION=4")
    public List<OrderQueryDTO> getOrderV4() {
        return orderQueryRepository.findOrderQueryDTOs();
    }

}
