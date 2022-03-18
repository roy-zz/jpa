package com.roy.jpa.utilization.service;

import com.roy.jpa.utilization.domain.Member;
import com.roy.jpa.utilization.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 테스트")
    public void joinTest() throws Exception {

        // GIVEN
        Member member = new Member();
        member.setName("roy");

        // WHEN
        Long memberId = memberService.join(member);

        // THEN
        assertEquals(member, memberRepository.findOne(memberId));
    }

    @Test
    @DisplayName("회원가입 이름 중복 발생 테스트")
    public void duplicatedNameTest() throws Exception {
        // GIVEN
        Member member1 = new Member();
        member1.setName("roy");
        Member member2 = new Member();
        member2.setName("roy");

        // WHEN
        memberService.join(member1);

        // THEN
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

    }

}
