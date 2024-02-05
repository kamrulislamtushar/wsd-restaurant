# WSD Order Management

## Building for production

### Packaging as jar

To build the final jar and optimize the orderManagement application for production, run:
```
./mvnw clean package -DskipTests  
```

Once that is done please run 

```
docker compose up
```

Once the application is running it can be accessed via 

```
http://localhost:8080
```
