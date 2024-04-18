package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        //왜 빈 껍데기 memberForm을 가지고 가나? validation 관련. (??)
        return "members/createMemberForm";
    }

    @PostMapping("/new") //PRG 패턴
    public String create(@Validated MemberForm memberForm, BindingResult result, RedirectAttributes redirectAttributes){
        //Member 엔티티를 바로 안쓰고 memberForm을 쓰는 이유
        //1. 화면에서 필요한 Form객체의 정보와 엔티티의 명세가 다를 수 있다.
        //2. 엔티티의 validation과 form 객체의 validation 조건이 다를 수 있다.


        //MemberForm validation
        //통과 못하면
        log.info("\n\n memberForm = {} \n\n", memberForm);
        if(result.hasErrors()){
//            model.addAttribute(memberForm);
            //@ModelAttribute가 생략되어서 안 써도 되는것.
            //memberForm과 bindingResult를 모두 뷰에 넘겨준다.
            return "members/createMemberForm";
        }
        // 통과하면
        //memberForm으로부터 member Entity를 만들고(나)
        //memberService에 저장
        Member member = memberForm.toMember();
        memberService.join(member);

        log.info("\n\n member = {} \n\n", member);

        redirectAttributes.addAttribute("memberId", member.getId());
        return "redirect:/members/{memberId}";
    }

    @GetMapping("/{memberId}") //회원 상세 정보 (내가 만든 것)
    public String myInfo(Model model, @PathVariable Long memberId){
        Member member = memberService.findMember(memberId);
        MemberForm memberForm = MemberForm.toMemberForm(member);
        model.addAttribute("memberForm", memberForm);
        return "members/myInfo";
    }

    @GetMapping
    public String memberList(Model model){
//        List<Member> members = memberService.findAllMembers();
//        model.addAttribute("members", members);
        //멤버 엔티티를 그대로 뿌리는 것은 비추
        //엔티티는 가급적 순수하게 유지하는게 좋다.
        //단순히 템플릿 엔진 등 뷰에 뿌리는 용도라면 엔티티를 써도 된다. (어차피 서버사이드에서만 도는거라)
        //하지만!!(중요) API를 만들 때는 절대 엔티티를 넘겨서는 안된다.
        //엔티티의 변화(ex.필드 추가)가 API 스펙 변화를 발생시킴->불안정한 API


        List<Member> allMembers = memberService.findAllMembers();
        List<MemberForm> members = new ArrayList<>();
        for (Member member : allMembers) {
            members.add(MemberForm.toMemberForm(member));
        }

        model.addAttribute("members", members);
        return "members/memberList";
    }
}
