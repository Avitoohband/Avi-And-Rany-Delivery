package com.walt.dao;

import com.walt.entity.Delivery;
import com.walt.entity.Driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {


    List<Delivery> findAllByDriver(Driver driver);
}


