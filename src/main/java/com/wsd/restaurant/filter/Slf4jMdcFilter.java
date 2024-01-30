package com.wsd.restaurant.filter;

import com.wsd.restaurant.util.Constants;
import com.wsd.restaurant.util.MdcIdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class Slf4jMdcFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    try {
      String token = MdcIdUtil.generateMdcId();
      MdcIdUtil.setMdcId(token);
      response.addHeader(Constants.NAME_CO_RELATION_ID, token);
      chain.doFilter(request, response);
    } finally {
      MdcIdUtil.removeMdcId();
    }
  }
}
