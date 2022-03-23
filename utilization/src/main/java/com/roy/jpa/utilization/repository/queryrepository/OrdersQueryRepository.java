package com.roy.jpa.utilization.repository.queryrepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrdersQueryRepository {

    private final EntityManager entityManager;

    public List<OrdersQueryDTO> findOrderQueryDTOs() {
        List<OrdersQueryDTO> result = findOrders();
        result.forEach(i -> i.setOrderItems(findOrderItems(i.getOrderId())));
        return result;
    }

    public List<OrdersQueryDTO> findOrderQueryDTOsV6() {
        List<OrdersQueryDTO> result = findOrders();
        Set<Long> ids = result.stream()
                .map(OrdersQueryDTO::getOrderId)
                .collect(Collectors.toSet());
        Map<Long, List<OrderItemQueryDTO>> mapOfOrderItem = findOrderItemMap(ids);
        result.forEach(i -> i.setOrderItems(mapOfOrderItem.get(i.getOrderId())));
        return result;
    }

    public List<OrderFlatQueryDTO> findOrderQueryDTOsV7() {
        return entityManager.createQuery(
                "SELECT new com.roy.jpa.utilization.repository.queryrepository.OrderFlatQueryDTO" +
                        "(O.id, M.name, O.orderDate, D.address, O.status, I.name, OI.orderPrice, OI.count) " +
                        "FROM Order O " +
                        "       JOIN O.member M " +
                        "       JOIN O.delivery D " +
                        "       JOIN O.orderItems OI " +
                        "       JOIN OI.item I ", OrderFlatQueryDTO.class)
                .getResultList();
    }

    private Map<Long, List<OrderItemQueryDTO>> findOrderItemMap(Set<Long> orderIds) {
        List<OrderItemQueryDTO> result = entityManager.createQuery(
                "SELECT new com.roy.jpa.utilization.repository.queryrepository.OrderItemQueryDTO " +
                        "(OI.order.id, I.name, OI.orderPrice, OI.count) " +
                        "FROM OrderItem OI " +
                        "       JOIN OI.item I " +
                        "WHERE " +
                        "   OI.order.id IN :orderIds ", OrderItemQueryDTO.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        return result.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDTO::getOrderId));
    }

    private List<OrdersQueryDTO> findOrders() {
        return entityManager.createQuery(
                "SELECT new com.roy.jpa.utilization.repository.queryrepository.OrdersQueryDTO " +
                        "(O.id, M.name, O.orderDate, O.status, D.address) " +
                        "FROM Order O " +
                        "       JOIN O.member M " +
                        "       JOIN O.delivery D ", OrdersQueryDTO.class)
                .getResultList();
    }

    private List<OrderItemQueryDTO> findOrderItems(Long orderId) {
        return entityManager.createQuery(
                "SELECT new com.roy.jpa.utilization.repository.queryrepository.OrderItemQueryDTO " +
                        "(OI.order.id, I.name, OI.orderPrice, OI.count) " +
                        "FROM OrderItem OI " +
                        "       JOIN OI.item I " +
                        "WHERE " +
                        "OI.order.id = :orderId", OrderItemQueryDTO.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

}
