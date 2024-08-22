package com.car_constructor.car_constructor.models;


import jakarta.persistence.*;

import java.awt.*;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "specifications", length = 2000)
    private String specifications;
    private String name;
    private int price;
    private String fullImagePath;
    private String shortImagePath;

    public Car(String name, String specifications, int price) {
        this.name = name;
        this.specifications = specifications;
        this.price = price;
    }

    public Car() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFullImagePath() {
        return fullImagePath;
    }

    public void setFullImagePath(String fullImagePath) {
        this.fullImagePath = fullImagePath;
    }

    public String getShortImagePath() {
        return shortImagePath;
    }

    public void setShortImagePath(String shortImagePath) {
        this.shortImagePath = shortImagePath;
    }
}
