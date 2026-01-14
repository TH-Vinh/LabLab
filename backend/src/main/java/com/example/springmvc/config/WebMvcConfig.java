package com.example.springmvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.springmvc")
@PropertySource("classpath:app.properties")
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        return resolver;
    }

    @Override
    public Validator getValidator() {
        return validator();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Tách chuỗi thành mảng các domain
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/**") // Áp dụng cho tất cả API
                .allowedOrigins(origins) // Cho phép các domain trong list
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        System.out.println("✅ CORS Configured for: " + allowedOrigins);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:///" + path + "avatars/");

        System.out.println("✅ Đã map thư mục ảnh tại: " + path + "avatars/");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String origin = request.getHeader("Origin");
                String method = request.getMethod();
                
                System.out.println("🌐 CORS Interceptor - Method: " + method + ", Path: " + request.getRequestURI() + ", Origin: " + origin);
                
                if (origin != null && isAllowedOrigin(origin)) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "*");
                    response.setHeader("Access-Control-Expose-Headers", "*");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    System.out.println("✅ CORS headers added by interceptor for origin: " + origin);
                }
                
                if ("OPTIONS".equalsIgnoreCase(method)) {
                    System.out.println("✅ Handling OPTIONS preflight in interceptor");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return false; // Stop further processing
                }
                
                return true;
            }
            
            private boolean isAllowedOrigin(String origin) {
                String[] origins = allowedOrigins.split(",");
                for (String allowedOrigin : origins) {
                    if (allowedOrigin.trim().equals(origin)) {
                        return true;
                    }
                }
                return false;
            }
        }).addPathPatterns("/**");
    }
}