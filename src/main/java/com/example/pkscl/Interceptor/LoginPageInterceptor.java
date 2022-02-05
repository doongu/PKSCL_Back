package com.example.pkscl.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;


public class LoginPageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
    response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // 로그인된 사용자는 재로그인 불가
        if(session != null && session.getAttribute("position") != null) {
            response.sendRedirect("/main");
            return false;
        }
        
        return true;
    }
}