package com.roy.jpa.utilization.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(STRING)
    private OrderStatus status;

    //==연관관계 편의 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료되었습니다.");
        } else {
            this.setStatus(OrderStatus.CANCEL);
            for (OrderItem orderItem : orderItems) {
                orderItem.cancel();
            }
        }
    }

    //==조회 로직==//
    public int getTotalPrice() {
        return this.orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    @Data
    @Builder
    public static class OrderResponseDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        public static OrderResponseDTO of(Order order) {
            return OrderResponseDTO.builder()
                    .orderId(order.getId())
                    .name(order.getMember().getName())
                    .orderDate(order.getOrderDate())
                    .orderStatus(order.getStatus())
                    .address(order.getMember().getAddress())
                    .build();
        }
    }

}
