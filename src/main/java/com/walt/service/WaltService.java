package com.walt.service;

import com.walt.entity.City;
import com.walt.entity.Customer;
import com.walt.entity.Delivery;
import com.walt.entity.Driver;
import com.walt.entity.DriverDistance;
import com.walt.entity.Restaurant;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WaltService {

    Delivery createOrderAndAssignDriver(Customer customer,
                                        Restaurant restaurant,
                                        Date deliveryTime);

    List<DriverDistance> getDriverRankReport();

    List<DriverDistance> getDriverRankReportByCity(City city);

    List<Driver> getAllDrivers();

    List<Delivery> getAllDeliveries();

    Optional<Driver> getDriverByName(String name);
}

