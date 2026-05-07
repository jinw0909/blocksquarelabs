package com.blocksquarelabs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // index.html
                        .requestMatchers("/", "/index.html").permitAll()

                        // API
                        .requestMatchers("/api/**").permitAll()

                        // 정적 리소스
                        .requestMatchers(
                                "/assets/**",
                                "/*.png",
                                "/*.jpg",
                                "/*.jpeg",
                                "/*.gif",
                                "/*.svg",
                                "/*.ico",
                                "/*.css",
                                "/*.js"
                        ).permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "https://blocksquarelabs.com",
                "https://www.blocksquarelabs.com",

                "http://localhost:*",
                "http://127.0.0.1:*",

                "http://121.133.55.203:*",
                "https://121.133.55.203:*"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
