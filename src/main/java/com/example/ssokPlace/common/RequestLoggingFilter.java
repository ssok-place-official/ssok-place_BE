package com.example.ssokplace.common;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 메서드, 경로(+쿼리), 처리시간, 상태코드 로깅
 * 본문 로깅은 기본 비활성화
 */
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) req;
        HttpServletResponse resp = (HttpServletResponse) res;

        long start = System.currentTimeMillis();
        int statusAfter = 500; // 기본값(예외 시)

        try {
            chain.doFilter(req, res);
            statusAfter = resp.getStatus();
        } finally {
            long took = System.currentTimeMillis() - start;
            String method = http.getMethod();
            String uri = http.getRequestURI();
            String qs = http.getQueryString();
            String path = (qs == null || qs.isBlank()) ? uri : (uri + "?" + qs);
            log.info("[REQ] {} {} -> {} ({} ms)", method, path, statusAfter, took);
        }
    }
}
