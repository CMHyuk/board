package com.spring.board.web.interceptor;

import com.spring.board.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.spring.board.Const.LOGIN_USER;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        HttpSession session = request.getSession();

        if(session == null || session.getAttribute(LOGIN_USER) == null) {
            log.info("미인증 사용자 요청");
            throw new AuthException();
        }
        return true;
    }
}
