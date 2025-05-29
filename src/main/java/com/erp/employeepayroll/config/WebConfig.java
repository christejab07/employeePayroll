//package com.erp.employeepayroll.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web configuration class for CORS settings.
// * Allows frontend applications from different origins to access the API.
// */
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // Apply CORS to all endpoints
//                .allowedOrigins("http://localhost:3000", "http://your-frontend-domain.com") // Replace with your frontend URL(s)
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
//                .allowedHeaders("*") // Allowed headers
//                .allowCredentials(true) // Allow credentials (cookies, authorization headers)
//                .maxAge(3600); // Max age for preflight requests
//    }
//}
