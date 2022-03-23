이번 장에서는 [xToMany 성능 최적화 1 (링크)](https://imprint.tistory.com/129?category=1061011)에 이어 xToMany 연관관계의 최적화에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 사항은 강의나 공식문서에서 확인한다.

---

### Step 5: DTO를 직접 조회하는 방법 (1 + N 쿼리 발생)

이번에는 Root Entity와 함께 xToMany를 제외한 관계의 데이터를 한 번에 조회하고
조회된 결과를 가지고 xToMany 관계의 데이터를 따로 조회하여 set 시키는 방식으로 진행한다.
이렇게 진행하는 경우 1 + N...(몇 번의 쿼리가 발생할지 모르는 상황)에서 1 + N으로 쿼리의 수를 줄일 수 있다.
또한 컬렉션 조인이 아니기 때문에 페이징 처리도 가능하다.

**OrderCollectionAPIController**

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
public class OrderCollectionAPIController {

    private final OrderRepository orderRepository;
    private final OrdersQueryRepository ordersQueryRepository;

    @GetMapping(value = "", headers = "X-API-VERSION=5")
    public List<OrdersQueryDTO> getOrdersV5() {
        return ordersQueryRepository.findOrderQueryDTOs();
    }
}
```

**OrderQueryDTO**
@Data 어노테이션에 @RequiredArgsContructor가 있기 때문에 final이 붙은 필드로 이루어진 생성자가 존재한다.

```java
@Data
@EqualsAndHashCode(of = "orderId")
public class OrdersQueryDTO {
    private final Long orderId;
    private final String name;
    private final LocalDateTime orderDate;
    private final OrderStatus orderStatus;
    private final Address address;
    private List<OrderItemQueryDTO> orderItems;
}
```

**OrderItemQueryDTO**

```java
@Data
@AllArgsConstructor
public class OrderItemQueryDTO {
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
}
```

**OrdersQueryRepository**

```java
@Repository
@RequiredArgsConstructor
public class OrdersQueryRepository {

    private final EntityManager entityManager;

    public List<OrdersQueryDTO> findOrderQueryDTOs() {
        List<OrdersQueryDTO> result = findOrders();
        result.forEach(i -> i.setOrderItems(findOrderItems(i.getOrderId())));
        return result;
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
```

최초에 findOrders를 호출하여 필요한 Order와 xToOne 연관관계인 Member, Delivery를 같이 조회한다.
xToOne 관계의 경우 Join을 하여도 row수가 증가하여 마치 Order의 양이 늘어나는 것과 같은 현상이 발생하지 않는다.
조회된 결과의 ID값을 사용하여 OrderItem을 조회하고 findOrders의 결과값에 빈 데이터를 채워넣는다.

1 + N... 에서 1 + N으로 변경되었다.

---

### Step 6: DTO를 직접 조회하여 Grouping하는 방법 (1 + 1 쿼리 발생, 페이징 가능)

Step 5와 동일하게 xToMany 관계를 제외한 데이터를 먼저 가져온다.
조회된 결과에서 id 값을 추출하여 IN 쿼리를 사용하여 xToMany 관계의 데이터를 가져온다.
이때 Map 자료구조를 사용하여 id를 키로 사용하고 Many 데이터를 value로 집어넣는다.

**OrderCollectionAPIController**

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
public class OrderCollectionAPIController {

    private final OrderRepository orderRepository;
    private final OrdersQueryRepository ordersQueryRepository;
    
    @GetMapping(value = "", headers = "X-API-VERSION=6")
    public List<OrdersQueryDTO> getOrdersV6() {
        return ordersQueryRepository.findOrderQueryDTOsV6();
    }
}
```

**OrdersQueryRepository**

```java
@Repository
@RequiredArgsConstructor
public class OrdersQueryRepository {

    private final EntityManager entityManager;
    
    public List<OrdersQueryDTO> findOrderQueryDTOsV6() {
        List<OrdersQueryDTO> result = findOrders();
        Set<Long> ids = result.stream()
                .map(OrdersQueryDTO::getOrderId)
                .collect(Collectors.toSet());
        Map<Long, List<OrderItemQueryDTO>> mapOfOrderItem = findOrderItemMap(ids);
        result.forEach(i -> i.setOrderItems(mapOfOrderItem.get(i.getOrderId())));
        return result;
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
}
```

Step 5에서 N 번의 쿼리가 추가로 발생했다면 Step 6에서는 IN 쿼리를 통해 추가로 한 번의 쿼리만 발생했다. 또한 페이징 처리가 가능하다.
발생한 쿼리는 아래와 같다.

**Order와 함께 xToOne 관계 데이터 조회**

```sql
select
    order0_.order_id as col_0_0_,
    member1_.name as col_1_0_,
    order0_.order_date as col_2_0_,
    order0_.status as col_3_0_,
    delivery2_.city as col_4_0_,
    delivery2_.street as col_4_1_,
    delivery2_.zipcode as col_4_2_ 
from
    orders order0_ 
inner join
    member member1_ 
        on order0_.member_id=member1_.member_id 
inner join
    delivery delivery2_ 
        on order0_.delivery_id=delivery2_.delivery_id
```

**Order Id 기준으로 xToMany 관계 데이터 조회**

```sql
select
    orderitem0_.order_id as col_0_0_,
    item1_.name as col_1_0_,
    orderitem0_.order_price as col_2_0_,
    orderitem0_.count as col_3_0_ 
from
    order_item orderitem0_ 
inner join
    item item1_ 
        on orderitem0_.item_id=item1_.item_id 
where
    orderitem0_.order_id in (
        ? , ?
    )
```

---

### Step 7: DTO를 직접 조회하여 Flatting하는 방법 (단 한 번의 쿼리, 페이징 불가)

이번에는 DTO를 생성하고 일반 조인을 사용하여 xToMany 연관관계 Entity까지 한 번에 조회한다.
조회한 데이터는 당연히 우리가 원하는 것과 다르게 xToMany 연관관계에 의해 늘어나 있는 상황이다.
이러한 상황에서 조회된 데이터를 가공하여 원하는 결과물을 만들어본다.
당연히 xToMany 연관관계인 Entity를 한 번에 조회하였기 때문에 페이징은 불가능하다.

한 번에 조회한 데이터를 담을 DTO를 생성한다.

**OrderFlatQueryDTO**

```java
@Data
@AllArgsConstructor
public class OrderFlatQueryDTO {
    // Order의 데이터
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private Address address;
    private OrderStatus orderStatus;
    // OrderItem Entity의 데이터
    private String itemName;
    private int orderPrice;
    private int count;
}
```

리포지토리에서 한 번에 데이터를 조회하여 위에서 생성한 DTO에 입력한다.

```java
@Repository
@RequiredArgsConstructor
public class OrdersQueryRepository {

    private final EntityManager entityManager;

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
}
```

OrdersQueryDTO와 OrderItemQueryDTO에 생성자를 추가한다.

**OrdersQueryDTO & OrderItemQueryDTO**

```java
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "orderId")
public class OrdersQueryDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDTO> orderItems;

    public OrdersQueryDTO(OrderFlatQueryDTO dto) {
        this.orderId = dto.getOrderId();
        this.name = dto.getName();
        this.orderDate = dto.getOrderDate();
        this.orderStatus = dto.getOrderStatus();
        this.address = dto.getAddress();
    }

    public OrdersQueryDTO(OrdersQueryDTO queryDTO, List<OrderItemQueryDTO> itemQueryDTOs) {
        this.orderId = queryDTO.getOrderId();
        this.name = queryDTO.getName();
        this.orderDate = queryDTO.getOrderDate();
        this.orderStatus = queryDTO.getOrderStatus();
        this.address = queryDTO.getAddress();
        this.orderItems = itemQueryDTOs;
    }
    
}
```

```java
@Data
@Builder
@AllArgsConstructor
public class OrderItemQueryDTO {
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
    public OrderItemQueryDTO (OrderFlatQueryDTO dto) {
        this.orderId = dto.getOrderId();
        this.itemName = dto.getItemName();
        this.orderPrice = dto.getOrderPrice();
        this.count = dto.getCount();
    }
}
```

컨트롤러에서 조회된 FlatDTO 데이터를 가공하여 우리가 원하는 데이터를 만든다.

**OrderCollectionAPIController**

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
public class OrderCollectionAPIController {

    private final OrderRepository orderRepository;
    private final OrdersQueryRepository ordersQueryRepository;

    @GetMapping(value = "", headers = "X-API-VERSION=7")
    public List<OrdersQueryDTO> getOrdersV7() {
        List<OrderFlatQueryDTO> flats = ordersQueryRepository.findOrderQueryDTOsV7();
        return flats.stream()
                .collect(groupingBy(OrdersQueryDTO::new,
                        mapping(OrderItemQueryDTO::new, toList())
                )).entrySet().stream()
                .map(e -> new OrdersQueryDTO(e.getKey(), e.getValue()))
                .collect(toList());
    }
}
```

stream 부분을 보면 OrdersQueryDTO가 중복되는 데이터를 groupingBy하였다.
groupingBy된 값을 key로 사용하였고 이외의 데이터는 리스트 형태로 value가 되어 Map에 담기게 되었다.
결과물은 HashMap<OrderQueryDTO, List<OrderItemQueryDTO>>의 형태가 된다.
만들어진 Map에서 Key와 Value를 뽑아 OrderQueryDTO를 생성하여 반환한다.

---

### Summary

사실상 목록을 조회하는 화면은 거의 모든 화면에서 페이징 처리가 되기 때문에 V1, V2, V3는 사용하지 못할 것이다.
같은 이유로 V5, V7또한 사용하지 못할 것이다. 

물론 선택 사항이지만 필자의 경우 default_batch_fetch_size를 통한 방법(V4)와 V6를 우선적으로 사용할 듯 싶다.

---

참고한 강의:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-API%EA%B0%9C%EB%B0%9C-%EC%84%B1%EB%8A%A5%EC%B5%9C%EC%A0%81%ED%99%94
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1
- https://www.inflearn.com/course/ORM-JPA-Basic

- JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference

- 위키백과: https://ko.wikipedia.org/wiki/%EC%9E%90%EB%B0%94_%ED%8D%BC%EC%8B%9C%EC%8A%A4%ED%84%B4%EC%8A%A4_API
