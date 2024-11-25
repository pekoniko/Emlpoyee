package com.example.employee.dto;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

//translations[] - all translated texts. Consist of [text][detectedLanguageCode].
public record JsonTranslateResult(@NotNull ArrayList<HashMap<String, String>> translations) {
}
