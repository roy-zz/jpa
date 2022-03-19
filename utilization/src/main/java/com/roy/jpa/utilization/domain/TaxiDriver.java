package com.roy.jpa.utilization.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@ToString
@Table(name = "taxi_driver")
@Getter @Setter
public class TaxiDriver {

    @Id
    @GeneratedValue
    @Column(name = "taxi_driver_id")
    private Long id;

    private String name;
    private String license;
    private String phone;

    @Builder
    @Getter @Setter
    public static class UpdateDTO {
        private Long id;
        private String name;
        private String license;
    }

    public void update(UpdateDTO dto) {
        Assert.notNull(dto.name, "이름은 필수입니다.");
        Assert.notNull(dto.license, "자격증 번호는 필수입니다.");
        this.name = dto.name;
        this.license = dto.license;
    }

}
