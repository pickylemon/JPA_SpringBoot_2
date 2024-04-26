package jpabook.jpashop.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     *
     * 이렇게 짜지 말라는 것!
     * 엔티티를 바로 반환하지 말라.
     * api를 사용하는 쪽에서 요구하는 정보 외에 엔티티의 모든 정보가 노출 될 수 있음
     * @JsonIgnore를 엔티티의 orders에 추가 : order 데이터는 빠진다.
     * but, 회원 조회 API가 다양할텐데 엔티티에 계속 화면 로직을 추가하는 건 맞지 않다.
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findAllMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findAllMembers();
        List<MemberDto> memberDtos = findMembers.stream().map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(memberDtos.size(), memberDtos);
        //v1처럼 바로 배열이나 리스트로 반환해버리면, 바로 json 배열 타입으로 나가게 되므로
        //유연성이 떨어진다.
        //그래서 Result로 한번 감싸서 내보내기
    }
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request){
        //커맨드성과 쿼리성을 분리한다.
        memberService.update(id, request.getName());
        Member findMember = memberService.findMember(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    //Dto를 두면 엔티티의 변화에 API스펙이 영향받지 않을 수 있다.
    //Dto는 API 스펙과 1:1
    static class MemberDto {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
