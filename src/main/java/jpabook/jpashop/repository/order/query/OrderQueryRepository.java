package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){ //api V4용 (루프를 돌면서 가져옴 -> N+1문제)
        //이 메서드는, OrderQueryDto 각각에 대하여 OrderItems를(+Item) 가져오는 쿼리를 날리고 set해줌
        //즉, 루프를 돈다
        //N+1 쿼리
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() { //api V5용 (한방에 다 가져온다.)
        //이 메서드는, OrderQueryDto 리스트와 OrderQuertItemDto 리스트를 각각 가져온 후,
        //OrderQueryItemDto를 orderId를 key로 map을 만든 뒤
        //OrderQueryDto에 각각 맞는 OrderQueryItemDto를 넣어준다.(orderId로)
        //즉, OrderQueryItemDto를 하나씩 가져오는게 아니라 List로 한번에 가져온다는게 v4와 다름
        //쿼리 총 2번(루트 쿼리1 + IN 쿼리1)

        List<OrderQueryDto> results = findOrders();

        //orderIds만 가져오기
        List<Long> orderIds = extractOrderIds(results);

        //OrderItemQueryDto를 한번에 가져와서 Map으로 변환
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        //OrderQueryDto에 orderId가 일치하는 OrderItemQueryDto를 set해주기
        results.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return results;
    }

    private List<Long> extractOrderIds(List<OrderQueryDto> results) {
        return results.stream().map(OrderQueryDto::getOrderId).collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {

        //IN쿼리를 사용해서 한번에 OrderItemDto를 List로 가져오고
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "SELECT new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " FROM OrderItem oi" +
                                " JOIN oi.item i " +
                                " WHERE oi.order.id IN :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //List를 Map으로 변환해줌(OrderQueryDto와 orderId로 매칭시켜주기 위해)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        return orderItemMap;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "SELECT new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " FROM OrderItem oi" +
                        " JOIN oi.item i " +
                        " WHERE oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        String query = "SELECT new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " FROM Order o " +
                " join o.member m " +
                " join o.delivery d";

        return em.createQuery(query, OrderQueryDto.class).getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        //쿼리 한 번으로 다 가져오기
        return em.createQuery(
                "SELECT " +
                            " new jpabook.jpashop." +
                                "repository.order." +
                                "query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " FROM Order o" +
                        " JOIN o.member m " +
                        " JOIN o.delivery d " +
                        " JOIN o.orderItems oi " +
                        " JOIN oi.item i", OrderFlatDto.class)
                .getResultList();

    }
}
