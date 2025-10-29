package com.testing.ex.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcLoggingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpReq = (HttpServletRequest) request;
    String requestId = UUID.randomUUID().toString();

    MDC.put("requestId", requestId);
    MDC.put("path", httpReq.getRequestURI());

    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear(); // Always clean up
    }
  }
}
