package com.walt.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driver;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToOne
    private Customer customer;

    private Date deliveryTime;
    private double distance;

    @Builder
    public Delivery(Driver driver,
                    Restaurant restaurant,
                    Customer customer,
                    Date deliveryTime, double distance) {
        this.driver = driver;
        this.restaurant = restaurant;
        this.customer = customer;
        this.deliveryTime = deliveryTime;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delivery)) return false;

        Delivery delivery = (Delivery) o;

        if (!driver.equals(delivery.driver)) return false;
        if (!restaurant.equals(delivery.restaurant)) return false;
        if (!customer.equals(delivery.customer)) return false;
        return deliveryTime.equals(delivery.deliveryTime);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = driver.hashCode();
        result = 31 * result + restaurant.hashCode();
        result = 31 * result + customer.hashCode();
        result = 31 * result + deliveryTime.hashCode();
        temp = Double.doubleToLongBits(distance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
