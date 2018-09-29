package com.lee.gateway.exception;

import com.lee.gateway.bean.JsonResult;
import com.lee.gateway.bean.LeeConstant;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * @author lee
 * @date 2018/9/12
 */

public class LeeErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    /**
     * Create a new {@code DefaultErrorWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resourceProperties the resources configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     */
    public LeeErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 确定返回什么HttpStatus
     *
     * @param errorAttributes
     * @return
     */
    @Override
    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        //HttpStatus status = (HttpStatus) errorAttributes.get("status");
        // return HttpStatus.INTERNAL_SERVER_ERROR == status ? HttpStatus.OK : status;
        return HttpStatus.OK;
    }

    /**
     * 返回的错误信息json内容
     *
     * @param request
     * @param includeStackTrace
     * @return
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {

        Throwable error = this.getError(request);
        return JsonResult.responseReturnMap(LeeConstant.GATEWAY_ERROR_CODE, this.buildMessage(request, error));
    }

    private String buildMessage(Throwable t) {
        return "未知错误！";
    }

    private String buildMessage(ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("api-gateway Failed to handle request [");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }

    private HttpStatus determineHttpStatus(Throwable error) {
        return error instanceof ResponseStatusException ? ((ResponseStatusException) error).getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
