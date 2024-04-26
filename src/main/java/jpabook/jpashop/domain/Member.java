package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
//    @NotEmpty
    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
//    @JsonIgnore
    //회원과 관련된 조회API가 하나가 아닐텐데 엔티티에 @JsonIgnore같은 것을 계속 추가할 수 없다.
    private List<Order> orders = new ArrayList<>();

//    public void changeOrder(Order order){
//        this.orders.add(order);
//        order.setMember(this);
//    }

}
