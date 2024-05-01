package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    /**
     *
     * jpql로 쌩 string으로 동적 쿼리 작성
     *
     */
    public List<Order> findAllByString(OrderSearch orderSearch){
        //jpql은 동적쿼리에 약하다. (문자열이라 컴파일 체크가 어려움)
        //검색 조건은 둘 중 하나만 있는데(현재 요구사항에 따르면)
        //조건에 따라 sql이 동적으로 바뀌게 된다.
        //jpql로 동적쿼리를 작성하는 일은 컴파일 체크를 못받고 문자열을 다루는거라
        //버그가 발생하기 쉽다.
        //무엇보다 핵심 비즈니스 로직이 아닌 분기처리로 코드가 도배됨..

        String jpql = "select o from Order o join o.member m ";
//        String jpql = "select o from Order o join fetch o.member m ";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if(orderSearch.getOrderStatus() != null) {
            if(isFirstCondition) {
                jpql += "where ";
                isFirstCondition = false;
            } else {
                jpql += "and ";
            }
            jpql += "o.status = :status ";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition){
                jpql += "where ";
                isFirstCondition = false;
            } else {
                jpql += "and ";
            }
            jpql += "m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);//페이징

        //파라미터 바인딩도 검색 조건에 따라 분기
        if(orderSearch.getOrderStatus()!=null) {
            query.setParameter("status",orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

    }

    /**
     *
     * JPA표준 - criteria로 동적 쿼리 작성하기 (....너무 복잡. 실무에서 안 쓴다.)
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if(orderSearch.getOrderStatus()!=null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.equal(m.get("name"), orderSearch.getMemberName());
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        String jpql = "select o from Order o join fetch o.member m join fetch o.delivery d";

        return em.createQuery(jpql, Order.class).getResultList();
    }

    public List<Order> findAllWithItem(){
        String jpql = "select distinct o from Order o " +
                " join fetch o.member m " +
                " join fetch o.delivery d " +
                " join fetch o.orderItems oi " +
                //order와 orderItems를 join하면 뻥튀기 발생함
                // (orderItems 갯수만큼 order 중복 발생)
                " join fetch oi.item i ";

        return em.createQuery(jpql, Order.class)
                .setFirstResult(1)
                .setMaxResults(100)
                .getResultList();
    }
}
