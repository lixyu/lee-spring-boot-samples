package com.lee.gateway.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult<T> {

    private String errorCode;

    private String msg;

    private T data;

    public static <T> JsonResult<T> successResponse(T data) {
        return new JsonResult<>(LeeConstant.GATEWAY_SUCCESS_CODE, "Success", data);
    }

    public static <T> JsonResult<T> errorResponse(String errorMessage) {
        return new JsonResult<>(LeeConstant.GATEWAY_ERROR_CODE, errorMessage, null);
    }

    public static <T> JsonResult<T> errorResponse(String errorCode, String errorMessage) {
        return new JsonResult<>(errorCode, errorMessage, null);
    }

    public static Map<String, Object> responseReturnMap(String errorCode, String errorMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", errorCode);
        map.put("msg", errorMessage);
        map.put("data", null);
        return map;
    }
}