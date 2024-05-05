package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;

@Data
public class OrderItemQueryDto implements Comparable<OrderItemQueryDto> {
    //아이템 이름 사전순 정렬로 내가 임의로 설정

    @JsonIgnore
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    @Override
    public int compareTo(OrderItemQueryDto o) {
        return o.getItemName().compareTo(this.getItemName());
    }
}
