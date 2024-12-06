package com.example.employee.service;

import com.example.employee.dto.JsonTranslateKeyResult;
import com.example.employee.dto.JsonTranslateRequest;
import com.example.employee.dto.JsonTranslateResult;
import com.example.employee.entities.ApiKey;
import com.example.employee.repositories.ApiKeyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class YandexTranslateService {
    private Environment env;
    private ApiKeyRepository apiKeyRepository;

    public JsonTranslateResult getTranslation(JsonTranslateRequest translationRequest) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        String actualKey = getActualKey();
        if (actualKey == null)
            return null;
        headers.setBearerAuth(actualKey);
        translationRequest.setFolderId(env.getProperty("yandex.api.folder"));
        HttpEntity<String> entity = new HttpEntity<>(translationRequest.toString(), headers);

        JsonTranslateResult result = restTemplate.postForObject(env.getProperty("yandex.api.translate.url"), entity, JsonTranslateResult.class);
        if (Objects.nonNull(result) && Objects.nonNull(result.translations())) {
            return result;
        }
        return null;
    }

    private String getActualKey() {
        List<ApiKey> apiKeyList = apiKeyRepository.findAll();
        apiKeyList.sort(Comparator.comparing(ApiKey::getApiTime));
        if (!apiKeyList.isEmpty()) {
            return apiKeyList.get(apiKeyList.size() - 1).getKey();
        }
        return null;
    }

    public JsonTranslateKeyResult getNewApiKey() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"yandexPassportOauthToken\":\"" + env.getProperty("yandex.api.key") + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        JsonTranslateKeyResult result = restTemplate.postForObject(env.getProperty("yandex.api.token.url"), entity, JsonTranslateKeyResult.class);
        if (Objects.nonNull(result) && Objects.nonNull(result.getIamToken())) {
            return result;
        }
        return null;
    }

}
