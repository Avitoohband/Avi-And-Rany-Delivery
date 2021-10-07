package com.walt.service;

import com.walt.dao.CityRepository;
import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.entity.City;
import com.walt.entity.Customer;
import com.walt.entity.Delivery;
import com.walt.entity.Driver;
import com.walt.entity.DriverDistance;
import com.walt.entity.Restaurant;
import com.walt.exceptions.NoDriverFoundException;
import com.walt.exceptions.NoSuchCityException;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WaltServiceImpl implements WaltService {

    private static final int MIN_DELIVERY_DISTANCE = 0;
    private static final int MAX_DELIVERY_DISTANCE = 20;

    private final DriverRepository driverRepository;
    private final DeliveryRepository deliveryRepository;
    private final CityRepository cityRepository;

    @Override
    public Delivery createOrderAndAssignDriver(Customer customer,
                                               Restaurant restaurant,
                                               LocalDateTime deliveryTime) {
        Driver matchedDriver = findMatchDriverOrElseThrow(deliveryTime, restaurant.getCity());

        return saveDelivery(customer, restaurant, deliveryTime, matchedDriver);
    }

    @Override
    public List<DriverDistance> getDriverRankReport() {
        return toDescOrderReportList(driverRepository.findAll());
    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {
        return toDescOrderReportList(driverRepository.findAllDriversByCity(city));
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @Override
    public Optional<Driver> getDriverByName(String name) {
        return driverRepository.findByName(name);
    }

    @Override
    public Optional<Driver> locateDriverForDeliveryAt(String cityName, LocalDateTime deliveryTime) {
        City city = cityRepository.findByName(cityName)
                                  .orElseThrow(NoSuchCityException::new);
        return driverRepository.findAllDriversByCity(city)
                               .stream()
                               .filter(availableForDeliveryAt(deliveryTime))
                               .min(getComparatorForLeastBusy());
    }

    private static int descendingComparator(DriverDistance o1, DriverDistance o2) {
        return Double.compare(o2.getTotalDistance(), o1.getTotalDistance());
    }

    @Deprecated
    private Driver findMatchDriverOrElseThrow(LocalDateTime deliveryTime, City city) {
        return driverRepository.findAllDriversByCity(city)
                               .stream()
                               .filter(availableForDeliveryAt(deliveryTime))
                               .min(getComparatorForLeastBusy())
                               .orElseThrow(NoDriverFoundException::new);
    }

    private Delivery saveDelivery(Customer customer,
                                  Restaurant restaurant,
                                  LocalDateTime deliveryTime,
                                  Driver driver) {

        return deliveryRepository.save(
                Delivery.builder()
                        .driver(driver)
                        .restaurant(restaurant)
                        .customer(customer)
                        .deliveryTime(deliveryTime)
                        .distance(getRandomDeliveryDistance())
                        .build()
        );
    }

    private Predicate<Driver> availableForDeliveryAt(LocalDateTime deliveryTime) {

        return driver -> deliveryRepository.findAllByDriver(driver)
                                           .stream()
                                           .noneMatch(deliveryAt(deliveryTime));
    }

    private Comparator<Driver> getComparatorForLeastBusy() {
        return Comparator.comparingInt(driver -> deliveryRepository.findAllByDriver(driver).size());
    }

    private double getRandomDeliveryDistance() {
        return ThreadLocalRandom.current()
                                .nextInt(MIN_DELIVERY_DISTANCE, MAX_DELIVERY_DISTANCE + 1);
    }

    private Predicate<Delivery> deliveryAt(LocalDateTime deliveryTime) {
        return delivery -> Objects.equals(delivery.getDeliveryTime(), deliveryTime);
    }

    private List<DriverDistance> toDescOrderReportList(List<Driver> drivers) {
        return drivers
                .stream()
                .map(this::toDriverDistance)
                .sorted(WaltServiceImpl::descendingComparator)
                .collect(Collectors.toList());
    }

    private DriverDistance toDriverDistance(Driver driver) {
        return new DriverDistance() {
            @Override
            public Driver getDriver() {
                return driver;
            }

            @Override
            public Double getTotalDistance() {
                return deliveryRepository.findAllByDriver(driver)
                                         .stream()
                                         .mapToDouble(Delivery::getDistance)
                                         .sum();
            }
        };
    }
}