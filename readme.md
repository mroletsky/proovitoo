### LocalInfo ###
Has static method `getIp()` that returns the local IPv4.
Used for connecting to local H2 Database server.


### Weather to database (CronJob) ###
Gets Weather Data from the Estonian Environment Agency and writes it into the database.
Meant to be run as a CronJob with the shell script runWeatherToDatabase.sh.
The Script uses mvn exec:java to run the program.

#### Crontab configuration:
Add line `15 * * * * /path/to/shell/script/runWeatherToDatabase.sh` to crontab.
This runs the script every 15th minute of every hour.
Also configure the script `runWeatherToDatabase.sh` to correctly change directory to project
`cd /path/to/project` (configure line 2 in script).


### Weather data ###
Stores weather data for calculating the delivery fee.
Has method `readWeatherData(String city)` that gets the latest data for specified city
from the weather database that is updated by cron, and stores it in private variables.
Weather data can be accessed via getters.


### Delivery ###
Constructed with necessary information for calculating the delivery fee.
Has method `calculateFee()` which takes no parameters and uses data stored in the delivery object
to calculate the delivery fee. Returns `-1.0` if vehicle selection is forbidden due to weather.


# REST interface #

## GET /api/delivery/fee
Get the delivery fee for specified delivery situation

### Parameters

- `city` (required): City of the delivery.
- `vehicleType` (required): Type of vehicle used for delivery.

### Example request

`GET /api/delivery/fee?city=Tallinn&vehicleType=Car`

### Example response

```json
4.0
```

### Status codes
- `200 OK`: The request was successful
- `400 Bad Request`: Usage of selected `vehicleType` is forbidden due to weather in selected `city`.
- `404 Not Found`: Invalid `city` or `vehicleType`.
- `500 Internal Server Error`: There was an error while calculating the delivery fee.