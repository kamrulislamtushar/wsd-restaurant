package com.wsd.restaurant.interceptor;


import com.wsd.restaurant.util.LogStats;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

@Component
public class StatsLoggingInterceptor implements AsyncHandlerInterceptor {
  private static final Logger STAT_LOGGER = LoggerFactory.getLogger("STATS-LOGGING");

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) {
    MDC.put("startTime", String.valueOf(System.currentTimeMillis()));
    MDC.put("referer", request.getHeader(HttpHeaders.REFERER));
    MDC.put("requestPath", request.getMethod() + "-" + request.getRequestURL().toString());
    MDC.put("responseStatus", String.valueOf(response.getStatus()));
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, @Nullable Exception ex) {
    LogStats.logStats(request, response, ex);
    STAT_LOGGER.info("Log MDC items");
  }
}
