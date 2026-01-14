package com.example.springmvc.config;

import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // Nạp các cấu hình tầng Root (Database, Security, Service...)
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{ AppConfig.class, SecurityConfig.class };
    }

    // Nạp cấu hình tầng Web (Controller, MVC...)
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{ WebMvcConfig.class };
    }

    // Map tất cả request vào DispatcherServlet của Spring
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    
    @Override
    protected Filter[] getServletFilters() {
        // Đăng ký CORS filter với priority cao nhất
        DelegatingFilterProxy corsFilterProxy = new DelegatingFilterProxy("corsFilter");
        corsFilterProxy.setTargetBeanName("corsFilter");
        return new Filter[]{corsFilterProxy};
    }
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        servletContext.addListener(new DatabaseCleanupListener());
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Cấu hình multipart cho file upload
        // maxFileSize: 10MB, maxRequestSize: 50MB, fileSizeThreshold: 1MB
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                null, // location - null để dùng temp directory
                10485760, // maxFileSize - 10MB
                52428800, // maxRequestSize - 50MB
                1048576  // fileSizeThreshold - 1MB
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
}