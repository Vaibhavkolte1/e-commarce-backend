package com.college.e_commarce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Allow cookies
        config.setAllowedOrigins(List.of("http://localhost:5173",
<<<<<<< HEAD
                "http://127.0.0.1:5173",
                "http://**")); // Your frontend
=======
                                        "https://clg-final-project-vaibhavs-projects-a019b67a.vercel.app/",
                                        "https://**")); // Your frontend
>>>>>>> 63117c875adfd085f7485d54694a492517f974f2
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Set-Cookie")); // Expose cookies to frontend

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
