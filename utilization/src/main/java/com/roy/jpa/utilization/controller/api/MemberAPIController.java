package com.roy.jpa.utilization.controller.api;

import com.roy.jpa.utilization.domain.Member;
import com.roy.jpa.utilization.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping(value = "", headers = "X-API-VERSION=1")
    public List<Member> getMemberV1() {
        return memberService.findMembers();
    }

}