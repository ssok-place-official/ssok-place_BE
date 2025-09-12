package com.example.ssokPlace.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * 요청마다 X-CID를 부여해서 로그 MDC에 넣어 전 구간 추적
 */
@Component
@Order(0)
public class CorrelationIdFilter implements Filter {
    public static final String HDR = "X-CID";
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException{
        var http = (HttpServletRequest) req;
        var resp = (HttpServletResponse) res;
        String cid = Optional.ofNullable(http.getHeader(HDR)).orElse(UUID.randomUUID().toString());
        MDC.put(HDR, cid);
        http.setAttribute(HDR, cid);
        resp.setHeader(HDR, cid);
        try { chain.doFilter(req, res); }
        finally { MDC.remove(HDR); }
    }
}
