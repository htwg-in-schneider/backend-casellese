# Notes
- Achte auf H2 Configuration and MariaDB-Configuration in application.properties
- CTRL + C beendet Prozess
- lsof -i :8081 | grep LISTEN (PID graben)
- kill -9 <PID> (Prozess killen)
- ./mvnw spring-boot:run (BackEnd starten)

# Support-Fragen Herr Schneider
- Was ist der Unterschied zu mvn spring-boot:run und starten der BackEnd-Application?
- 

# Iteration 1:
./mvnw spring-boot:run , !WICHTIG! Das Backend erst starten, bevor das Terminal funktioniert
curl http://localhost:8081/api/product in neuem Bash Terminal

# Iteration 2: 
## Lokal mit H2 (erst Java-Prozesse beenden!)
pkill -f java  # oder: rm -f ./target/saitenweise-db.mv.db
mvn spring-boot:run -Dspring-boot.run.profiles=local

## Mit Test-Datenbank (MariaDB)
mvn spring-boot:run -Dspring-boot.run.profiles=test

## Mit Produktions-Datenbank (MariaDB)
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Iteration 3:
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
- JDBC URL: jdbc:h2:file:./target/saitenweise-db;AUTO_SERVER=TRUE
- Username: sa
- unter http://localhost:8081/h2-console
![alt text](image-1.png)

# Iteration 4: CORS Configuration
Ohne diese Konfiguration würde der Browser Anfragen vom Frontend (z.B. http://localhost:5173) an das Backend (z.B. http://localhost:8081) blockieren, da sie von unterschiedlichen Origins kommen. Die CORS-Konfiguration erlaubt diese Cross-Origin-Anfragen.

# Iteration 5: Added GitHub Actions workflow for Maven build verification
Der GitHub Actions Workflow macht Folgendes:
Schritt : Beschreibung 
______________________
Trigger : Läuft bei jedem push und pull_request

Checkout: Klont das Repository

JDK 21 Setup: Installiert Java 21 (Temurin Distribution) mit Maven-Caching

Maven Verify: Führt mvn verify aus (kompiliert, testet, verifiziert)

![alt text](image.png)

### Iteration 6: CRUD for Products (and tests)
mvn test -Dspring.profiles.active=local , um Tests durchzuführen
![alt text](image-2.png)

### Iteration 7: Added 1:n relation Product - Review
Benötige keine Reviews

### Iteration 8: Search and filter products
http://localhost:8081/api/product
http://localhost:8081/api/product?name=Brot
http://localhost:8081/api/product?category=SALAMI
http://localhost:8081/api/product?name=Sal&category=SALAMI
http://localhost:8081/api/category

(nach ./mvnw spring-boot:run)

### Iteration 9: Added user profile and spring security (OAuth2 with Auth0)
Lokal testen:
> .requestMatchers("/api/**").permitAll()) auskommentieren
> .anyRequest().permitAll())

> ./mvnw spring-boot:run -> http://localhost:8081/api/product

> Bearer Token unter -> https://manage.auth0.com/dashboard/us/dev-xtay6xv00u4qv7bp/apis/6943dc63e6b6264b90c426f2/test unter -> jwt.io

> curl -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im5OUDV2WUdoY0hGTVNaUWs4RkY1QiJ9.eyJpc3MiOiJodHRwczovL2Rldi14dGF5Nnh2MDB1NHF2N2JwLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJZQTZ4YVRyMXBWNEpVQkpzRGYwU1BGbHpXamNpdWUxZEBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9hcGkuY2FzZWxsZXNlLmxvY2FsIiwiaWF0IjoxNzY3MjYwMzMxLCJleHAiOjE3NjczNDY3MzEsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IllBNnhhVHIxcFY0SlVCSnNEZjBTUEZseldqY2l1ZTFkIn0.0tjVdExyBz0dZBGBv5-yXRzyVgt9xno_086PW7WwDcDiuPH_yEb0CefUsW6M4piA6Qj3RN8e-l_KPhqITx4Vl9BIi25Nv1nby0yWz00njQzJ6Bj2TGufTvz4kHNEV76Mfqwv4DfpQU7nV6D6m1o7h8dYLwpeRTsvCUpClxzCjfuouP0jma-Op-_FtzzmgZ2aDTk7Zn0PXgJS7q9zarnOv-6-ZM7GRTGRKBDf-8YcSd59JrX3MsHebbJZ-yAu3p4qGQKEw3QLIpu-LcqaVozyHvwk6-7GODIaYXH5u_Xb59_4YpOg1FkIMhZT5HqbhNlrCfcSKFgBRQ-zR_VqCD0Qmg" http://localhost:8081/api/profile

> curl -v -H für Details

> WICHTIG: ![alt text](image-3.png)
PS: Ist noch auf Machine-to-Machine Token

.