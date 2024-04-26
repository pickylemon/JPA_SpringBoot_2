package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //JPA는 tx단위로 작동한다.
public class MemberService {
    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional(readOnly = false)
    public Long join(Member member) {
        //중복회원 검증 로직
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //이름이 중복인 회원이면 예외를 터뜨린다.
        //사실 이 비즈니스 로직은 멀티스레드와 같은 상황에서 문제가 될 수 있으므로
        //(ex.동시에 같은 이름의 회원이 검증 성공함)
        //최후의 보루로 DB에서 name에 unique 제약조건을 주어야 한다.
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findAllMembers(){
        return memberRepository.findAll();
    }

    //특정 회원 조회
    public Member findMember(Long memberId){
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        //수정할때는 가급적 변경감지를 사용할 것.
        Member findMember = memberRepository.findOne(id);
        findMember.setName(name);

        //return 값을 Member로 해도 된다.
        //단, Command성(변경성 메서드)과 Query(조회성 메서드)가 분리되는 편이 더 좋기 때문에
        //유지보수에도 더 좋다.

    }
}
