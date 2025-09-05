package com.hackathon.inditex;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * Test configuration to provide a RestTemplate bean that supports HTTP PATCH requests.
 * <p>
 * This configuration is only loaded in the test context, not in production.
 * It uses Apache HttpClient to enable full HTTP method support for RestTemplate.
 */
@TestConfiguration
public class PatchSupportConfig {

    /**
     * Creates a RestTemplate bean with support for HTTP PATCH requests.
     *
     * @return a RestTemplate instance configured with HttpComponentsClientHttpRequestFactory
     */
    @Bean
    public RestTemplate restTemplate() {
        HttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
