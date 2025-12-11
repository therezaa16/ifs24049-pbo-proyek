package org.delcom.app.interceptors;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor untuk Web Pages (session-based authentication)
 * Berbeda dengan AuthInterceptor yang untuk REST API (JWT-based)
 */
@Component
public class WebAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthContext authContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Ambil authentication dari Spring Security Session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Jika user sudah login dan principal adalah User object
        if (authentication != null 
            && authentication.isAuthenticated() 
            && authentication.getPrincipal() instanceof User) {
            
            User user = (User) authentication.getPrincipal();
            authContext.setAuthUser(user);
        }
        
        return true;
    }
}