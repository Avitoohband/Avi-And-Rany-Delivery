package com.walt.rest.controller;

import com.walt.entity.Delivery;
import com.walt.entity.Driver;
import com.walt.service.WaltService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WaltController {

    private final WaltService waltService;

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.of(Optional.of(waltService.getAllDrivers()));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.of(Optional.of(waltService.getAllDeliveries()));
    }

    @GetMapping("/drivers/{name}")
    public ResponseEntity<Driver> getDriverByName(@PathVariable String name) {
        return ResponseEntity.of(waltService.getDriverByName(name));
    }

    @GetMapping("/drivers/locate/{cityName}/{deliveryTime}")
    public ResponseEntity<Driver> locateDriver(@PathVariable String cityName,
                                               @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deliveryTime) {
        return ResponseEntity.of(waltService.locateDriverForDeliveryAt(cityName, deliveryTime));
    }
}
