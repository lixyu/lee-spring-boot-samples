package com.lee.ai.aip.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult<T> {

    private static String successCode = "10000";
    private static String sysErrorCode="10001";

    private String errorCode;

    private String msg;

    private T data;

    public static <T> JsonResult<T> successResponse(T data) {
        return new JsonResult<>(successCode, "Success", data);
    }

    public static <T> JsonResult<T> errorResponse(String errorMessage) {
        return new JsonResult<>(sysErrorCode, errorMessage, null);
    }

    public static <T> JsonResult<T> errorResponse(String status, String errorMessage) {
        return new JsonResult<>(status, errorMessage, null);
    }

    public static Map<String, Object> responseReturnMap(String status, String errorMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", status);
        map.put("msg", errorMessage);
        map.put("data", null);
        return map;
    }
}