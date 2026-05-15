package com.blocksquarelabs.config;

import com.blocksquarelabs.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/api/**"))
                )
                .authorizeHttpRequests(auth -> auth
                        // index.html
                        .requestMatchers("/", "/index.html", "/login").permitAll()

                        // API
                        .requestMatchers("/api/**").permitAll()

                        // 정적 리소스
                        .requestMatchers(
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/*.png",
                                "/*.jpg",
                                "/*.jpeg",
                                "/*.gif",
                                "/*.svg",
                                "/*.ico",
                                "/*.css",
                                "/*.js",
                                "/*.json",
                                "/*.ico",
                                "/*.txt",
                                "/*.xml",
                                "/*.webmanifest"
                        ).permitAll()

                        // 관리자 페이지
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 관리자입니다."));

            return User.builder()
                    .username(member.getUsername())
                    .password(member.getPassword())
                    .roles(normalizeRole(member.getRole()))
                    .build();
        };
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "ADMIN";
        }

        // DB에 ROLE_ADMIN으로 저장되어 있어도 ADMIN으로 변환
        if (role.startsWith("ROLE_")) {
            return role.substring(5);
        }

        return role;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
