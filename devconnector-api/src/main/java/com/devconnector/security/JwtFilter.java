package com.devconnector.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String path = request.getServletPath();
        System.out.println("Path: " + path + " | Header: " + authHeader);

        // 1. FAST TRACK: Skip processing for public routes
        if (path.equals("/api/auth/login") || (path.equals("/api/users") && request.getMethod().equals("POST"))) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. ROBUST HEADER CHECK: Handle "null" or "undefined" strings from React
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.contains("null") || authHeader.contains("undefined")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtils.validateToken(token)) { // Added explicit validation check
                String email = jwtUtils.getEmailFromToken(token);
                System.out.println("✅ Extracted Email: " + email);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 3. SET CONTEXT with proper authorities if needed
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ Security Context Set for: " + email);
                }
            }
        } catch (Exception e) {
            // 4. CLEAR CONTEXT on failure to ensure it doesn't stay as a partial session
            SecurityContextHolder.clearContext();
            System.out.println("❌ JWT Validation Failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}