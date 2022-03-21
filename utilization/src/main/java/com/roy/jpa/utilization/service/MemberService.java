package com.roy.jpa.utilization.service;

import com.roy.jpa.utilization.domain.Address;
import com.roy.jpa.utilization.domain.Member;
import com.roy.jpa.utilization.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        validateIsUniqueMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateIsUniqueMember(Member member) {
        if (!memberRepository.findByName(member.getName()).isEmpty()){
            throw new IllegalStateException("회원명 중복");
        }
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

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
