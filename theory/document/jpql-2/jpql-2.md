이번 장에서는 [JPQL-1(링크)](https://imprint.tistory.com/122?category=1061011)에 이어 JPQL의 기능들에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 사항은 강의나 공식문서에서 확인한다.
Class와 Entity에 대한 설명은 JPQL-1의 글에서 확인한다.

---

### 페이징 API

JPA에서는 아래 두 개의 API로 추상화하였다.
이로인해 JPA가 지원하는 모든 DB의 종류에 상관없이 같은 방식으로 페이징 처리가 가능하다.

- setFirstResult(int startPosition): 조회 시작 위치
- setMaxResult(int maxResult): 조회할 데이터의 수

100명의 택시 기사를 생성하고 이를 페이징 처리하는 코드는 아래와 같다.

```java
for (int i = 0; i < 100; i++) {
    TaxiDriver taxiDriver = new TaxiDriver();
    taxiDriver.setName(String.valueOf(i));
    entityManager.persist(taxiDriver);
}

String query = "SELECT TD FROM TaxiDriver TD";
List<TaxiDriver> results = entityManager.createQuery(query, TaxiDriver.class)
        .setFirstResult(50)
        .setMaxResults(20)
        .getResultList();
```

생성된 쿼리를 확인해보면 offset과 limit가 생성되어 원하는 양만큼의 데이터를 가져오게 된다.

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD */ select
            taxidriver0_.id as id1_2_,
            taxidriver0_.name as name2_2_,
            taxidriver0_.phone as phone3_2_,
            taxidriver0_.taxi_company_id as taxi_com4_2_ 
        from
            TaxiDriver taxidriver0_ limit ? offset ?
```

만약 DB가 MSSQL이라면 어떠한 쿼리가 생성되는지 확인해본다.

META-INF/persistence.xml의 dialect부분을 아래와 같이 MSSQL에 맞게 수정한다.

```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.SQLServer2016Dialect"/>
```

같은 Java 코드이지만 생성된 쿼리는 아래와 같이 다른 것을 확인할 수 있다.

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD */ with query as (select
            inner_query.*,
            row_number() over (
        order by
            current_timestamp) as __row__ 
        from
            ( select
                taxidriver0_.id as id1_2_,
                taxidriver0_.name as name2_2_,
                taxidriver0_.phone as phone3_2_,
                taxidriver0_.taxi_company_id as taxi_com4_2_ 
            from
                TaxiDriver taxidriver0_ ) inner_query ) select
                id1_2_,
                name2_2_,
                phone3_2_,
                taxi_com4_2_ 
            from
                query 
            where
                __row__ >= ? 
                and __row__ < ?
```

---

### JOIN

JPA에서는 Inner Join(내부 조인), Outer Join(외부 조인), Theta Join(세타 조인)을 지원한다.
Join들의 성질에 대해서 궁금하다면 글의 가장 하단부에 테이블 조인 방식의 글을 참고한다.

**Inner Join**

```java
String query = "SELECT TD FROM TaxiDriver TD JOIN TD.taxiCompany TC";
```

생성된 쿼리

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD 
    JOIN
        TD.taxiCompany TC */ select
            taxidriver0_.id as id1_2_,
            taxidriver0_.name as name2_2_,
            taxidriver0_.phone as phone3_2_,
            taxidriver0_.taxi_company_id as taxi_com4_2_ 
        from
            TaxiDriver taxidriver0_ 
        inner join
            TaxiCompany taxicompan1_ 
                on taxidriver0_.taxi_company_id=taxicompan1_.id
```

**Outer Join**

```java
String query = "SELECT TD FROM TaxiDriver TD LEFT JOIN TD.taxiCompany TC";
```

생성된 쿼리

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD 
    LEFT JOIN
        TD.taxiCompany TC */ select
            taxidriver0_.id as id1_2_,
            taxidriver0_.name as name2_2_,
            taxidriver0_.phone as phone3_2_,
            taxidriver0_.taxi_company_id as taxi_com4_2_ 
        from
            TaxiDriver taxidriver0_ 
        left outer join
            TaxiCompany taxicompan1_ 
                on taxidriver0_.taxi_company_id=taxicompan1_.id
```

**Theta Join**

```java
String query = "SELECT TD FROM TaxiDriver TD, TaxiCompany TC WHERE TD.name = TC.name";
```

생성된 쿼리

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD,
        TaxiCompany TC 
    WHERE
        TD.name = TC.name */ select
            taxidriver0_.id as id1_2_,
            taxidriver0_.name as name2_2_,
            taxidriver0_.phone as phone3_2_,
            taxidriver0_.taxi_company_id as taxi_com4_2_ 
        from
            TaxiDriver taxidriver0_ cross 
        join
            TaxiCompany taxicompan1_ 
        where
            taxidriver0_.name=taxicompan1_.name
```

또한 ON절에서 필터링하는 방식도 직접 쿼리를 작성하는 방식과 유사하다.
아래는 TaxiDriver와 TaxiCompany가 Join될 때 TaxiCompany의 이름 중에 "운수"라는 문구가 포함된 업체만 Join을 하는 코드와 생성된 쿼리이다.

```java
String query = "SELECT TD FROM TaxiDriver TD LEFT JOIN TD.taxiCompany TC ON TC.name LIKE '%운수%'";
```

생성된 쿼리

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD 
    LEFT JOIN
        TD.taxiCompany TC 
            ON TC.name LIKE '%운수%' */ select
                taxidriver0_.id as id1_2_,
                taxidriver0_.name as name2_2_,
                taxidriver0_.phone as phone3_2_,
                taxidriver0_.taxi_company_id as taxi_com4_2_ 
        from
            TaxiDriver taxidriver0_ 
        left outer join
            TaxiCompany taxicompan1_ 
                on taxidriver0_.taxi_company_id=taxicompan1_.id 
                and (
                    taxicompan1_.name like '%운수%'
                )
```

또한 ON절을 통해서 아무런 연관관계가 없는 테이블간에 Join도 가능하다.
아래는 TaxiCompany와 TaxiDriver를 Join하면서 외래 키가 아닌 이름이 같은 경우를 조회하는 경우의 코드와 생성된 쿼리이다.

```java
String query = "SELECT TD FROM TaxiDriver TD LEFT JOIN TD.taxiCompany TC ON TD.name = TC.name";
```

```sql
Hibernate: 
    /* SELECT
        TD 
    FROM
        TaxiDriver TD 
    LEFT JOIN
        TD.taxiCompany TC 
            ON TD.name = TC.name */ select
                taxidriver0_.id as id1_2_,
                taxidriver0_.name as name2_2_,
                taxidriver0_.phone as phone3_2_,
                taxidriver0_.taxi_company_id as taxi_com4_2_ 
        from
            TaxiDriver taxidriver0_ 
        left outer join
            TaxiCompany taxicompan1_ 
                on taxidriver0_.taxi_company_id=taxicompan1_.id 
                and (
                    taxidriver0_.name=taxicompan1_.name
                )
```

---

### Sub Query

서브 쿼리의 경우 일반적으로 우리가 사용하던 쿼리의 형태와 동일하다.
단, FROM 절에서는 서브 쿼리를 사용할 수 없으므로 Join을 사용하여 문제를 해결해야한다.

서브 쿼리를 사용하는 예시는 아래와 같다.

- 한 번이라도 고객을 태운적이 있는 기사 찾기

```java
String query = "SELECT TD " +
               "FROM TaxiDriver TD " +
               "WHERE (SELECT COUNT(TE) FROM TaxiEvent TE WHERE TD = TE.taxiDriver) > 0 ";
```

- 한 번이라도 택시를 이용한 고객 찾기

```java
String query = "SELECT C " +
               "FROM Customer C " +
               "WHERE (SELECT COUNT(TE) FROM TaxiEvent TE WHERE C = TE.customer) > 0 ";
```

아래와 같은 명령어를 통한 서브 쿼리도 가능하다.

- EXIST: 서브 쿼리의 결과가 존재하면 참, NOT EXIST와 같이 반대의 의미로도 사용이 가능하다.

예) 한 번이라도 택시를 이용한 고객 찾기

```java
String query = "SELECT C " +
               "FROM Customer C " +
               "WHERE EXISTS (SELECT COUNT(TE) FROM TaxiEvent TE WHERE C = TE.customer) ";
```

- ALL: 서브 쿼리의 내용을 모드 만족하면 참

- ANY, SOME, IN: 조건 중 하나라도 만족하면 참, IN은 앞에 NOT IN과 같이 반대의 의미로도 사용이 가능하다.

예) 업체에 속해있는 기사 찾기

```java
String query = "SELECT TD " +
               "FROM TaxiDriver TD " +
               "WHERE TD.taxiCompany = ANY (SELECT TC FROM TaxiCompany TC) ";
```

---

### 표현

대부분의 표현은 직접 DB의 쿼리를 작성하는 방식과 유사하다.
다만 Enum타입의 경우 직접 Enum의 패키지명까지 명시해주어야한다는 점이 다르다.

**기본 Case**
```java
"SELECT " +
"	CASE " +
"		WHEN TE.cost >= 100000 THEN '우량고객' " +
" 		ELSE '일반고객' " +
"	END " +
"FROM TaxiEvent TE ";
```

**단순 Case**
```java
"SELECT " +
"	CASE " +
"		WHEN TC.name = 'A운송' THEN '우량업체' " +
" 		WHEN TC.name = 'B통운' THEN '탈퇴한 업체' " +
"		ELSE '일반 업체' " +
"	END " +
"FROM TaxiCompany TC ";
```

**COALESCE**
조회 결과가 null이 아니면 치환한다.
아래의 쿼리는 휴대폰 번호가 없는 고객들의 휴대폰 번호를 null 대신 '번호없음'으로 변환하는 방법이다.

```java
"SELECT " +
"COALESCE(C.phone, '번호없음') " +
"FROM Customer C ";
```

**NULLIF**
두 값이 같으면 null을 반환하고 다르면 첫번째 값을 반환한다.
아래의 쿼리는 사용자의 이름이 '로이'면 null로 치환하고 그 외의 경우 사용자의 이름을 반환하는 방법이다.

```java
"SELECT " +
"NULLIF(C.name, '로이') " +
"FROM Customer C ";
```


---

참고한 강의: https://www.inflearn.com/course/ORM-JPA-Basic

JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference

위키백과: https://ko.wikipedia.org/wiki/%EC%9E%90%EB%B0%94_%ED%8D%BC%EC%8B%9C%EC%8A%A4%ED%84%B4%EC%8A%A4_API

테이블 조인 방식: https://sparkdia.tistory.com/18