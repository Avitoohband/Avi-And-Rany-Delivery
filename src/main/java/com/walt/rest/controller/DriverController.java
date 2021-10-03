package com.walt.rest.controller;

import com.walt.entity.Driver;
import com.walt.service.WaltService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final WaltService waltService;

    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> allDrivers = waltService.getAllDrivers();
        return ResponseEntity.of(Optional.of(allDrivers));
    }
}
