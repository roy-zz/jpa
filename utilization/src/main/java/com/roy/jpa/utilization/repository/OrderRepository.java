package com.roy.jpa.utilization.repository;

import com.roy.jpa.utilization.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager entityManager;

    public void save(Order order) {
        entityManager.persist(order);
    }

    public Order findOne(Long id) {
        return entityManager.find(Order.class, id);
    }

    public List<Order> findAll() {
        return entityManager.createQuery("select o from Order o", Order.class)
                .getResultList();
    }
    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "SELECT O FROM Order O JOIN O.member M";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (Objects.nonNull(orderSearch.getOrderStatus())) {
            if (isFirstCondition) {
                jpql += " WHERE";
                isFirstCondition = false;
            } else {
                jpql += " AND";
            }
            jpql += " O.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " WHERE";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " M.name LIKE %:name%";
        }

        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (Objects.nonNull(orderSearch.getOrderStatus())) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> rootOrder = criteriaQuery.from(Order.class);
        Join<Object, Object> join = rootOrder.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if (Objects.nonNull(orderSearch.getOrderStatus())) {
            Predicate status = criteriaBuilder.equal(rootOrder.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = criteriaBuilder.like(join.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        criteriaQuery.where(criteria.toArray(new Predicate[0]));
        TypedQuery<Order> query = entityManager.createQuery(criteriaQuery).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllByFetchJoin() {
        return entityManager.createQuery(
                "SELECT " +
                        "   O " +
                        "FROM " +
                        "   Order O " +
                        "       JOIN FETCH O.member M " +
                        "       JOIN FETCH O.delivery D ", Order.class
        ).getResultList();
    }

    public List<Order> fetchAllByFetchJoin() {
        return entityManager.createQuery(
                "SELECT DISTINCT O " +
                        "FROM Order O " +
                        "       JOIN FETCH O.member M " +
                        "       JOIN FETCH O.delivery D " +
                        "       JOIN FETCH O.orderItems OI " +
                        "       JOIN FETCH OI.item I ", Order.class
        ).getResultList();
    }

    public List<Order> findAllByFetchJoinWithPagination(int offset, int limit) {
        return entityManager.createQuery(
                "SELECT O " +
                        "FROM Order O " +
                        "       JOIN FETCH O.member M " +
                        "       JOIN FETCH O.delivery D ", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
