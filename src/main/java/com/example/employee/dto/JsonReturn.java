package com.example.employee.dto;

import java.util.ArrayList;
import java.util.List;

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

    public List<Object> getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

}
