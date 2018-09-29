package com.lee.gateway.model;

import lombok.Data;
import org.springframework.http.HttpHeaders;

//import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 日志审计入库的实体类
 *
 * @author wichell
 */
@Data
//@Document(collection = "audit_log")
public class AuditLogPO {
    private String serviceId;
    private String sourceIp;
    private String requestMethod;
    private String requestUri;
    private HttpHeaders requestHeaders;
    private String requestBody;
    private Long startTimestamp;
    private int statusCode;
    private HttpHeaders responseHeaders;
    private String responseBody;
    private Long endTimestamp;
}
