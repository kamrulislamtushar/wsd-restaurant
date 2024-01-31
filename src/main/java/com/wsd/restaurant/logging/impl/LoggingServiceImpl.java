package com.wsd.restaurant.logging.impl;


import com.wsd.restaurant.logging.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class LoggingServiceImpl implements LoggingService {

  Logger logger = LoggerFactory.getLogger("LoggingServiceImpl");

  @Override
  public void displayReq(HttpServletRequest request, Object body) {
    StringBuilder reqMessage = new StringBuilder();
    Map<String, String> parameters = getParameters(request);
    if (!Objects.isNull(body)) {
      reqMessage.append(" body = [").append(body).append("]");
    }
    MDC.put("requestBody", String.valueOf(reqMessage));
    logger.info("log Request: {}", reqMessage);
  }

  @Override
  public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
    StringBuilder respMessage = new StringBuilder();
    Map<String, String> headers = getHeaders(response);
    respMessage.append("RESPONSE ");
    respMessage.append(" responseBody = [").append(body).append("]");
    MDC.put("responseBody", String.valueOf(respMessage));
    logger.info("logResponse: {}", respMessage);
  }

  private Map<String, String> getHeaders(HttpServletResponse response) {
    Map<String, String> headers = new HashMap<>();
    Collection<String> headerMap = response.getHeaderNames();
    for (String str : headerMap) {
      headers.put(str, response.getHeader(str));
    }
    return headers;
  }

  private Map<String, String> getParameters(HttpServletRequest request) {
    Map<String, String> parameters = new HashMap<>();
    Enumeration<String> params = request.getParameterNames();
    while (params.hasMoreElements()) {
      String paramName = params.nextElement();
      String paramValue = request.getParameter(paramName);
      parameters.put(paramName, paramValue);
    }
    return parameters;
  }


}
