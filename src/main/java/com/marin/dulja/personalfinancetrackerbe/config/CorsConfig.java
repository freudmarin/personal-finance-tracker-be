package com.marin.dulja.personalfinancetrackerbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(@Value("${spring.profiles.active:}") String profile, @Value("${allowed.origin:personal-finances-01.netlify.app}") String allowedOrigin) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        if ("dev".equals(profile)) {
            config.setAllowedOrigins(List.of("http://localhost:5173"));
        } else {
            config.setAllowedOrigins(List.of(allowedOrigin, "http://localhost:5173")); // fallback to prod origin
        }

        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}