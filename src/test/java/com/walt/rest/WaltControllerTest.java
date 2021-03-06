package com.walt.rest;

import com.walt.entity.City;
import com.walt.entity.Customer;
import com.walt.entity.Delivery;
import com.walt.entity.Driver;
import com.walt.entity.Restaurant;
import com.walt.rest.controller.WaltController;
import com.walt.service.WaltService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.SneakyThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaltController.class)
public class WaltControllerTest {
    public static final String ENDPOINT_DRIVERS = "/api/drivers";
    public static final String ENDPOINT_LOCATE_DRIVER = "/api/drivers/locate";
    public static final String ENDPOINT_DELIVERIES = "/api/deliveries";
    public static final int LENGTH_NAME_DRIVER = 8;
    public static final int LENGTH_NAME_CITY = 8;
    public static final int LENGTH_CUSTOMER_NAME = 3;
    public static final int LENGTH_ADDRESS = 10;
    public static final int LENGTH_NAME_RESTAURANT = 5;
    private static final int MIN_DELIVERY_DISTANCE = 0;
    private static final int MAX_DELIVERY_DISTANCE = 20;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WaltService waltService;

    @BeforeEach
    public void foo() {

    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestAllDrivers_andHasDrivers_responseIsOK_JSON_withDrivers() {
        List<Driver> drivers = createRandomDrivers(3);

        when(waltService.getAllDrivers()).thenReturn(drivers);

        mockMvc.perform(get(ENDPOINT_DRIVERS))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(drivers.size()));
    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestAllDrivers_andHasNoDrivers_responseIsOK_withoutDrivers() {
        when(waltService.getAllDrivers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(ENDPOINT_DRIVERS))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestAllDeliveries_andHasDeliveries_responseIsOK_JSON_withDeliveries() {
        List<Delivery> deliveries = createRandomDeliveries(3);

        when(waltService.getAllDeliveries()).thenReturn(deliveries);

        mockMvc.perform(get(ENDPOINT_DELIVERIES))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(deliveries.size()));
    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestAllDeliveries_andHasNoDeliveries_responseIsOK_withoutDeliveries() {

        when(waltService.getAllDeliveries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(ENDPOINT_DELIVERIES))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestGetDriverByName_andHasMatchingDriver_responseOK_withDriver() {
        Optional<Driver> optionalDriver = Optional.of(createRandomDriver());
        String driverName = optionalDriver.get().getName();

        when(waltService.getDriverByName(driverName)).thenReturn(optionalDriver);

        mockMvc.perform(get(ENDPOINT_DRIVERS + "/{name}", driverName))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(driverName));
    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestGetDriverByName_andHasNoMatchingDriver_responseNotFound() {
        Optional<Driver> optionalDriver = Optional.of(createRandomDriver());
        String driverName = optionalDriver.get().getName();

        when(waltService.getDriverByName(driverName)).thenReturn(Optional.empty());

        mockMvc.perform(get(ENDPOINT_DRIVERS + "/{name}", driverName))
               .andExpect(status().isNotFound());
    }

    @WithMockUser
    @SneakyThrows
    @Test
    public void whenRequestFindMatchDriver_andDriverFound_responseOk_withDriver() {
        Optional<Driver> optionalRandomDriver = Optional.of(createRandomDriver());

        String driverName = optionalRandomDriver.get().getName();

        when(waltService.locateDriverForDeliveryAt(any(String.class),
                                                   any(LocalDateTime.class))).thenReturn(
                optionalRandomDriver);

        LocalDateTime randomDeliveryTime = createRandomDeliveryTime();

        mockMvc.perform(get(ENDPOINT_LOCATE_DRIVER + "/{cityName}/{deliveryTime}",
                            createRandomCity().getName(),
                            randomDeliveryTime))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(driverName));
    }


    private List<Delivery> createRandomDeliveries(int totalDeliveries) {
        return IntStream.range(0, totalDeliveries)
                        .mapToObj(__ -> createRandomDelivery())
                        .collect(Collectors.toList());
    }

    private Delivery createRandomDelivery() {
        return Delivery.builder()
                       .driver(createRandomDriver())
                       .customer(createRandomCustomer())
                       .distance(createRandomDistance(MIN_DELIVERY_DISTANCE, MAX_DELIVERY_DISTANCE))
                       .restaurant(createRandomRestaurant())
                       .deliveryTime(createRandomDeliveryTime())
                       .build();
    }

    private LocalDateTime createRandomDeliveryTime() {
        long minSeconds = LocalDateTime.of(1970, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long maxSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long randomSeconds = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds);
        return LocalDateTime.ofEpochSecond(randomSeconds, 0, ZoneOffset.UTC);
    }

    private Restaurant createRandomRestaurant() {
        return Restaurant.builder()
                         .address(randomName(LENGTH_ADDRESS))
                         .name(randomName(LENGTH_NAME_RESTAURANT))
                         .city(createRandomCity())
                         .build();
    }

    private Customer createRandomCustomer() {
        return Customer.builder()
                       .name(randomName(LENGTH_CUSTOMER_NAME))
                       .city(createRandomCity())
                       .address(randomName(LENGTH_ADDRESS))
                       .build();
    }

    private List<Driver> createRandomDrivers(int totalDrivers) {
        return IntStream.range(0, totalDrivers)
                        .mapToObj(__ -> createRandomDriver())
                        .collect(Collectors.toList());
    }

    private Driver createRandomDriver() {
        return Driver.builder()
                     .name(randomName(LENGTH_NAME_DRIVER))
                     .city(createRandomCity())
                     .build();
    }

    private City createRandomCity() {
        return City.builder().name(randomName(LENGTH_NAME_CITY)).build();
    }

    private String randomName(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    private double createRandomDistance(int begin, int end) {
        return ThreadLocalRandom.current()
                                .nextInt(begin, end + 1);
    }
}
