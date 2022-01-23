package com.example.pkscl.Interceptor;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
    response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        HttpSession session = request.getSession(false);

        // 미인증 사용자 요청시
        
        if (session == null || session.getAttribute("position") == null || session.getAttribute("email") == null) {
            log.info("미인증 사용자 요청");
            
            // 401 Unauthorized 응답
            response.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
            
            // 로그인 페이지로 이동
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}