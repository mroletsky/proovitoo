package com.fujitsupraktika.markus.proovitoo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/delivery")
public class ApplicationController {

    /**
     *  Calculate delivery fee for specified city and vehicle type.
     *  Valid cities: Tallinn, Tartu, Pärnu.
     *  Valid vehicles: Car, Scooter, Bike.
     *  Fee calculation takes into account the latest weather data.
     *  For extreme weather cases returns Usage of selected vehicle type is forbidden.
     *  Example request:
     *  GET /api/delivery/fee?city=Tallinn&vehicleType=Car
     *
     *  @param city location of delivery.
     *  @param vehicleType vehicle to deliver.
     *  @throws org.springframework.web.client.HttpServerErrorException.InternalServerError
     *          when an error occurs while calculating the delivery fee.
     *  @return deliveryFee
     *
     */
    @GetMapping("/fee")
    public ResponseEntity<?> getDeliveryFee(@RequestParam String city, @RequestParam String vehicleType) {
        try {
            // Ignore case of parameters
            city = city.toLowerCase();
            vehicleType = vehicleType.toLowerCase();

            // Validate the input parameters
            if (!Arrays.asList("tallinn", "tartu", "pärnu").contains(city)) {
                return new ResponseEntity<>("Invalid city (Valid cities are: Tallinn, Tartu, Pärnu)", HttpStatus.BAD_REQUEST);
            }
            if (!Arrays.asList("car", "scooter", "bike").contains(vehicleType)) {
                return new ResponseEntity<>("Invalid vehicle type (Valid vehicle types are: Car, Scooter, Bike)", HttpStatus.BAD_REQUEST);
            }

            // Create WeatherData object
            WeatherData data = new WeatherData();

            // Read weather data from database
            data.readWeatherData(city);

            // Create a new Delivery object
            Delivery delivery = new Delivery(city, vehicleType, data.getPhenomenon(), data.getWindspeed(), data.getTemperature());

            // Calculate the delivery fee
            double deliveryFee = delivery.calculateFee();

            // Response case if vehicle forbidden
            if (deliveryFee < 0.0) {
                return new ResponseEntity<>("Usage of selected vehicle type is forbidden", HttpStatus.BAD_REQUEST);
            }

            // Return response data
            return new ResponseEntity<>(deliveryFee, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while calculating the delivery fee", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
