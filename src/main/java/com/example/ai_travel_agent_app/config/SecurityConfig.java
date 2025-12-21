package com.example.ai_travel_agent_app.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;



@Component
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;


    private static String api = "";
    private static final String[] WHITE_LIST_URLS = {
            api +"/hello",
            api +"/register",
            api +"/verifyRegistration",
            api +"/resendVerifyToken",
            api + "/resetPassword",
            api + "/login",
            api +"/refresh-token",
            "/test",
            "/files/**",
            api + "/public/**",
            "categories",
            api + "/services",
            // Travel Agent endpoints
            "/",
            "/ask-ajax",
            "/travel/**",
            "/api/bookings/**",
            // Customer Agent endpoints
            "/customer-agent/**",
            "/customer/ask",
            // Static resources
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .requestMatchers("/customer/ask").permitAll() // Allow both anonymous and authenticated access to chat
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/worker/**").hasAnyAuthority("WORKER")
                        .requestMatchers("/customer/bookings/**").hasAnyAuthority("CUSTOMER") // Bookings require authentication
                        .requestMatchers("/customer/**").hasAnyAuthority("CUSTOMER")
                        .requestMatchers("/api/public/**").permitAll() // Cho phép tất cả public APIs
                        .requestMatchers(HttpMethod.GET, "/api/articles/published").permitAll() // Backup
                        .requestMatchers(HttpMethod.GET, "/api/article-categories").permitAll() // Backup
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll() // Backup cho endpoint cũ

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("🚫 Access Denied for path: " + request.getRequestURI());
                            System.out.println("🚫 Required authority: ADMIN");
                            
                            // Debug current authentication
                            org.springframework.security.core.Authentication auth = 
                                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                            if (auth != null) {
                                System.out.println("🚫 Current principal: " + auth.getName());
                                System.out.println("🚫 Current authorities: " + auth.getAuthorities());
                                System.out.println("🚫 Is authenticated: " + auth.isAuthenticated());
                            } else {
                                System.out.println("🚫 No authentication found");
                            }
                            
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"You do not have permission to access this resource.\", \"status\":1}");
                        })
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // or explicitly specify the allowed origins
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
