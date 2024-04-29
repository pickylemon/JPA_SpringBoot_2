//package jpabook.jpashop;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jpabook.jpashop.domain.*;
//import jpabook.jpashop.domain.items.Book;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * 총 주문 2개(각 회원당 1주문, 1주문당 책 2개 포함)
// * userA
// * * JPA1 BOOK
// * * JPA2 BOOK
// * userB
// * * Spring1 BOOK
// * * Spring2 Book
// */
//@Component
//@RequiredArgsConstructor
//public class InitDb2 {
//    private final EntityManager em;
//
//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void init(){
//
////        initService.dbInit1();
//        Member member1 = createSampleMember("userA",new Address("서울", "1", "1111") );
//        em.persist(member1);
//
//        Book book1 = createSampleBook("JPA1",10000 , 100);
//        em.persist(book1);
//
//        Book book2 = createSampleBook("JPA2", 20000, 100);
//        em.persist(book2);
//
//        //생성메서드를 통해 order생성
//        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
//        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
//
//
//        Delivery delivery1 = new Delivery();
//        delivery1.setAddress(member1.getAddress());
//        Order order1 = Order.createOrder(member1, delivery1, orderItem1, orderItem2);
//        em.persist(order1);
//
//
////        initService.dbInit2();
//
//        Member member2 = createSampleMember("userB",new Address("Newyork", "2", "2222") );
//        em.persist(member2);
//
//        Book book3 = createSampleBook("SpringBook1",20000 , 200);
//        em.persist(book3);
//
//        Book book4 = createSampleBook("SpringBook2",40000, 300);
//        em.persist(book4);
//
//        //생성메서드를 통해 order생성
//        OrderItem orderItem3 = OrderItem.createOrderItem(book3, 20000, 3);
//        OrderItem orderItem4 = OrderItem.createOrderItem(book4, 40000, 4);
//
//
//        Delivery delivery2 = new Delivery();
//        delivery2.setAddress(member2.getAddress());
//        Order order2 = Order.createOrder(member2, delivery2, orderItem3, orderItem4);
//        em.persist(order2);
//    }
//    private Member createSampleMember(String name, Address address) {
//        Member member1 = new Member();
//        member1.setName(name);
//        member1.setAddress(address);
//        return member1;
//    }
//
//    private Book createSampleBook(String name, Integer price, Integer stockQuantity) {
//        Book book1 = new Book();
//        book1.setName(name);
//        book1.setPrice(price);
//        book1.setStockQuantity(stockQuantity);
//        return book1;
//    }
//
//}
