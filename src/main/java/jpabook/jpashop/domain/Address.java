package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
public class Address {
    private String city;
    private String street;
    private String zipcode;
    protected Address() {
    }   

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
