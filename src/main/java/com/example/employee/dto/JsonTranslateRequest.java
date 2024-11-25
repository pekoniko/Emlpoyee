package com.example.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Arrays;

//targetLanguageCode - result text language
//texts - all texts to translate
//folderId - for authorisation with account
@NoArgsConstructor
@Data
public class JsonTranslateRequest {
    @NotNull
    String targetLanguageCode;
    @NotNull
    String[] texts;
    @NotNull
    String folderId;

    public JsonTranslateRequest(String targetLanguageCode, String[] texts) {
        this.targetLanguageCode = targetLanguageCode;
        this.texts = texts;
    }

    @Override
    public String toString() {
        return "{\"targetLanguageCode\":\"" + targetLanguageCode + "\",\"texts\":" + textsToString() +
                ",\"folderId\":\"" + folderId + "\"}";
    }

    private String textsToString() {
        StringBuilder result = new StringBuilder("[\"");
        for (int i = 0; i < texts.length; i++) {
            String text = texts[i];
            result.append(text).append("\"");
            if (i != texts.length - 1)
                result.append(",\"");
        }
        result.append("]");
        return result.toString();
    }
}
