package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.items.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model){
        List<Member> members = memberService.findAllMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/new")
    public String createOrder(@RequestParam("memberId") Long memberId,
                              @RequestParam("itemId") Long itemId,
                              @RequestParam("count") int count){
        //유효성 검사?

        //통과했다고 치면
        //조회는 상관없지만,
        //핵심 비즈니스로직이 있는 커맨드성의 코드인 경우
        //컨트롤러에서는 서비스로 식별자만 딱딱 넘긴다
        //1. 컨트롤러에서 entity를 알지 않아도 된다.
        //2. 서비스쪽(Tx)에서 엔티티를 찾는 것부터 처리하는게 자연스러움.
        //서비스 바깥에서 객체를 넘겨버리면, 영속성 컨텍스트에서 더티체킹도 어려움
        //3. 컨트롤러 코드가 지저분해지지 않는다.

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping
    public String orderList(@ModelAttribute OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "/order/orderList";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId){
        log.info("\n\n orderId={}\n\n", orderId);
        orderService.cancelOrder(orderId);
        //성공적으로 취소가 되면(delivery status가 배송완이 아닌경우)
        return "redirect:/orders";

        //뷰에서 주문상태가 취소인 것들은 cancel버튼 노출이 안된다.
    }

}
