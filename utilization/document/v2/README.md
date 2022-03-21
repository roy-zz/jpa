### Optimizing Insert & Update

이번 장에서는 Entity 생성 및 갱신 최적화에 알아본다.
글의 하단부에 참고한 강의와 공식문서의 경로를 첨부하였으므로 자세한 사항은 강의나 공식문서에서 확인한다.

### Step 1 (Insert): Request Body에 Member Entity가 사용되는 경우

**MemberController**
```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberAPIController {

    private final MemberService memberService;

    @PostMapping(value = "", headers = "X-API-VERSION=1")
    public Member.ResponseDTO registerUserV1(
            @RequestBody @Valid Member member
    ) {
        Long id = memberService.join(member);
        Member storedMember = memberService.findOne(id);
        return Member.ResponseDTO.of(storedMember);
    }
}
```

**Member**
```java
@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @NotNull(message = "사용자 이름은 필수값입니다.")
    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @Data
    @Builder
    public static class ResponseDTO {
        private Long id;
        private String name;
        private Address address;
        public static ResponseDTO of(Member entity) {
            return ResponseDTO.builder()
                    .id(entity.id)
                    .name(entity.name)
                    .address(entity.address)
                    .build();
        }
    }

}
```

- 프레젠테이션 계층을 위한 로직이 Entity에 추가된다.
- Entity에 API의 파라미터를 검증하는 로직이 들어간다. 
- Member Entity를 사용하는 다른 API에서는 name은 null이 가능하고 address가 NotNull일 수도 있다. Entity하나로 모든 API의 호환을 맞추는 것은 불가능하다.
- Entity가 변경되면 API 스펙이 변해야한다. "DB 변경 -> Entity 변경 -> API 스펙 변경 -> 프론트엔드 코드 변경" 변경 지점이 전파된다.


Step 2에서는 이러한 문제를 해결하기 위해 API 스펙에 맞추어 별도의 DTO를 만든다.

### Step 2 (Insert): Request Body에 DTO가 사용되는 경우

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberAPIController {

    private final MemberService memberService;

    @PostMapping(value = "", headers = "X-API-VERSION=1")
    public Member.InsertResponseDTO registerUserV1(
            @RequestBody @Valid Member member
    ) {
        Long id = memberService.join(member);
        Member storedMember = memberService.findOne(id);
        return Member.InsertResponseDTO.of(storedMember);
    }

    @PostMapping(value = "", headers = "X-API-VERSION=2")
    public Member.InsertResponseDTO joinUserV2(
            @RequestBody @Valid Member.InsertRequestDTO request
    ) {
        Member member = Member.of(request);
        Long id = memberService.join(member);
        Member storedMember = memberService.findOne(id);
        return Member.InsertResponseDTO.of(storedMember);
    }

}
```

```java
@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public static Member of(InsertRequestDTO dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setAddress(dto.getAddress());
        return member;
    }

    @Data
    public static class InsertRequestDTO {
        @NotNull(message = "사용자 이름은 필수값입니다.")
        private String name;
        private Address address;
    }

    @Data
    @Builder
    public static class InsertResponseDTO {
        private Long id;
        private String name;
        private Address address;
        public static InsertResponseDTO of(Member entity) {
            return InsertResponseDTO.builder()
                    .id(entity.id)
                    .name(entity.name)
                    .address(entity.address)
                    .build();
        }
    }

}
```

Member Entity 클래스 내부에 생성을 위해 사용되는 InsertRequestDTO를 생성하였다.
Member Entity에 종속적으로 사용되는 DTO이기 때문에 Member Entity 내부에 생성하였다.
프레젠테이션 계층을 위한 로직은 InsertRequestDTO와 InsertResponseDTO로 분리되었다.
Entity가 변경되면 DTO 관련 로직만 변경될 뿐 API 스펙은 영향을 받지 않는다.

## Step 1 (Update): Request Body에 DTO가 사용되는 경우

수정도 Insert와 동일하다 수정을 위해 사용되는 DTO를 사용하고 RequestBody 와 ResponseBody에 사용한다.
(하단부의 예제는 글이 길어지는 관계로 Insert 관련 코드는 삭제하였음)

**MemberController**
```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberAPIController {

    private final MemberService memberService;

    @PutMapping(value = "/{memberId}", headers = "X-API-VERSION=1")
    public Member.UpdateResponseDTO updateUserV1(
            @PathVariable("memberId") Long memberId,
            @RequestBody @Valid Member.UpdateRequestDTO request
    ) {
        memberService.putMember(memberId, request.getName(), request.getAddress());
        Member storedMember = memberService.findOne(memberId);
        return Member.UpdateResponseDTO.of(storedMember);
    }

    @PatchMapping(value = "/{memberId}", headers = "X-API-VERSION=2")
    public Member.UpdateResponseDTO updateUserV2(
            @PathVariable("memberId") Long memberId,
            @RequestBody @Valid Member.UpdateRequestDTO request
    ) {
        memberService.patchMember(memberId, request.getName(), request.getAddress());
        Member storedMember = memberService.findOne(memberId);
        return Member.UpdateResponseDTO.of(storedMember);
    }

}
```

**Member**
```java
@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public static Member of(InsertRequestDTO dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setAddress(dto.getAddress());
        return member;
    }
    
    @Data
    public static class UpdateRequestDTO {
        private String name;
        private Address address;
    }

    @Data
    @Builder
    public static class UpdateResponseDTO {
        private Long id;
        private String name;
        private Address address;
        public static UpdateResponseDTO of(Member entity) {
            return UpdateResponseDTO.builder()
                    .id(entity.id)
                    .name(entity.name)
                    .address(entity.address)
                    .build();
        }
    }

}
```

**MemberService**
```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void putMember(Long memberId, String name, Address address) {
        Member member = findOne(memberId);
        member.setName(name);
        member.setAddress(address);
    }

    @Transactional
    public void patchMember(Long memberId, String name, Address address) {
        Member member = findOne(memberId);
        member.setName(Objects.nonNull(name) ? name : member.getName());
        member.setAddress(Objects.nonNull(address) ? address : member.getAddress());
    }

}
```

Patch와 Post 메서드를 통해 Member Entity를 업데이트 하였다.
또한 @Transactional 어노테이션 내부에서 Dirty Checking을 통한 업데이트를 진행하였다.

RestAPI에서 Put 메서드는 Null을 포함한 입력받은 값으로 Entity를 업데이트하고
Patch 메서드는 Null을 제외한 입력받은 값으로 Entity를 업데이트한다.

---

참고한 강의:

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-API%EA%B0%9C%EB%B0%9C-%EC%84%B1%EB%8A%A5%EC%B5%9C%EC%A0%81%ED%99%94
- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1
- https://www.inflearn.com/course/ORM-JPA-Basic

- JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference

위키백과: https://ko.wikipedia.org/wiki/%EC%9E%90%EB%B0%94_%ED%8D%BC%EC%8B%9C%EC%8A%A4%ED%84%B4%EC%8A%A4_API




## Optimizing xToOne Relation

## Optimizing xToMany Relation
