package com.example.pkscl.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;


public class EditPageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
    response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // 학생회장이 아니면 /main으로 이동
        if(!session.getAttribute("position").equals("president")) {
            response.sendRedirect("/main");
            return false;
        }
        
        return true;
    }
}