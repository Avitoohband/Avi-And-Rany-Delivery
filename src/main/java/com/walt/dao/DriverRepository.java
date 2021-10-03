package com.walt.dao;

import com.walt.entity.City;
import com.walt.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {
    List<Driver> findAllDriversByCity(City city);
    Driver findByName(String name);


}
