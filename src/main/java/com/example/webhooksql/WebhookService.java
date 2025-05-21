package com.example.webhooksql;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    
    private final String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    
    private final String regNo = "REG12347";

    private final String name = "John Doe";
    private final String email = "john@example.com";

    public void processWebhookFlow() {
        
        Map<String, String> requestBody = Map.of(
                "name", name,
                "regNo", regNo,
                "email", email
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        
        ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            
            String finalQuery = getFinalQuery();

            
            sendFinalQuery(webhookUrl, accessToken, finalQuery);
        } else {
            System.out.println("Failed to get webhook or access token");
        }
    }

    private String getFinalQuery() {
        
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastNum = Integer.parseInt(lastTwoDigits);

        if (lastNum % 2 == 1) {
            
            return
                "SELECT PAYMENTS.AMOUNT AS SALARY, " +
                "CONCAT(EMPLOYEE.FIRST_NAME, ' ', EMPLOYEE.LAST_NAME) AS NAME, " +
                "YEAR(CURRENT_DATE) - YEAR(EMPLOYEE.DOB) AS AGE, " +
                "DEPARTMENT.DEPARTMENT_NAME " +
                "FROM PAYMENTS " +
                "JOIN EMPLOYEE ON PAYMENTS.EMP_ID = EMPLOYEE.EMP_ID " +
                "JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT = DEPARTMENT.DEPARTMENT_ID " +
                "WHERE DAY(PAYMENTS.PAYMENT_TIME) <> 1 " +
                "AND PAYMENTS.AMOUNT = ( " +
                "    SELECT MAX(AMOUNT) " +
                "    FROM PAYMENTS " +
                "    WHERE DAY(PAYMENTS.PAYMENT_TIME) <> 1 " +
                ") LIMIT 1";
        } else {
            // Even: You can put question 2 SQL here
            return "";
        }
    }

    private void sendFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        
        headers.setBearerAuth(accessToken);

        Map<String, String> body = Map.of("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        System.out.println("Submission response status: " + response.getStatusCode());
        System.out.println("Submission response body: " + response.getBody());
    }
}
