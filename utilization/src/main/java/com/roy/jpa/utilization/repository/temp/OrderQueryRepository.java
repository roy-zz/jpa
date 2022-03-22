package com.roy.jpa.utilization.repository.temp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager entityManager;

    public List<OrderQueryDTO> findOrderQueryDTOs() {
        return entityManager.createQuery(
                "SELECT new com.roy.jpa.utilization.repository.queryrepository.OrderQueryDTO" +
                        "(O.id, M.name, O.orderDate, O.status, D.address) " +
                        "FROM " +
                        "   Order O " +
                        "       JOIN O.member M " +
                        "       JOIN O.delivery D ", OrderQueryDTO.class)
                .getResultList();
    }

}
