package com.cloud.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.sound.midi.Soundbank;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Component
public class Scheduler {

    @Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 60 * 1000)
    //@Scheduled(fixedDelay = 30 * 1000, initialDelay = 30 * 1000)
    public void testConnection() {
        System.out.println("\n\n-----------------------------");
        System.out.println("Time is - " + new Date());

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://mxbeanstalk-env.eba-mw6as2kj.eu-central-1.elasticbeanstalk.com/api";

        // Fetch Token
        String token = getToken(url, restTemplate);
        System.out.println("token fetched successfully= " + token);

        // Test Account
        testAccount(url, token, restTemplate);

        // Test Product
        testProduct(url, token, restTemplate);
    }

    private void testProduct(String url, String token, RestTemplate restTemplate) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

            int i = 1;
            String productNameTemplate = "PC_00";
            String productName;
            ResponseEntity<JsonNode> response;

            // Search Product name that doesn't already exist
            do {
                productName = productNameTemplate + i;
                response = restTemplate.exchange(url + "/products?filter=objectName eq '"+ productName +"'", HttpMethod.GET,
                        httpEntity, JsonNode.class);
                i++;
            } while(response.getBody().get("total").asInt() != 0);

            // create product with unique product name
            HttpHeaders httpHeaders1 = new HttpHeaders(httpHeaders);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> httpEntity1 = new HttpEntity<>(getBody(productName), httpHeaders1);
            response = restTemplate.exchange(url + "/products", HttpMethod.POST,
                    httpEntity1, JsonNode.class);

            String productId = response.getBody().get(0).get("id").asText();
            System.out.println("Product created successfully with id = " + productId);

            // Get Product
            response = restTemplate.exchange(url + "/products/" + productId, HttpMethod.GET,
                    httpEntity, JsonNode.class);
            String actualObjectName = response.getBody().get("objectName").asText();

            System.out.println("Comparing {" + productName +"} with {" + actualObjectName + "}");
            if(!productName.equals(actualObjectName)) {
                System.out.println("Object Names are not equal");
            }

            // Delete Product
            restTemplate.exchange(url + "/products/" + productId, HttpMethod.DELETE,
                    httpEntity, String.class);

            System.out.println("Product deleted");

            // Search Product
            response = restTemplate.exchange(url + "/products?filter=objectName eq '"+ productName +"'", HttpMethod.GET,
                    httpEntity, JsonNode.class);

            if(response.getBody().get("total").asInt() != 0) {
                System.out.println("Product found in search");
            }
        } catch (Exception ex) {
            System.err.println("Error while testing product");
            ex.printStackTrace();
        }
    }

    private void testAccount(String url, String token, RestTemplate restTemplate) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

            // Search any account
            ResponseEntity<JsonNode> response = restTemplate.exchange(url + "/accounts", HttpMethod.GET,
                    httpEntity, JsonNode.class);

            int size = response.getBody().get("entry").size();
            int random = new Random().nextInt(size);
            System.out.println("Total accounts fetched = " + size + ", testing index = " + random);

            JsonNode account = response.getBody().get("entry").get(random);
            String accountId = account.get("id").asText();
            String expectedObjectName = account.get("objectName").asText();

            // Fetch Account
            ResponseEntity<JsonNode> fetchedAccount = restTemplate.exchange(url + "/accounts/" + accountId, HttpMethod.GET,
                    httpEntity, JsonNode.class);
            String actualObjectName = fetchedAccount.getBody().get("objectName").asText();

            System.out.println("Comparing {" + expectedObjectName +"} with {" + actualObjectName + "}");
            if(!expectedObjectName.equals(actualObjectName)) {
                System.out.println("Object Names are not equal");
            }

        } catch (Exception ex) {
            System.err.println("Error while testing account");
            ex.printStackTrace();
        }
    }

    private String getToken(String url, RestTemplate restTemplate) {
        try{
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", "mx_client");
            map.add("username", "admin");
            map.add("password", "imc123");

            HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(map, httpHeaders);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url + "/oauth/token", HttpMethod.POST,
                    httpEntity, JsonNode.class);

            return Objects.requireNonNull(response.getBody()).get("access_token").asText();
        } catch (Exception ex) {
            System.err.println("Error while fetching token");
            ex.printStackTrace();
        }

        return null;
    }

    String getBody(String productName) {
        return  "{\"type\":\"Product\",\"label\":\""+productName+"\",\"objectName\":\""+productName+"\",\"hasProductStatus\":[\"http://www.inmindcloud.com/application/schema.owl#ProductActive\"],\"hasProductType\":[\"http://www.inmindcloud.com/application/schema.owl#CONFIGURABLE\"]}";
    }
}
