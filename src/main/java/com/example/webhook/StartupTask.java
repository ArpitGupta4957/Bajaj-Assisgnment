package com.example.webhook;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class StartupTask {
    private final RestTemplate restTemplate = new RestTemplate();

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        // Step 1: Generate webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"name\": \"John Doe\", \"regNo\": \"REG12347\", \"email\": \"john@example.com\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, entity, Map.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            System.out.println("Failed to generate webhook");
            return;
        }
        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");
        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // Step 2: Prepare SQL query
        String finalQuery = "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, (SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e.DEPARTMENT AND e2.DOB > e.DOB) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID ORDER BY e.EMP_ID DESC;";

        // Step 3: Submit solution
        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
    submitHeaders.set("Authorization", accessToken);
        String submitBody = String.format("{\"finalQuery\": \"%s\"}", finalQuery.replace("\"", "\\\""));
        HttpEntity<String> submitEntity = new HttpEntity<>(submitBody, submitHeaders);
        try {
            System.out.println("Submitting to webhook: " + webhookUrl);
            System.out.println("Request body: " + submitBody);
            ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, submitEntity, String.class);
            System.out.println("Submission response: " + submitResponse.getBody());
        } catch (Exception ex) {
            System.out.println("Error submitting to webhook: " + ex.getMessage());
            if (ex instanceof org.springframework.web.client.HttpClientErrorException) {
                org.springframework.web.client.HttpClientErrorException httpEx = (org.springframework.web.client.HttpClientErrorException) ex;
                System.out.println("Status code: " + httpEx.getStatusCode());
                System.out.println("Response body: " + httpEx.getResponseBodyAsString());
            }
        }
    }
}
