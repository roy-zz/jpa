package com.roy.jpa.utilization.repository;

import com.roy.jpa.utilization.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager entityManager;

    public void save(Item item) {
        if (Objects.isNull(item.getId())) {
            entityManager.persist(item);
        } else {
            entityManager.merge(item);
        }
    }

    public Item findOne(Long id) {
        return entityManager.find(Item.class, id);
    }

    public List<Item> findAll() {
        return entityManager.createQuery("SELECT I FROM Item I", Item.class)
                .getResultList();
    }

}
