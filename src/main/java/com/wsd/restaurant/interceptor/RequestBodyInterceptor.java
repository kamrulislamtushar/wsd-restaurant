package com.wsd.restaurant.interceptor;

import com.wsd.restaurant.logging.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

@ControllerAdvice
public class RequestBodyInterceptor extends RequestBodyAdviceAdapter {

   private LoggingService logService;

  HttpServletRequest request;

  public RequestBodyInterceptor(LoggingService logService, HttpServletRequest request) {
    this.logService = logService;
    this.request = request;
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                              MethodParameter parameter, Type targetType,
                              Class<? extends HttpMessageConverter<?>> converterType) {
    logService.displayReq(request, body);
    return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
  }

  @Override
  public boolean supports(MethodParameter methodParameter, Type targetType,
                          Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }
}
