package com.bookstore.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        
        System.out.println("User logged in: " + authentication.getName() + " with roles: " + roles);
        
        if (roles.contains("ROLE_ADMIN")) {
            System.out.println("Redirecting admin to dashboard");
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            System.out.println("Redirecting user to home");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
} 