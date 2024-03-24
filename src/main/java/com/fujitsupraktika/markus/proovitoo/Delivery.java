package com.fujitsupraktika.markus.proovitoo;

import java.util.regex.Pattern;

public class Delivery {

    private final String city;
    private final String vehicle;
    private final String phenomenon;
    private final double windspeed;
    private final double temperature;

    public Delivery(String city, String vehicle, String phenomenon, double windspeed, double temperature) {
        this.city = city;
        this.vehicle = vehicle;
        this.phenomenon = phenomenon;
        this.windspeed = windspeed;
        this.temperature = temperature;
    }

    private double RBF() { // Regional base fee
        switch (city.toLowerCase()) {
            case "tallinn":
                switch (vehicle.toLowerCase()) {
                    case "car":
                        return 4.0;
                    case "scooter":
                        return 3.5;
                    case "bike":
                        return 3.0;
                }
            case "tartu":
                switch (vehicle.toLowerCase()) {
                    case "car":
                        return 3.5;
                    case "scooter":
                        return 3.0;
                    case "bike":
                        return 2.5;
                }
            case "pärnu":
                switch (vehicle.toLowerCase()) {
                    case "car":
                        return 3.0;
                    case "scooter":
                        return 2.5;
                    case "bike":
                        return 2.0;
                }
        }
        return 0.0;
    }
    private double ATEF() { // Air temperature extra fee
        if (vehicle.equalsIgnoreCase("scooter") || vehicle.equalsIgnoreCase("bike")) {
            if (temperature < -10.0)
                return 1.0;
            if (temperature <= 0.0)
                return 0.5;
        }
        return 0.0;
    }
    private double WSEF() { // Wind speed extra fee
        if (vehicle.equalsIgnoreCase("bike")) {
            if (windspeed > 20.0) {
                return -1.0;
            }
            if (windspeed >= 10.0) {
                return 0.5;
            }
        }
        return 0.0;
    }
    private double WPEF() { // Weather phenomenon extra fee
        if (vehicle.equalsIgnoreCase("scooter") || vehicle.equalsIgnoreCase("bike")) {
            if (Pattern.compile(Pattern.quote("shower"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find() ||
                    Pattern.compile(Pattern.quote("rain"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find()) {
                return 0.5;
            }
            if (Pattern.compile(Pattern.quote("snow"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find() ||
                    Pattern.compile(Pattern.quote("sleet"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find()) {
                return 1.0;
            }
            if (Pattern.compile(Pattern.quote("glaze"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find() ||
                    Pattern.compile(Pattern.quote("hail"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find() ||
                    Pattern.compile(Pattern.quote("thunder"), Pattern.CASE_INSENSITIVE).matcher(phenomenon).find()) {
                return -1.0;
            }
        }
        return 0.0;
    }

    /**
     * Function for calculating the total fee of the delivery
     * based on base fee for selected city and vehicle
     * and extra fees based on the current weather in the city.
     * In cases of a high windspeed or extreme weather phenomenon
     * some vehicles are forbidden to use:
     * (vehicle: bike, windspeed > 20 or extreme weather) or (vehicle: scooter, extreme weather)
     * @return -1.0 when selection forbidden due to weather, otherwise sum of base fee and extra fees
     */
    public double calculateFee() {
        if (WPEF() < 0 || WSEF() < 0) { // Vehicle forbidden cases
            System.out.println("Usage of selected vehicle is forbidden");
            return -1.0;
        }
        return RBF() + ATEF() + WSEF() + WPEF(); // Calculating total fee
    }
}
