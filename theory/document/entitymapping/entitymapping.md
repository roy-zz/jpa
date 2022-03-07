이번 장에서는 엔티티 매핑에 대해서 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 사항은 강의나 공식문서에서 확인한다.

---

### 기본 매핑

#### @Entity

JPA를 사용하여 테이블과 매핑되는 클래스에는 @Entity 어노테이션이 붙어야한다.
@Entity 어노테이션이 붙은 클래스는 JPA의 관리 대상이 된다.

JPA에서는 우리가 작성한 클래스를 그대로 사용하는 것이 아니라 우리가 작성한 클래스를 기반으로 새로운 프록시 객체를 생성한다.
그렇기 때문에 몇가지 지켜야하는 규칙이 있다.

기본 생성자(파라미터가 없는)가 있어야하며 접근제한자는 public 또는 protected만 가능하다.
필자의 경우 Entity 클래스에 아래와 같은 어노테이션을 추가하여 사용한다.
```java
@NoArgsConstructor(AccessLevel.PROTECTED)
```
final 클래스는 상속이 불가능하기 때문에 Entity 클래스에서는 사용이 불가능하다.
추가로 Enum, Interface, Inner 클래스 또한 사용할 수 없다.
데이터가 저장되어야 하는 필드에 final도 사용해서는 안된다.

기본적으로 JPA의 엔티티 이름은 클래스 이름을 따라간다.
Entity 클래스 중 중복되는 클래스 이름이 존재하지 않는다면 기본값을 사용하는 것이 좋다.

---

#### @Table
@Entity 어노테이션은 JPA에게 테이블과 매핑된다는 것을 알리는 어노테이션이며
실제로 매핑될 테이블을 지정하는 어노테이션은 @Table이다.

@Table에서 사용할 수 있는 속성은 아래와 같다. (상세한 내용이 궁금하다면 하단의 공식문서를 참고한다.)

- name: 매핑될 테이블의 이름
- catalog: 데이터베이스 catalog와 매핑
- schema: 데이터베이스 schema와 매핑
- uniqueConstraints(DDL): DDL 생성 시에 유니크 제약 조건 생성

---

#### 데이터베이스 스키마 자동 생성

어플리케이션이 실행되는 시점에 @Entity 어노테이션이 붙어있는 클래스들을 참고하여 DDL을 생성한다.
기존에 Table을 생성하고 객체를 생성하던 방식에서 객체를 생성하면 Table이 생성되는 방식으로 변경된 것이다.
JPA는 설정되어 있는 방언에 맞게 사용하는 데이터베이스에 맞는 DDL을 생성한다.

스키마 자동 생성 옵션은 아래와 같다.
hibernate.hbm2ddl.auto

- create: 기존테이블 삭제 후 다시 생성(DROP + CREATE)
- create-drop: 종료시점에 DROP하고 실행시점에 CREATE
- update: 변경된 사항만 반영.
- validate: 엔티티와 테이블이 정상 매핑되어 있는지만 확인.
- none: 사용하지 않음.

대략 옵션을 보면 알겠지만 create, create-drop, update는 사용하면 안된다.
Entity와 Table이 정상적으로 매핑되었는지 확인하는 validate정도만 사용하면 될 듯하다.

---

### 필드와 컬럼 매핑

#### @Column

컬럼을 매핑하기 위해서 사용한다. 
@Column 어노테이션에 존재하는 속성은 아래와 같다.

- name: 필드와 매핑할 테이블의 컬럼 이름.  
  일반적으로 DB 컬럼 명명규칙과 Java 변수 명명규칙이 다른 경우가 많은데 이러한 경우에 사용한다.
- insertable, updatable: 등록 및 변경 가능 여부.  
  예를 들어 데이터가 생성된 일시를 나타내는 createdAt은 updatable = false로 설정하여 수정되지 않도록 한다.
- nullable: null값의 허용 여부를 설정한다.  
  false로 설정하면 DDL이 생성되는 시점에 NotNull 제약조건이 추가된다.
- unique: @Table의 uniqueConstraints와 동일하지만 단일 컬럼에 간단하게 사용하고 싶은 경우에 사용한다.
- columnDefinition: 데이터베이스 컬럼 정보를 직접 작성한다.
- length: 문자 길이의 제약조건을 나타내며 String 타입에만 사용된다.
- precision, scale: BigDecimal과 BigInteger에 사용된다.  
  precision은 소수점을 포함한 전체 자리수를 의미하며 scale은 소수의 자리수를 나타낸다.  
  
---

#### @Enumerated

자바의 enum 타입을 매핑할 때 사용한다.

- EnumType.ORDINAL: enum의 순서를 데이터베이스에 저장한다.
- EnumType.STRING: enum 이름을 데이터베이스에 저장한다.

만약 enum의 순서가 변경되었는데 속성이 ORDINAL이라면?
돌이킬수 없는 상황이 된다. ORDINAL 속성은 절대 사용하지 않도록 한다.

필자의 경우 enum의 이름(String)이 DB에 들어갈 필요가 없고 자원 낭비라고 판단하여 Enum 별로 Converter 클래스를 생성하여 사용하였다.
Converter를 만드는 방법은 [우아한형제들의 기술블로그(링크)](https://techblog.woowahan.com/2600/)에 잘 정리되어 있으므로 확인해보도록 한다.

---

#### @Temporal

날짜 타입인 Date, Calendar을 매핑할 때 사용한다.
최신 하이버네이트에서는 LocalDate, LocalDateTime을 사용할 때는 생략이 가능하다.

- TemporalType.DATE: 데이터베이스 date 타입과 매핑.
- TemporalType.TIME: 데이터베이스 time 타입과 매핑.
- TemporalType.TIMESTAMP: 데이터베이스의 timestamp 타입과 매핑.

---

#### @Transient

DB 컬럼과 매핑하고 싶지 않은 필드에 사용한다.
중간 결과값등을 저장하고 실제로 DB에는 반영하고 싶지 않은 경우에 사용한다.

---

### 기본 키 매핑

기본 키를 매핑하기 위해서 @Id, @GeneratedValue 어노테이션이 사용된다.
@GeneratedValue에는 아래와 같은 속성이 있다.

**GenerationType.IDENTITY**

기본 키 생성을 데이터베이스에 위임한다.
주로 MySQL, PostgreSQL, DB2에서 사용되며 MySQL의 auto_increment로 이해하면 된다.
JPA는 커밋 시점에 INSERT SQL을 실행하기 때문에 데이터베이스에 INSERT를 한 이후에 id값을 알 수 있다.
Identity 전략은 persist() 시점에 즉시 INSERT SQL을 실행하고 DB에서 식별자를 조회한다.
Identity 전략은 아래와 같이 사용한다.

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

**GenerationType.SEQUENCE** 

데이터베이스 시퀀스는 유일한 값인 시퀀스 오브젝트를 사용하며 대표적인 예로 Oracle이 있다.
@SequenceGenerator와 함께 사용되어야 하며 속성은 아래와 같다.

- name: Sequence Generator의 이름.
- sequenceName: 데이터베이스에 등록되어 있는 Sequence의 이름.
- initialValue: DDL을 생성하는 경우에만 사용되며 Sequence에 처음으로 사용되는 수치를 입력한다.
- allocationSize: Sequence를 한 번 호출하였을 때 증가하는 수치이며 추후 성능을 최적화하는데 필요하다.
- catalog, schema: 데이터베이스의 catalog, schema의 이름.

Sequence 전략은 아래와 같이 사용한다.

```java
@Entity
@SequenceGenerator(
        name = "USER_SEQ_GENERATOR",
        sequenceName = "USER_SEQ", // 매핑할 데이터베이스의 시퀀스 이름.
        initialValue = 1, allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ_GENERATOR")
    private Long id;
}
```

**GenerationType.TABLE** 

키 생성을 담당하는 전용 테이블을 만들어서 Sequence와 유사하게 작동하도록 하는 전략.
키를 생성하기위한 테이블이 추가로 유지보수되어야 하며 데이터를 삽입하기 위해서 키를 관리하는 테이블을 조회해야하는 성능 이슈가 있다.
하지만 모든 데이터베이스에 호환된다는 장점이 있다.
@TableGenerator 필요로 하며 속성은 아래와 같다.

- name: Table Generator의 이름.
- table: 키 생성을 담당하는 전용 테이블의 이름.
- pkColumnName: 시퀀스 컬럼명
- valueColumnNa: 시퀀스 값의 컬럼명
- pkColumnValue: 키로 사용할 값의 이름.
- initialValue: 최초로 생성되는 키의 값.
- allocationSize: 키를 한번 호출하였을 때 증가하는 수이며 성능 최적화에 사용된다.

Table 전략의 사용법은 아래와 같다.

```java
@TableGenerator(
        name = "USER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "USER_SEQ", allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "USER_SEQ_GENERATOR")
    private Long id;
}
```

**GenerationType.AUTO** 

DB에 따라 자동으로 지정, 기본값

---

참고한 강의: https://www.inflearn.com/course/ORM-JPA-Basic

JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference

위키백과: https://ko.wikipedia.org/wiki/%EC%9E%90%EB%B0%94_%ED%8D%BC%EC%8B%9C%EC%8A%A4%ED%84%B4%EC%8A%A4_API