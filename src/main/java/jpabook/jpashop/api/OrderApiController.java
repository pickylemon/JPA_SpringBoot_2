package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
