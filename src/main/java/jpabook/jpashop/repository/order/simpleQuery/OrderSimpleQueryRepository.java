package jpabook.jpashop.repository.order.simpleQuery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;
    public List<OrderSimpleQueryDto> findOrderDtos() {
        //주의 OrderSimpleQueryDto(o)이렇게 바로 객체를 넘길 수 x (jpql에서 객체는 엔티티의 식별자로 인식함)
        //생성자에 일일이 넣어주어야 함.
        String jpql = "select new jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
                +" from Order o "
                +" join o.member m "
                +" join o.delivery d";

        return em.createQuery(jpql, OrderSimpleQueryDto.class).getResultList();
        //JPA는 엔티티나 값 객체(ex.임베디드 타입)만 반환할 수 있다. 일반 Dto는 그냥 반환 못함
        //query에서 new로 dto를 지정해주어야 한다.!!


    }
}
