package com.walt.dao;

import com.walt.entity.Driver;
import com.walt.entity.Delivery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {


    List<Delivery> findAllByDriver(Driver driver);
}


