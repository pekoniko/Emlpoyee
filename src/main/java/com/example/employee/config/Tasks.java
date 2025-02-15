package com.example.employee.config;

import com.example.employee.dto.JsonTranslateKeyResult;
import com.example.employee.entities.ApiKey;
import com.example.employee.repositories.ApiKeyRepository;
import com.example.employee.service.YandexTranslateService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
public class Tasks {
    private final ApiKeyRepository apiKeyRepository;
    private final YandexTranslateService yandexTranslateService;

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 12, initialDelay = 0)
    public void scheduleFixedRateTask() {
        Timestamp keyExpirationDate = apiKeyRepository.findAll()
                .stream()
                .map(ApiKey::getApiTime)
                .max(Comparator.comparing(Timestamp::getTime)).orElse(null);
        if (keyExpirationDate == null || LocalDateTime.now().isAfter(keyExpirationDate.toLocalDateTime())) {
            getKey();
        }
    }

    private void getKey() {
        JsonTranslateKeyResult response = yandexTranslateService.getNewApiKey();
        if (response.getIamToken() != null && response.getExpiresAt() != null) {
            LocalDateTime keyEndTime = LocalDateTime.parse(response.getExpiresAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn'Z'"));
            apiKeyRepository.save(new ApiKey(response.getIamToken(), keyEndTime));
        }
    }

}
