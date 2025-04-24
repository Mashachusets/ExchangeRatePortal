# ExchangeRatePortal API
## Exchange rate portal API used for historical and real-time currency data
### Localhost URLs
- http://localhost:8080/api/convert
- http://localhost:8080/api/history
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### Requirements
Before running the project, make sure you have the following software installed on your machine:
- Java 17  (or a higher version)
- Maven 3.8 (or a newer version)

### Database Configuration
This project uses an embedded H2 database for simplicity and ease of development.

The database configuration is already set in src/main/resources/application.properties.

You can access the in-browser H2 console for development and debugging:
- URL: http://localhost:8080/h2-console
- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:file:./data/exchange_rates
- Username: sa
- Password: (leave blank)

### Using project
- Clone project from github : git clone <https://github.com/Mashachusets/ExchangeRatePortal.git>
- Navigate to the project directory: cd ExchangeRatePortal
- Run command: mvn spring-boot:run

Feel free to customize the instructions further based on your specific project requirements.