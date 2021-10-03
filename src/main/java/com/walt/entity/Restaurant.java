package com.walt.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Restaurant extends NamedEntity {

    @ManyToOne
    private City city;
    private String address;

    @Builder
    public Restaurant(String name, City city, String address) {
        this.name = name;
        this.city = city;
        this.address = address;
    }
}
