package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.items.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em; //테스트 데이터를 넣을 목적으로, em을 바로 받아온다.


    @Test
    @DisplayName("상품 주문 테스트 - 주문 성공")
    void orderTest() {
        //given
        Member member = createMember("회원1");
        Book book = createBook("JPA 마스터", 10000, 10);

        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);
        OrderItem orderItem = order.getOrderItems().get(0);

        em.flush();
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).as("주문 상태는 ORDER여야 한다.").isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).as("주문한 상품 수가 일치해야한다.").isEqualTo(1);
        assertThat(order.getTotalPrice()).as("주문 가격은 상품가격 * 수량이다. 로직 체크").isEqualTo(book.getPrice() * orderCount);
        //주문 수량만큼 아이템 재고 수량이 줄어야 한다.
        assertThat(book.getStockQuantity()).as("주문 수량만큼 아이템 재고가 줄어야 한다.").isEqualTo(10 - orderItem.getCount());

    }



    @Test //이런 실패 테스트가 중요하다.
    @DisplayName("상품 주문 테스트 - 주문 실패(재고 초과)")
    void orderTest2() {
        //given
        Member member = createMember("회원1");
        Book book = createBook("JPA 마스터", 10000, 10);

        int orderCount = 11; //상품 재고보다 더 많이 주문

        //when
        //then
        em.flush();

        assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);

        //fail("재고 수량 부족 예외가 발생해야한다"); //Unreachable 해야한다.


    }
    @Test
    @DisplayName("주문 취소 테스트 - 취소 성공")
    void cancelSuccess() {
        //given
        Member member = createMember("회원1");
        Book book = createBook("JPA 마스터", 10000, 10);

        int orderCount = 8; //상품 재고보다 더 많이 주문

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);
        assertThat(order.getStatus()).as("주문상태가 취소로 변경").isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).as("아이템 재고 원복").isEqualTo(10);
    }
    @Test
    @DisplayName("주문 취소 테스트 - (배송완료) 주문 취소 실패")
    void cancelFail() {
        //given
        Member member = createMember("회원1");
        Book book = createBook("JPA 마스터", 10000, 10);

        int orderCount = 8; //상품 재고보다 더 많이 주문

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order order = orderRepository.findOne(orderId);
        order.getDelivery().setStatus(DeliveryStatus.COMP);
        //when
        //then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 배송완료된 상품은 취소가 불가합니다.");

        assertThat(order.getStatus()).as("주문은 취소 상태가 아님").isEqualTo(OrderStatus.ORDER);

    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "신도로", "123-123"));
        em.persist(member);
        return member;
    }
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }




}