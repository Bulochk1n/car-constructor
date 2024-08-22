package com.car_constructor.car_constructor.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @Getter
    private String customerName;
    @Setter
    @Getter
    private String customerAddress;
    @Setter
    @Getter
    private String customerPhone;
    @Setter
    @Getter
    private String customerEmail;
    @Setter
    @Getter
    private Long carId;
    @Setter
    @Getter
    private String carName;
    @Setter
    @Getter
    private int price;
    @Setter
    @Getter
    @Column(name = "specifications", length = 2000)
    private String specifications;
    @Setter
    @Getter
    private String username;

//
//    public Order(String customerName, String customerAddress, String customerPhone, String customerEmail) {
//        this.customerName = customerName;
//        this.customerAddress = customerAddress;
//        this.customerPhone = customerPhone;
//        this.customerEmail = customerEmail;
//    }

    public Order() {

    }
}
