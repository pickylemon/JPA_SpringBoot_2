//package jpabook.jpashop;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jpabook.jpashop.domain.Member;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//

//@Slf4j
//public class MemberRepository {
//    @PersistenceContext //스프링부트가 em을 주입해 줌
//    private EntityManager em;
//
//    public Long save(Member member) {
//        log.info("\n\n em = {}\n\n", em);
//        em.persist(member);
//        return member.getId();
//    }
//    //왜 Member로 반환 안 하나? 커맨드와 쿼리를 분리하는 원칙 (CQS패턴)
//    //:저장을 하고 나면 가급적 리턴값을 안 만든다. (side-effect있을 수 있으므로)
//
//    public Member find(Long id){
//        return em.find(Member.class, id);
//    }
//
//
//
//}
