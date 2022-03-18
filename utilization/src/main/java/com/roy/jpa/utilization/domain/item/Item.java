package com.roy.jpa.utilization.domain.item;


import com.roy.jpa.utilization.domain.Category;
import com.roy.jpa.utilization.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity - quantity < 0) {
            throw new NotEnoughStockException("Need more stock");
        } else {
            this.stockQuantity -= quantity;
        }
    }

}
