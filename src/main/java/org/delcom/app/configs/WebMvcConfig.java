package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.delcom.app.interceptors.WebAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor; // Untuk REST API

    @Autowired
    private WebAuthInterceptor webAuthInterceptor; // Untuk Web Pages

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Interceptor untuk REST API (JWT-based)
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // Terapkan ke semua endpoint /api
                .excludePathPatterns("/api/auth/**") // Kecuali endpoint auth
                .excludePathPatterns("/api/public/**"); // Dan endpoint public

        // Interceptor untuk Web Pages (Session-based)
        registry.addInterceptor(webAuthInterceptor)
                .addPathPatterns("/**") // Terapkan ke semua web pages
                .excludePathPatterns("/api/**") // Kecuali API
                .excludePathPatterns("/auth/**") // Kecuali halaman auth
                .excludePathPatterns("/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico"); // Kecuali static resources
    }
}