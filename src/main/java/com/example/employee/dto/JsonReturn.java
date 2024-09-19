package com.example.employee.dto;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class JsonReturn {
    List<Object> result;
    String error;
    boolean success;

    public JsonReturn(Object result, String error, boolean success) {
        if (result == null || result instanceof List<?>) {
            this.result = (List<Object>) result;
        } else {
            var resultList = new ArrayList<Object>();
            resultList.add(result);
            this.result = resultList;
        }
        this.error = error;
        this.success = success;
    }

}
