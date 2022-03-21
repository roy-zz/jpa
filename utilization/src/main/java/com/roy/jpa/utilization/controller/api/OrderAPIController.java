package com.roy.jpa.utilization.controller.api;

import com.roy.jpa.utilization.domain.Order;
import com.roy.jpa.utilization.repository.OrderRepository;
import com.roy.jpa.utilization.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/order")
public class OrderAPIController {

    private final OrderRepository orderRepository;

    @GetMapping(value = "", headers = "X-API-VERSION=1")
    public List<Order> getOrderV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        orders.forEach(i -> {
            i.getMember().getName();
            i.getDelivery().getAddress();
        });
        return orders;
    }

}
