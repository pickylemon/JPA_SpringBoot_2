package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@RequiredArgsConstructor 스프링 데이터 JPA를 쓰면, em도 @Autowired로 주입 받을 수 있다. private+final
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

/*
    @PersistenceUnit
    private EntityManagerFactory emf;
    //emf는 @PersistenceUnit으로 주입받을 수 있다.
*/

    public void save(Member member){
        em.persist(member);
        //왜 Member로 반환 안 하나? 커맨드와 쿼리를 분리하는 원칙 (CQS패턴)
        //:저장을 하고 나면 가급적 리턴값을 안 만든다. (side-effect있을 수 있으므로)
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }
    public List<Member> findAll(){
        String query = "select m from Member m";
        return em.createQuery(query, Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        String query = "select m from Member m where m.name=:name";
        return em.createQuery(query, Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
