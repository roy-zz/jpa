package com.roy.jpa.utilization.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    @JsonIgnore
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
