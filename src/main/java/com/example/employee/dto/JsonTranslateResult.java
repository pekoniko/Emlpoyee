package com.example.employee.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

//translations[] - all translated texts. Consist of requested number of [text][detectedLanguageCode] pairs.
public record JsonTranslateResult(@NotNull List<Map<String, String>> translations) {
}
