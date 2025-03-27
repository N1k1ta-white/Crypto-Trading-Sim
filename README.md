# Crypto Trading Simulator

A full-stack application for simulating cryptocurrency trading with real-time market data from Kraken.

## Project Structure

- **Backend**: Spring Boot application with MySQL database
- **Frontend**: Angular application

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java**: 21+
- **Node.js**: 16+
- **npm**: 8+
- **Maven**: 3.8+
- **MySQL**: 8.0+

## Database Setup

The project includes a Docker Compose setup for MySQL database:

1. Navigate to the database directory:
    ```bash
    cd db
    ```

2. Create a `.env` file in the `db` directory with the following variables:
    ```
    MYSQL_ROOT_PASSWORD=your_root_password
    MYSQL_DATABASE=crypto_trading_sim
    MYSQL_USER=app_user
    MYSQL_PASSWORD=app_password
    ```

3. Start the database services:
    ```bash
    docker-compose up -d
    ```

This will initialize:
- MySQL database server on port 3306
- Adminer (database management UI) accessible at http://localhost:8080

To stop the database services:
```bash
docker-compose down
```

## Initial Database Setup

For the backend to function correctly, you need to manually add some initial data to the database:

1. Connect to the database using one of these methods:
    - Use Adminer at http://localhost:8080 
      - System: MySQL
      - Server: db (or mysql if using host network)
      - Username: app_user
      - Password: app_password
      - Database: crypto_trading_sim
    - Connect directly to MySQL container:
      ```bash
      docker exec -it db mysql -u app_user -p crypto_trading_sim
      ```
    - Connect with your MySQL client if using port forwarding:
      ```bash
      mysql -h localhost -u app_user -p crypto_trading_sim
      ```

2. Add required cryptocurrency codes by running the following SQL commands:

    ```sql
    INSERT INTO crypto_code (code) VALUES ('BTC');
    INSERT INTO crypto_code (code) VALUES ('ETH');
    INSERT INTO crypto_code (code) VALUES ('XRP');
    INSERT INTO crypto_code (code) VALUES ('SOL');
    INSERT INTO crypto_code (code) VALUES ('DOT');
    -- Add any other cryptocurrency codes you need
    ```

These codes must be added before starting the backend server, as they are used for initializing the real-time market data connections.

## Backend Setup

1. Navigate to the backend directory:
    ```bash
    cd backend
    ```

2. Create or update `application.properties` in the `src/main/resources` directory:
    ```properties
    # Server configuration
    spring.application.name=crypto-trading-backend
    server.port=5000

    # Database connection
    spring.datasource.url=jdbc:mysql://localhost:3306/crypto_trading_sim
    spring.datasource.username=app_user
    spring.datasource.password=app_password
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    # JPA/Hibernate
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

    # Kraken
    kraken.websocket.url=wss://ws.kraken.com/v2

    # CORS
    allowed.origins.http=http://localhost:4200

    # JWT
    jwt.term=7200000
    jwt.secretKey=BASE64 secret

    # JSON Configuration
    # Excludes properties with null values from JSON responses
    spring.jackson.default-property-inclusion=non_null

    # Values
    crypto.pair.currency=USD
    reset.balance=10000
    ```

3. Build the application:
    ```bash
    mvn clean install
    ```

4. Run the application:
    ```bash
    mvn spring-boot:run
    ```

For testing:
```bash
mvn test
```

The Spring Boot application will start and be available at http://localhost:5000

## Frontend Setup

1. Navigate to the frontend directory:
    ```bash
    cd frontend
    ```

2. Create or update `env.ts` in the `src/app` directory:
    ```typescript
    export const env = {
      production: false,
      apiUrl: 'http://localhost:5000/api'
    };
    ```

3. Install dependencies:
    ```bash
    npm install
    ```

4. Run the development server:
    ```bash
    npm start
    ```
   The application will be available at http://localhost:4200

For testing:
```bash
npm test
```

The Angular application requires the backend to be running for full functionality. Make sure the Spring Boot application is running at http://localhost:5000 before connecting to the frontend.