package com.wsd.restaurant.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

public class LogStats {
  private LogStats() {
  }

  public static void logStats(HttpServletRequest request, HttpServletResponse response,
                              Exception ex) {
    String executeTime = MDC.get("startTime");
    double executeTimeInSeconds = Double.NaN;
    if (executeTime != null) {
      executeTimeInSeconds =
          (System.currentTimeMillis() - Long.parseLong(executeTime)) / 1000.0;
    }
    Map<String, String[]> params = getQueryParameters(request);
    MDC.put("referer", request.getHeader(HttpHeaders.REFERER));
    MDC.put("requestParams", toJson(params));
    MDC.put("requestPath", request.getMethod() + "-" + request.getRequestURL().toString());
    MDC.put("executeTime", Double.toString(executeTimeInSeconds));
    MDC.put("responseStatus", Integer.toString(response.getStatus()));
    if (ex != null) {

      MDC.put("exception", ex.getClass().getCanonicalName());
    }
  }

  private static Map<String, String[]> getQueryParameters(HttpServletRequest request) {
    Map<String, String[]> queryParameters = new HashMap<>();
    String queryString = request.getQueryString();
    if (queryString != null && !queryString.isEmpty()) {
      try {
        queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString());
        String[] params = queryString.split("&");
        for (String param : params) {
          String[] parts = param.split("=", 2);
          String key = parts[0];
          String value = (parts.length > 1) ? parts[1] : "";
          String[] values = queryParameters.getOrDefault(key, new String[0]);
          String[] newValues = new String[values.length + 1];
          System.arraycopy(values, 0, newValues, 0, values.length);
          newValues[values.length] = value;
          queryParameters.put(key, newValues);
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    return queryParameters;
  }

  private static String toJson(Object object) {
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}
