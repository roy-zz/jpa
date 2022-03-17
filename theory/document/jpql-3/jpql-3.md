이번 장에서는 [JPQL-1(링크)](https://imprint.tistory.com/122?category=1061011)에 이어 JPQL의 기능들에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 사항은 강의나 공식문서에서 확인한다.
Class와 Entity에 대한 설명은 JPQL-1의 글에서 확인한다.

---

### 엔티티 직접 사용

우리는 쿼리를 작성할 때 대부분 PK나 FK를 사용하여 조회한다.
이러한 검색 조건들은 객체가 아니라 대부분 숫자로 구성되어 있을 것이다.
하지만 JPQL에서 쿼리를 작성할 때 Entity의 id를 사용하는 것과 Entity 자체를 사용하는 것이 동일하게 취급된다.

예를 들어 아래는 Customer Entity에서 특정 결과를 카운트 하는 코드다.

```java
SELECT COUNT(C) FROM Customer C WHERE C.id > 50;
SELECT COUNT(C.id) FROM Customer C WHERE C.id > 50;
```

분명 코드는 다르지만 JPA에서는 Entity를 id로 구분하기 때문에 동일한 쿼리가 생성된다.

```sql
SELECT COUNT(C.id) AS count FROM customer C;
```

**동적 쿼리를 작성할 때 파라미터로 전달되는 식별자 값에도 Entity를 직접 사용할 수 있다.**

TaxiDriver를 Entity로 조회하는 방법
```java
String query = "SELECT TD FROM TaxiDriver TD WHERE TD = :taxiDriver";
List<TaxiDriver> result = entityManager.createQuery(query, TaxiDriver.class)
        .setParameter("taxiDriver", driver1)
        .getResultList();
```

TaxiDriver를 식별자로 조회하는 방법
```java
String query = "SELECT TD FROM TaxiDriver TD WHERE TD.id = :taxiDriverId";
List<TaxiDriver> result = entityManager.createQuery(query, TaxiDriver.class)
        .setParameter("taxiDriverId", driverId)
        .getResultList();
```

발생한 쿼리는 두 경우 모두 동일하다.

```sql
SELECT TD.* FROM taxi_driver TD WHERE TD.id = ?;
```

**동적 쿼리를 작성할 때 파라미터로 전달되는 외래 키 값에도 Entity를 직접 사용할 수 있다.**

TaxiDriver가 속한 TaxiCompany Entity로 조회하는 방법
```java
String query = "SELECT TD FROM TaxiDriver TD WHERE TD.taxiCompany = :taxiCompany";
List<TaxiDriver> result = entityManager.createQuery(query, TaxiDriver.class)
        .setParameter("taxiCompany", taxiCompany)
        .getResultList();
```

TaxiDriver가 속한 TaxiCompany Entity의 식별자(Taxi Driver에게는 외래 키)로 조회하는 방법
```java
String query = "SELECT TD FROM TaxiDriver TD WHERE TD.taxiCompany.id = :taxiCompanyId";
List<TaxiDriver> result = entityManager.createQuery(query, TaxiDriver.class)
        .setParameter("taxiCompanyId", taxiCompanyId)
        .getResultList();
```

발생한 쿼리는 두 경우 모두 동일하다.

```sql
SELECT TD.* FROM taxi_driver TD WHERE TD.taxi_company_id = ?;
```

---

### Named 쿼리 (정적쿼리)

지금까지 동적으로 변경되는 동적쿼리를 주로 알아보았다.
하지만 쿼리의 내용이 실시간으로 변경되지 않는 쿼리라면 정적 쿼리로 작성할 수 있다.
이렇게 사전에 정의된 쿼리는 애플리케이션 로딩 시점에 캐싱되므로 동적 쿼리에 비해 성능이 좋다.
또한 쿼리에 문제가 있는 경우 DB에 쿼리를 전달하기 이전인 컴파일 시점에 문제가 발견된다는 이점이 있다.

Named 쿼리를 지정하는 방법에는 어노테이션 방식과 XML에 정의 하는 두 가지 방식이 있다.

**어노테이션에 정의하는 방식**

Entity 클래스에 @NamedQuery 어노테이션을 추가하여 원하는 이름을 지정하고 쿼리를 작성한다.

```java
@NamedQuery(
        name = "TaxiDriver.findByName",
        query = "SELECT TD FROM TaxiDriver TD WHERE TD.name = :name"
)
public class TaxiDriver {
}
```

사용하는 방법은 Entity Manager의 createQuery() 대신 createNamedQuery()를 사용하고 Named 쿼리의 이름을 입력하면 된다.

```java
List<TaxiDriver> result = entityManager.createNamedQuery("TaxiDriver.findByName", TaxiDriver.class)
        .setParameter("name", "홍길동")
        .getResultList();
```

**XML에 정의하는 방식**

META-INF 하위 경로에 원하는 이름의 XML 파일을 만들고 아래와 같이 원하는 쿼리를 작성한다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">
    <named-query name="TaxiDriver.findByName">
        <query><![CDATA[
            SELECT TD
            FROM TaxiDriver TD
            WHERE TD.name = :name
        ]]></query>
    </named-query>
    <named-query name="TaxiDriver.countByName">
        <query>SELECT COUNT(TD) FROM TaxiDriver TD</query>
    </named-query>
</entity-mappings>
```

META-INF/persistence.xml에 아래와 같이 위에서 작성한 XML 파일을 등록한다.

```xml
<persistence-unit name="hello">
        <mapping-file>META-INF/namedQueryTaxiDriver.xml</mapping-file>
```

사용하는 방식은 어노테이션에 작성하는 방식과 동일하다.
이렇게 XML과 어노테이션이 동시에 사용되는 경우 우선순위는 XML에게 있다.
또한 애플리케이션의 배포 환경별로 쿼리가 바뀌어야하는 경우 어노테이션보다는 XML 방식이 유리하다.

---

### 벌크 연산

지금까지 우리는 단일 Entity의 속성을 변경하여 저장하는 방식에 대해서 알아보았다.
하지만 변경해야하는 데이터가 많고 지금까지 우리가 해왔던 방식으로 해야한다면 문제가 발생한다.

이번에는 한 번에 여러 Entity(정확히는 DB의 Row가 맞는 표현)를 수정하는 방법에 대해서 알아본다.

갑자기 회사의 정책이 변경되어 TaxiDriver의 전화번호를 저장할 때 010-1234-1234와 같은 형태가 아니라
01012341234와 같은 형태로 변경하는 상황을 가정해본다.

아래와 같은 방식으로 업데이트 쿼리를 전송하고 영향을 받은 Row의 수를 리턴받는다.
(REPLACE 함수는 기본적으로 등록되어 있지 않으므로 Dialect 클래스에 수동으로 등록해야한다.)

```java
String query = "UPDATE TaxiDriver TD SET TD.phone = FUNCTION('REPLACE', TD.phone, '-', '')";
int resultCount = entityManager.createQuery(query)
        .executeUpdate();
```

벌크 연산의 경우 **영속성 컨텍스트를 무시**하고 DB에 직접 쿼리를 전송한다.
이러한 이유 때문에 벌크 연산 이전과 이후에 코드상의 데이터와 DB 데이터의 싱크가 맞지 않을 수 있다.
벌크 연산을 사용해야한다면 벌크 연산을 먼저 실행시키고 영속성 컨텍스트를 초기화하여 1차 캐시의 내용을 지우고
추가로 필요한 데이터는 DB에서 조회해서 캐시를 채우도록 유도해야한다.

---

### 다형성

Entity를 상속 구조로 설계하였다면 다형성 쿼리를 사용하여 특정 자식 Entity만 조회가 가능하다.

예를 들어 'Vehicle'이라는 상위 클래스가 있고 이를 상속받은 'Car(C)', 'Bike(B)', 'Scooter(S)'가 있을 때
Car와 Bike만 조회하는 코드는 아래와 같다. (괄호 안의 문자는 @DiscriminatorValue를 의미한다.)

**Type**

```java
SELECT V FROM Vehicle V WHERE TYPE(V) IN (Car, Bike);
```

실제로 생성된 쿼리는 아래와 같다.

```sql
SELECT V FROM vehicle V WHERE v.DTYPE IN ('C', 'B');
```

**Treat**

Treat를 사용하여 자바에서 타입 캐스팅을 하는 것과 유사하게 사용할 수 있다.
Vehicle의 하위 클래스 중 Car에만 차대번호(vin)정보가 있다고 가정해본다.
타입이 Vehicle인 상태에서는 Car에만 존재하는 엔진의 정보를 조회할 수 없다.
하지만 Vehicle을 Car로 타입 캐스팅하면 VIN 정보의 조회가 가능해진다.
JPQL에서도 Treat를 통해서 자바와 유사하게 사용할 수 있다.

```java
SELECT V.* FROM Vehicle V WHERE TREAT(V as Car).vin = "gok2kg045hj1kd";
```

실제로 생성된 쿼리는 아래와 같다.

```sql
SELECT V.* FROM vehicle V WHERE V.DTYPE = "C" AND V.vin = "gok2kg045hj1kd";
```

---

참고한 강의: https://www.inflearn.com/course/ORM-JPA-Basic

JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference

위키백과: https://ko.wikipedia.org/wiki/%EC%9E%90%EB%B0%94_%ED%8D%BC%EC%8B%9C%EC%8A%A4%ED%84%B4%EC%8A%A4_API