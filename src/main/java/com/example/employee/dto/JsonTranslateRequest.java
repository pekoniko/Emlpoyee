package com.example.employee.dto;

import lombok.*;


//targetLanguageCode - result text language
//texts - all texts to translate
//folderId - for authorisation with account
@NoArgsConstructor
@Data
public class JsonTranslateRequest {
    private String targetLanguageCode;
    private String[] texts;
    private String folderId;

    public JsonTranslateRequest(String targetLanguageCode, String[] texts) {
        this.targetLanguageCode = targetLanguageCode;
        this.texts = texts;
    }
}
