package com.example.employee.service;

import com.example.employee.dto.JsonTranslateRequest;
import com.example.employee.dto.JsonTranslateResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class YandexTranslateService {
    private Environment env;
    private ObjectMapper objectMapper;

    public JsonTranslateResult getTranslation(JsonTranslateRequest translationRequest) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(env.getProperty("yandex.api.key"));
        translationRequest.setFolderId(env.getProperty("yandex.api.folder"));
        HttpEntity<String> entity = new HttpEntity<>(translationRequest.toString(), headers);
        JsonTranslateResult result = restTemplate.postForObject(env.getProperty("yandex.api.url"), entity, JsonTranslateResult.class);

        if (Objects.nonNull(result) && Objects.nonNull(result.translations()))
            return result;
        return null;
    }
}
