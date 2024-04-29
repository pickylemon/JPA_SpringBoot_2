//package jpabook.jpashop;
//
//import jpabook.jpashop.domain.Address;
//import jpabook.jpashop.domain.Member;
//import jpabook.jpashop.domain.items.Book;
//import jpabook.jpashop.repository.ItemRepository;
//import jpabook.jpashop.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.annotation.Profile;
//import org.springframework.context.event.ApplicationContextEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//@Profile("local")
//public class TestDataInit {
//    private final MemberRepository memberRepository;
//    private final ItemRepository itemRepository;
//
//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void initData(){
//        log.info("test data init");
//        Member member1 = new Member();
//        member1.setName("춘식이");
//        member1.setAddress(new Address("서울", "street5", "12312"));
//
//        Member member2 = new Member();
//        member2.setName("라이언");
//        member2.setAddress(new Address("뉴욕", "street11", "121212"));
//
//        Book book1 = new Book();
//        book1.setName("book1");
//        book1.setIsbn("adfa11");
//        book1.setStockQuantity(100);
//        book1.setPrice(15000);
//        book1.setAuthor("kevin");
//
//        Book book2 = new Book();
//        book2.setName("book2");
//        book2.setIsbn("22adfa11");
//        book2.setStockQuantity(150);
//        book2.setPrice(223000);
//        book2.setAuthor("michael");
//
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//        itemRepository.save(book1);
//        itemRepository.save(book2);
//
//    }
//}
