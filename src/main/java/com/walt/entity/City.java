package com.walt.entity;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class City extends NamedEntity {

    @Builder
    public City(String name) {
        this.name = name;
    }
}
