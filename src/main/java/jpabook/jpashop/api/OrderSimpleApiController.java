package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne 즉, One을 조회하는 것에서의 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Member에 대해 Lazy 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        //api 스펙은 명확해야한다.

        //order 2개 조회됨.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // 각 order에 대해 루프를 돌게된다.(총 2회)
        // 각 order에 대한 member, delivery 조회 쿼리 나감.
        // N + 1문제
        // EAGER로 바꿔도 해결되지 않는다.
        // find()같은 메서드로 조회하는 게 아니라
        // 이미 작성된 JPQL이 실행되는 것이기때문에 최적화의 여지가 없다.
        // 오히려 양방향 연관관계에 따라 예상치 못한, 더 복잡한 쿼리가 나간다.
        List<SimpleOrderDto> dtoList = orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());

        return dtoList;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        //v2를 성능 최적화 한게 v3
        //fetch join으로 select order시 member와 delivery도 같이 가져온다.
        //프록시도 아니고 정말 아예 조인 쿼리를 날려서 같이 가져옴
        //**반복 : LAZY로딩 디폴트 + 필요에 따라 명시적으로 fetch조인으로 성능 최적화(N+1문제 해결)
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
            this.orderId = orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;
        }

        public SimpleOrderDto(Order order){
            this(order.getId(), order.getMember().getName(), order.getOrderDate(), order.getStatus(), order.getDelivery().getAddress());
            //getMember().getName()과 getDelivery().getAddress()에서 LAZY 로딩 초기화 발생. (N+1쿼리)
        }
    }
}
