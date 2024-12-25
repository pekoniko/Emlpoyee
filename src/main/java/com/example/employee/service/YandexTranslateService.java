package com.example.employee.service;

import com.example.employee.dto.JsonTranslateKeyResult;
import com.example.employee.dto.JsonTranslateRequest;
import com.example.employee.dto.JsonTranslateResult;
import com.example.employee.entities.ApiKey;
import com.example.employee.repositories.ApiKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class YandexTranslateService {
    private final ApiKeyRepository apiKeyRepository;
    private final RestTemplate restTemplate;
    private final static StringHttpMessageConverter CONVERTER = new StringHttpMessageConverter(StandardCharsets.UTF_8);
    @Value("${yandex.api.folder}")
    private String apiFolder;
    @Value("${yandex.api.translate.url}")
    private String translateUrl;
    @Value("${yandex.api.key}")
    private String apiKey;
    @Value("${yandex.api.token.url}")
    private String tokenUrl;

    public String getTranslation(String language, String text) {
        JsonTranslateRequest request = new JsonTranslateRequest(language, new String[]{text});
        restTemplate.getMessageConverters()
                .add(0, CONVERTER);
        HttpHeaders headers = new HttpHeaders();
        String actualKey = getActualKey();
        if (actualKey == null)
            return null; //todo Exception
        headers.setBearerAuth(actualKey);
        request.setFolderId(apiFolder);
        ObjectMapper mapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        JsonTranslateResult result = restTemplate.postForObject(translateUrl, entity, JsonTranslateResult.class);
        if (Objects.nonNull(result) && Objects.nonNull(result.translations())) {
            return result.translations().get(0).get("text");
        }
        return null; //todo Exception
    }

    private String getActualKey() {
        return apiKeyRepository.findAll()
                .stream()
                .max(Comparator.comparing(ApiKey::getApiTime))
                .map(ApiKey::getKey)
                .orElse(null);
    }


    public JsonTranslateKeyResult getNewApiKey() {
        restTemplate.getMessageConverters()
                .add(0, CONVERTER);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"yandexPassportOauthToken\":\"" + apiKey + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        JsonTranslateKeyResult result = restTemplate.postForObject(tokenUrl, entity, JsonTranslateKeyResult.class);
        if (Objects.nonNull(result) && Objects.nonNull(result.getIamToken())) {
            return result;
        }
        return null;
    }

}
