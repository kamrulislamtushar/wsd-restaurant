package com.wsd.restaurant.interceptor;

import com.wsd.restaurant.logging.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptLog implements HandlerInterceptor {

  private LoggingService loggingService;

  public InterceptLog(LoggingService loggingService) {
    this.loggingService = loggingService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) {
    if (request.getMethod().equals(HttpMethod.GET.name())
        || request.getMethod().equals(HttpMethod.DELETE.name())
        || request.getMethod().equals(HttpMethod.PUT.name())) {
      loggingService.displayReq(request, null);
    }
    return true;
  }
}
