package com.fujitsupraktika.markus.proovitoo;

import java.sql.*;
import java.util.regex.Pattern;

public class WeatherData {

    private String stationName, phenomenon;
    private int wmocode;
    private double temperature, windspeed;
    private Timestamp timestamp;

    public String getStationName() {
        return stationName;
    }

    public int getWmocode() {
        return wmocode;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     *   Gets the latest weatherdata for specified city from the database.
     *   Sets the values to read data.
     *   Also outputs to console the read weather data.
     *   @param city takes a city as input
     */
    public void readWeatherData(String city) {
        try {
            // JDBC driver name and database URL
            String JDBC_DRIVER = "org.h2.Driver";
            String DB_URL = "jdbc:h2:tcp://192.168.1.183:9092/~/weatherdata";

            //  Database credentials
            String USER = "sa";
            String PASS = "";

            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute a query
            String sql = "SELECT * FROM WEATHER ORDER BY TIMESTAMP DESC LIMIT 3";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                stationName = rs.getString("STATION_NAME");
                if (!Pattern.compile(Pattern.quote(city), Pattern.CASE_INSENSITIVE).matcher(stationName).find()) {
                    continue;
                }
                wmocode = rs.getInt("WMO_CODE");
                temperature = rs.getDouble("AIR_TEMPERATURE");
                windspeed = rs.getDouble("WIND_SPEED");
                phenomenon = rs.getString("PHENOMENON");
                timestamp = rs.getTimestamp("TIMESTAMP");

                // Display values
                System.out.println("Station Name: " + stationName);
                System.out.println("WMO Code: " + wmocode);
                System.out.println("Air Temperature: " + temperature);
                System.out.println("Wind Speed: " + windspeed);
                System.out.println("Weather Phenomenon: " + phenomenon);
                System.out.println("Timestamp: " + timestamp);
                System.out.println();
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
    }

}
