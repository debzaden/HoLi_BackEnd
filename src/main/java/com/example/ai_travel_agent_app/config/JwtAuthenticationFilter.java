package com.example.ai_travel_agent_app.config;


import com.example.ai_travel_agent_app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userEmail = jwtService.extractUsername(jwt);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Token đã hết hạn
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"token đã hết hạn\", \n \"status\":1}");
                return;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                // Token sai format
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token sai format\", \n  \"status\": 2}");
                return;
            } catch (Exception e) {
                // Lỗi khác
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token không chính xác\", \n \"status\": 3}");
                return;
            }

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                System.out.println("🔍 JWT Debug - UserEmail: " + userEmail);
                System.out.println("🔍 JWT Debug - UserDetails: " + userDetails.getUsername());
                System.out.println("🔍 JWT Debug - Authorities: " + userDetails.getAuthorities());
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ JWT Debug - Authentication set successfully");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token không chính xác\", \n  \"status\": 3}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
