package com.fujitsupraktika.markus.proovitoo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class WeatherToDatabase {

    /**
     *  Gets Weather Data from the Estonian Environment Agency
     *  and writes it into the database.
     */
    public static void main(String[] args) {
        try {
            URI uri = new URI("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
            URL url = uri.toURL();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url.openStream());
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("station");

            // JDBC driver name and database URL
            String JDBC_DRIVER = "org.h2.Driver";
            String DB_URL = ""; // Declare DB_URL as and empty String
            /*
            Check if the program was run with the shell script runWeatherToDatabase.sh
            If it was then get the database address from script input ip
            If this is not the case, then guess that the program was run from the IDE and try to find the database on executing device
            If that does not work either then the whole program stop and throw and exception
             */
            try {
                String ip = args[0];
                DB_URL = "jdbc:h2:tcp://" + ip + ":9092/~/weatherdata";
            } catch (Exception e) {
                DB_URL = "jdbc:h2:tcp://" + LocalInfo.getIp() + ":9092/~/weatherdata";
            }

            //  Database credentials
            String USER = "sa";
            String PASS = "";

            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create new table if necessary
            Statement createTableStatement = conn.createStatement();
            createTableStatement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS WEATHER (ID INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    STATION_NAME VARCHAR(255),\n" +
                    "    WMO_CODE INTEGER,\n" +
                    "    AIR_TEMPERATURE DOUBLE,\n" +
                    "    WIND_SPEED DOUBLE,\n" +
                    "    PHENOMENON VARCHAR(255),\n" +
                    "    TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            createTableStatement.close();

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String stationName = element.getElementsByTagName("name").item(0).getTextContent();

                    if (stationName.equals("Tallinn-Harku") || stationName.equals("Tartu-Tõravere") || stationName.equals("Pärnu")) {
                        System.out.println("Station Name : " + stationName);
                        System.out.println("WMO Code : " + element.getElementsByTagName("wmocode").item(0).getTextContent());
                        System.out.println("Air Temperature : " + element.getElementsByTagName("airtemperature").item(0).getTextContent());
                        System.out.println("Wind Speed : " + element.getElementsByTagName("windspeed").item(0).getTextContent());
                        System.out.println("Weather Phenomenon : " + element.getElementsByTagName("phenomenon").item(0).getTextContent());
                        System.out.println();
                        String wmocode = element.getElementsByTagName("wmocode").item(0).getTextContent();
                        String airtemperature = element.getElementsByTagName("airtemperature").item(0).getTextContent();
                        String windspeed = element.getElementsByTagName("windspeed").item(0).getTextContent();
                        String phenomenon = element.getElementsByTagName("phenomenon").item(0).getTextContent();

                        String sql = "INSERT INTO WEATHER (STATION_NAME, WMO_CODE, AIR_TEMPERATURE, WIND_SPEED, PHENOMENON) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1, stationName);
                        preparedStatement.setString(2, wmocode);
                        preparedStatement.setString(3, airtemperature);
                        preparedStatement.setString(4, windspeed);
                        preparedStatement.setString(5, phenomenon);
                        preparedStatement.executeUpdate();
                    }
                }
            }
            System.out.println("Timestamp of the Observations : " + doc.getElementsByTagName("observations").item(0).getAttributes().getNamedItem("timestamp").getNodeValue());
            conn.close();
        } catch (MalformedURLException e) {
            System.out.println("The URL is not formed correctly.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("An error occurred while trying to open a connection to the URL.");
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            throw new RuntimeException(e);
        }
    }
}
