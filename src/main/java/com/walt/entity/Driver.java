package com.walt.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Driver extends NamedEntity {

    @ManyToOne
    private City city;

    @Builder
    public Driver(String name, City city) {
        this.name = name;
        this.city = city;
    }
}
