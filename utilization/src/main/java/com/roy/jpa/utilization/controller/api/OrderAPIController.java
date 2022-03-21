package com.roy.jpa.utilization.controller.api;

import com.roy.jpa.utilization.domain.Order;
import com.roy.jpa.utilization.repository.OrderRepository;
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

}
