# WeatherReport

This application fetches weather data for a given city using WeatherStack as the primary provider and OpenWeatherMap as a failover.

Prerequisites:
•	Java 17+
•	Maven 3.8+
•	Internet connection (for API calls)

Build the Project:
•	mvn clean install
This will:
•	Compile the code
•	Run all unit and integration tests
•	Package the app into a JAR file

 Run the Application
•	mvn spring-boot : run

Access the API
•	Once running, access the API at: http://localhost:9090/api/v1/weather?city=Melbourne 
Note : As instructed, I have hard coded the city as Melbourne to take from the application.properties , if you use other city name via API it will throw BadRequestException.

Run Tests
•	mvn test
This includes:
•	Unit tests for core logic
•	Integration tests using MockWebServer

Configuration : 
I have set my own api keys in application.properties file. You can use your own  API keys to configure on application.properties.
weatherstack.api.key=your-weatherstack-key
openweather.api.key=your-openweathermap-key
 
 
