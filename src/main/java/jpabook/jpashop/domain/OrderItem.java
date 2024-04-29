package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpabook.jpashop.domain.items.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice; //아이템의 가격에 쿠폰이나 할인 정책이 반영된 금액
    private int count; //주문수량


    //==생성 메서드==//
    // 생성자와 차이점은??
    // new생성자를 쓰기보단 명확한 생성메서드를 만들어 쓰는 편이 좋다.
    // 유지보수 관점 등등에서 좋음.
    // 생성자를 private으로 바꾸면 좋겠지만 JPA spec상 protected까지만 좁힐 수 있음
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        //생성될 때 아이템에서 수량을 깎아주어야 한다.
        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==/
    public void cancel() { //재고수량을 원복한다.
        getItem().addStock(count);
    }

    //==조회 로직==//

    /**
     *
     * 주문상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
        //proxy객체를 생성하는 경우때문에 getter 사용을 권장
        //JPA엔티티에서 equals, hashcode를 구현할 때는 getter를 내부에서 사용해야함
    }
}
