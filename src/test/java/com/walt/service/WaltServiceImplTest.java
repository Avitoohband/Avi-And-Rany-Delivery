package com.walt.service;

import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.entity.City;
import com.walt.entity.Customer;
import com.walt.entity.Delivery;
import com.walt.entity.Driver;
import com.walt.entity.DriverDistance;
import com.walt.entity.Restaurant;
import com.walt.exceptions.NoDriverFoundException;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WaltServiceImplTest {

    @Autowired
    private WaltService waltService;
    @MockBean
    private DriverRepository driverRepository;
    @MockBean
    private DeliveryRepository deliveryRepository;
    @Captor
    private ArgumentCaptor<Delivery> argumentCaptor;

    @Test
    public void whenNoDriverFoundInCity_shouldThrow() {
        when(driverRepository.findAllDriversByCity(any()))
                .thenReturn(Collections.emptyList());

        assertThrows(NoDriverFoundException.class,
                     () -> waltService.createOrderAndAssignDriver(new Customer(),
                                                                  new Restaurant(),
                                                                  LocalDateTime.now()));
    }

    @Test
    public void whenAvailableDriverForDelivery_shouldCreateAndSaveDelivery() {
        City city = new City("Tel-Aviv");
        Driver driver = new Driver("Moshe", city);
        Customer customer = new Customer("David", city, "Borochov");
        Restaurant restaurant = new Restaurant("Japan-Japan", city, "Hahagana 21");
        LocalDateTime deliveryTimeNow = LocalDateTime.now();
        LocalDateTime deliveryTimeTomorrow = deliveryTimeNow.plusDays(1);

        Delivery deliveryNow = Delivery.builder()
                                       .driver(driver)
                                       .restaurant(restaurant)
                                       .customer(customer)
                                       .deliveryTime(deliveryTimeNow)
                                       .build();

        Delivery expectedDelivery = Delivery.builder()
                                            .driver(driver)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .build();

        when(driverRepository.findAllDriversByCity(any())).thenReturn(
                Lists.newArrayList(driver)
        );

        when(deliveryRepository.findAllByDriver(any())).thenReturn(
                Lists.newArrayList(deliveryNow)
        );

        waltService.createOrderAndAssignDriver(customer, restaurant, deliveryTimeTomorrow);

        verify(deliveryRepository).save(argumentCaptor.capture());

        Delivery actualDelivery = argumentCaptor.getValue();

        assertEquals(expectedDelivery, actualDelivery);
    }

    @Test
    public void whenAvailableDriverIsOccupiedAtAGivenTime_shouldThrow() {
        City city = new City("TelAviv");
        Driver driver = new Driver("Moshe", city);
        Customer customer = new Customer("David", city, "Borochov");
        Restaurant restaurant = new Restaurant("Japan-Japan", city, "Hahagana 21");
        LocalDateTime deliveryTimeNow = LocalDateTime.now();


        Delivery deliveryNow = Delivery.builder()
                                       .driver(driver)
                                       .restaurant(restaurant)
                                       .customer(customer)
                                       .deliveryTime(deliveryTimeNow)
                                       .build();

        when(driverRepository.findAllDriversByCity(any())).thenReturn(
                Lists.newArrayList(driver)
        );

        when(deliveryRepository.findAllByDriver(any())).thenReturn(
                Lists.newArrayList(deliveryNow)
        );

        assertThrows(NoDriverFoundException.class,
                     () -> waltService.createOrderAndAssignDriver(customer,
                                                                  restaurant,
                                                                  deliveryTimeNow));
    }

    @Test
    public void whenPluralAvailableDrivers_shouldReturnLeastBusy() {
        City city = new City("TelAviv");
        Driver busyDriver = new Driver("Moshe", city);
        Driver leaseBusyDriver = new Driver("David", city);
        Customer customer = new Customer("David", city, "Borochov");
        Restaurant restaurant = new Restaurant("Japan-Japan", city, "Hahagana 21");
        LocalDateTime deliveryTimeNow = LocalDateTime.now();

        Delivery driver1Delivery1 = Delivery.builder()
                                            .driver(busyDriver)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .build();

        Delivery driver1Delivery2 = Delivery.builder()
                                            .driver(busyDriver)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(tomorrow())
                                            .build();

        Delivery driver2Delivery1 = Delivery.builder()
                                            .driver(leaseBusyDriver)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .build();


        when(driverRepository.findAllDriversByCity(any())).thenReturn(
                Lists.newArrayList(busyDriver, leaseBusyDriver)
        );

        when(deliveryRepository.findAllByDriver(busyDriver)).thenReturn(
                Lists.newArrayList(driver1Delivery1, driver1Delivery2)
        );

        when(deliveryRepository.findAllByDriver(leaseBusyDriver)).thenReturn(
                Lists.newArrayList(driver2Delivery1)
        );

        waltService.createOrderAndAssignDriver(customer, restaurant, tomorrow());

        verify(deliveryRepository).save(argumentCaptor.capture());

        Delivery savedDelivery = argumentCaptor.getValue();

        Driver assignedDriver = savedDelivery.getDriver();

        assertEquals(assignedDriver, leaseBusyDriver);
    }

    @Test
    public void whenRankDriverIsCalled_shouldReturnDescDriverDistanceSortedList() {
        City city = new City("Jerusalem");
        Customer customer = new Customer("Daniel", city, "Hertsel 53");
        Driver driver1 = new Driver("Eli", city);
        Driver driver2 = new Driver("Dafna", city);
        Driver driver3 = new Driver("David", city);


        Restaurant restaurant = new Restaurant("Tamara", city, "Agudat Hapoel 2");
        LocalDateTime deliveryTimeNow = LocalDateTime.now();
        LocalDateTime deliveryTimeTomorrow = tomorrow();

        Delivery driver1Delivery1 = Delivery.builder()
                                            .driver(driver1)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .build();

        Delivery driver1Delivery2 = Delivery.builder()
                                            .driver(driver1)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .build();
        Delivery driver2Delivery1 = Delivery.builder()
                                            .driver(driver2)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .build();

        Delivery driver2Delivery2 = Delivery.builder()
                                            .driver(driver2)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .build();

        Delivery driver3Delivery1 = Delivery.builder()
                                            .driver(driver3)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .build();

        Delivery driver3Delivery2 = Delivery.builder()
                                            .driver(driver3)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .build();

        driver1Delivery1.setDistance(10);
        driver1Delivery2.setDistance(10);

        driver2Delivery1.setDistance(5);
        driver2Delivery2.setDistance(5);

        driver3Delivery1.setDistance(2);
        driver3Delivery2.setDistance(2);

        ArrayList<Driver> allDrivers = Lists.newArrayList(driver1, driver2, driver3);

        when(driverRepository.findAll()).thenReturn(allDrivers);

        when(deliveryRepository.findAllByDriver(driver1)).thenReturn(
                Lists.newArrayList(driver1Delivery1, driver1Delivery2)
        );

        when(deliveryRepository.findAllByDriver(driver2)).thenReturn(
                Lists.newArrayList(driver2Delivery1, driver2Delivery2)
        );

        when(deliveryRepository.findAllByDriver(driver3)).thenReturn(
                Lists.newArrayList(driver3Delivery1, driver3Delivery2)
        );

        List<DriverDistance> driverRankReport = waltService.getDriverRankReport();

        List<Double> resultDistances = driverRankReport.stream()
                                                       .map(DriverDistance::getTotalDistance)
                                                       .collect(Collectors.toList());

        ArrayList<Double> copyResultDistances = new ArrayList<>(resultDistances);
        copyResultDistances.sort((o1, o2) -> Double.compare(o2, o1));

        assertEquals(copyResultDistances, resultDistances, "List is not sorted!");
    }

    @Test
    public void whenRankDriverFromCityIsCalled_shouldReturnDescDriverDistanceSortedList() {
        City city = new City("Jerusalem");
        Customer customer = new Customer("Daniel", city, "Hertsel 53");
        Driver driver1 = new Driver("Eli", city);
        Driver driver2 = new Driver("Dafna", city);
        Driver driver3 = new Driver("David", city);

        Restaurant restaurant = new Restaurant("Tamara", city, "Agudat Hapoel 2");
        LocalDateTime deliveryTimeNow = LocalDateTime.now();
        LocalDateTime deliveryTimeTomorrow = tomorrow();

        Delivery driver1Delivery1 = Delivery.builder()
                                            .driver(driver1)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .distance(10)
                                            .build();

        Delivery driver1Delivery2 = Delivery.builder()
                                            .driver(driver1)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .distance(10)
                                            .build();

        Delivery driver2Delivery1 = Delivery.builder()
                                            .driver(driver2)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .distance(5)
                                            .build();

        Delivery driver2Delivery2 = Delivery.builder()
                                            .driver(driver2)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .distance(5)
                                            .build();

        Delivery driver3Delivery1 = Delivery.builder()
                                            .driver(driver3)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeNow)
                                            .distance(2)
                                            .build();

        Delivery driver3Delivery2 = Delivery.builder()
                                            .driver(driver3)
                                            .restaurant(restaurant)
                                            .customer(customer)
                                            .deliveryTime(deliveryTimeTomorrow)
                                            .distance(2)
                                            .build();

        ArrayList<Driver> allDrivers = Lists.newArrayList(driver1, driver2, driver3);

        when(driverRepository.findAllDriversByCity(eq(city))).thenReturn(allDrivers);

        when(deliveryRepository.findAllByDriver(driver1)).thenReturn(
                Lists.newArrayList(driver1Delivery1, driver1Delivery2)
        );

        when(deliveryRepository.findAllByDriver(driver2)).thenReturn(
                Lists.newArrayList(driver2Delivery1, driver2Delivery2)
        );

        when(deliveryRepository.findAllByDriver(driver3)).thenReturn(
                Lists.newArrayList(driver3Delivery1, driver3Delivery2)
        );

        List<DriverDistance> driverRankReport = waltService.getDriverRankReport();

        List<Double> resultDistances = driverRankReport.stream()
                                                       .map(DriverDistance::getTotalDistance)
                                                       .collect(Collectors.toList());

        ArrayList<Double> copyResultDistances = new ArrayList<>(resultDistances);
        copyResultDistances.sort((o1, o2) -> Double.compare(o2, o1));

        assertEquals(copyResultDistances, resultDistances, "List is not sorted!");
    }

    private static LocalDateTime tomorrow() {
        return LocalDateTime.now().plusDays(1);
    }
}