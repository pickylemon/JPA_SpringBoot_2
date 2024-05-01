package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    /**
     * XToMany 조회의 최적화
     * XToMany를 조회하면, Many의 수에 맞춰 X도 같이 뻥튀기 되어 조회된다.
     */

    @GetMapping("/api/v1/orders") //가장 하면 안되는짓. 엔티티를 그대로 반환.
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            //하이버네이트5모듈을 사용하므로
            //lazy로딩 호출 후 정상적으로 프록시가 초기화된 객체만 api로 반환됨
            //(초기화 되지 않은 프록시는 그냥 null로 반환)
            //양방향 연관관계는 @JsonIgnore 붙여주는건 SimpleOrderApiController때와 동일

            //강제 초기화 코드
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            //OrderItem도 초기화하고(위), orderItem에 있는 item도 초기화(아래)
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        //엔티티가 아닌 Dto를 반환하는 예제
        //주의, dto를 반환하라는 건 엔티티를 한번 wrapping해서 반환하라는게 아니다.
        //(여기서 orderDto가 orderItem 엔티티를 그대로 포함하고 있음. 즉, 엔티티의 모든 정보가 그대로 노출됨)
        //엔티티에 대한 의존을 완전히 끊어야 한다. orderItem조차도 별도의 dto로 바꿔야함.

        //지연로딩이라 나가는 SQL수가 많다.
        return orderRepository.findAllByString(new OrderSearch())
                .stream().map(OrderDto::new).collect(Collectors.toList());
    }


    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
//        private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o){
            this.orderId = o.getId();
            this.name = o.getMember().getName();
            this.orderDate = o.getOrderDate();
            this.address = o.getDelivery().getAddress();
            this.orderStatus = o.getStatus();
            //orderItem은 엔티티라 lazy로딩 되므로 프록시 초기화를 시켜주는 코드 필요
            o.getOrderItems().stream().forEach(i->i.getItem().getName());
            this.orderItems = o.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(Collectors.toList());
            
            
        }

        @Getter
        static class OrderItemDto {
            private String itemName; //상품명
            private int orderPrice; //주문 가격
            private int count; //주문 수량
            OrderItemDto(OrderItem orderItem) {
                this.itemName = orderItem.getItem().getName();
                this.orderPrice = orderItem.getOrderPrice();
                this.count = orderItem.getCount();
            }

        }

    }
}
