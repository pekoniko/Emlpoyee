package com.example.employee.controller;

import com.example.employee.dto.JsonTranslateRequest;
import com.example.employee.dto.JsonTranslateResult;
import com.example.employee.service.YandexTranslateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class YandexTranslateController {
    private final YandexTranslateService service;

    @Operation(summary = "Get translation",
            description = "Get texts translation using yandex api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get translation on chosen language " +
                    "using yandex translation  api",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonTranslateResult.class))})})
    @PostMapping
    public JsonTranslateResult getTranslate(@RequestBody JsonTranslateRequest translationRequest) {

        return service.getTranslation(translationRequest);
    }

}
