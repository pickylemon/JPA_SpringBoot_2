package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //@SpringBootTest에 이미 @ExtendWith 있음
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원 가입 성공")
    void MemberServiceTest1() {
        //given
        Member member = new Member();
        member.setName("memberA");
        //when
        Long savedId = memberService.join(member);
        //then
        em.flush(); //쿼리 확인용
        Member findMember = memberService.findMember(savedId);
        Assertions.assertThat(findMember).isSameAs(member);
        //왜? id값이 같으면(pk) 영속성 컨텍스트 안에서 하나로 관리된다.

        //insert쿼리가 안나간다.
        //1. @Transactional에 의해 rollback되므로 flush()안됨.
    }

    @Test
    @DisplayName("중복 회원 예외")
    void MemberServiceTest2() {
        //given
        Member member = new Member();
        member.setName("memberA");

        Member member2 = new Member();
        member.setName("memberA");
        //when
        Long savedId = memberService.join(member);
        //then
        Assertions.assertThatThrownBy(()->memberService.join(member2))
                .isInstanceOf(IllegalStateException.class);

        // Assertions.fail("예외가 발생해야 한다");
        // 이 코드가 실행되면 테스트가 잘못되었다는 것.
    }

}