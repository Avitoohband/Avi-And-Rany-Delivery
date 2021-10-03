package com.walt.rest;

import com.walt.entity.City;
import com.walt.entity.Driver;
import com.walt.rest.controller.DriverController;
import com.walt.service.WaltService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import lombok.SneakyThrows;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
public class DriverControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WaltService waltService;

    private List<Driver> drivers;

    @BeforeEach
    public void initEach() {
        drivers = List.of(Driver.builder()
                                .name("DriverName1")
                                .city(City.builder().name("Jerusalem").build())
                                .build(),
                          Driver.builder()
                                .name("DriverName2")
                                .city(City.builder().name("TelAviv").build())
                                .build(),
                          Driver.builder()
                                .name("DriverName3")
                                .city(City.builder().name("Jerusalem").build())
                                .build());
    }

    @SneakyThrows
    @Test
    public void whenRequestAllDrivers_andHasDrivers_responseIsOK_JSON_withDrivers() {
        when(waltService.getAllDrivers()).thenReturn(drivers);

        mockMvc.perform(get("/api/drivers"))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(drivers.size()));
    }

    @SneakyThrows
    @Test
    public void whenRequestAllDrivers_andHasNoDrivers_responseIsOK_withoutDrivers() {
        when(waltService.getAllDrivers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/drivers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

}
