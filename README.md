# Webhook Solution Spring Boot App

This app automatically:
- Requests a webhook and JWT token on startup
- Submits the required SQL query to the webhook using the JWT token

## Build
```
mvn clean package
```

## Run
```
java -jar target/webhook-solution-1.0.0.jar
```

## Output
- The app prints the webhook URL, access token, and submission response to the console.

## Submission
- See instructions in the problem statement for submitting your JAR and repo link.
