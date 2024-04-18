package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import lombok.*;

@Getter
@Setter
@ToString
public class MemberForm {
    @NotEmpty(message = "회원 이름은 공백일 수 없습니다.")
    private String name;
    private String city;
    private String street;
    private String zipcode;
    private Long id;



    //내가 만든, MemberForm <-> Entity 변환 메서드
    public static MemberForm toMemberForm(Member member){
        Address address = member.getAddress();

        MemberForm memberForm = new MemberForm();
        memberForm.setName(member.getName());
        memberForm.setCity(address.getCity());
        memberForm.setStreet(address.getStreet());
        memberForm.setZipcode(address.getZipcode());
        memberForm.setId(member.getId());

        return memberForm;
    }

    public Member toMember(){
        Member member = new Member();
        Address address = new Address(this.city, this.street, this.zipcode);
        member.setName(this.getName());
        member.setAddress(address);

        return member;
    }
}
