package com.roy.jpa.utilization.service;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.Member;
import com.roy.jpa.utilization.domain.Order;
import com.roy.jpa.utilization.domain.OrderStatus;
import com.roy.jpa.utilization.domain.item.Book;
import com.roy.jpa.utilization.domain.item.Item;
import com.roy.jpa.utilization.exception.NotEnoughStockException;
import com.roy.jpa.utilization.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문 테스트")
    void orderTest() throws Exception {
        // GIVEN
        Member member = createMember();
        Item item = createBook("이것이 자바다", 10000, 10);
        int orderCount = 2;

        // WHEN
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // THEN
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문 시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * 2, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        assertEquals(8, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @Test
    @DisplayName("상품 주문 재고 수량 초과 테스트")
    public void notEnoughStockTest() {
        // GIVEN
        Member member = createMember();
        Item item = createBook("이것이 자바다", 10000, 10);
        int orderCount = 11;

        // WHEN & THEN
        Assertions.assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });
    }

    @Test
    @DisplayName("주문 취소 정상 작동 테스트")
    public void cancelOrderTest() {
        // GIVEN
        Member member = createMember();
        Book item = createBook("이것이 자바다", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // WHEN
        orderService.cancelOrder(orderId);

        // THEN
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        entityManager.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("Roy");
        member.setAddress(new Address("서울", "강남대로", "111-111"));
        entityManager.persist(member);
        return member;
    }

}
