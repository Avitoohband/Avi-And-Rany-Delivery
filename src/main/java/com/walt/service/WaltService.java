package com.walt.service;

import com.walt.entity.City;
import com.walt.entity.Customer;
import com.walt.entity.Delivery;
import com.walt.entity.Driver;
import com.walt.entity.DriverDistance;
import com.walt.entity.Restaurant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaltService {

    Delivery createOrderAndAssignDriver(Customer customer,
                                        Restaurant restaurant,
                                        LocalDateTime deliveryTime);

    List<DriverDistance> getDriverRankReport();

    List<DriverDistance> getDriverRankReportByCity(City city);

    List<Driver> getAllDrivers();

    List<Delivery> getAllDeliveries();

    Optional<Driver> getDriverByName(String name);

    Optional<Driver> locateDriverForDeliveryAt(String cityName, LocalDateTime deliveryTime);
}

