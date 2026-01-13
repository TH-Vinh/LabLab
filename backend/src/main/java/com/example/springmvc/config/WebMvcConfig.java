package com.example.springmvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
}