package com.example.employee;

import com.example.employee.dto.JsonTranslateKeyResult;
import com.example.employee.entities.ApiKey;
import com.example.employee.repositories.ApiKeyRepository;
import com.example.employee.repositories.EmployeeRepository;
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
import java.util.List;

@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
public class Tasks {
    private final ApiKeyRepository apiKeyRepository;
    private final YandexTranslateService yandexTranslateService;

    @Scheduled(fixedRate = 43200000, initialDelay = 1000)
    public void scheduleFixedRateTask() {
        List<ApiKey> apiKeyList = apiKeyRepository.findAll();
        apiKeyList.sort(Comparator.comparing(ApiKey::getApiTime));
        Timestamp dateNow = new Timestamp(new Date().getTime());
        if (apiKeyList.isEmpty() || dateNow.before(apiKeyList.get(apiKeyList.size() - 1).getApiTime())) {
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
