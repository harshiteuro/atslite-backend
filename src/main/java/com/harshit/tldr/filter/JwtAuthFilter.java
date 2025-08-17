package com.harshit.tldr.filter;

import com.harshit.tldr.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(token);
            } catch (ExpiredJwtException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

// ✅ Why add a custom JWT filter?
// Spring Security does not automatically handle JWTs.
//
// Your jwtAuthFilter is needed to:
// - Extract the JWT from the request header.
// - Validate the token.
// - Set the authenticated user in SecurityContext.
//
// Without it, Spring won't recognize the token or authenticate the user.

// ✅ What does UsernamePasswordAuthenticationFilter do?
// It's a default Spring Security filter for form-based login.
//
// It:
// - Reads username and password from request.
// - Calls AuthenticationManager.
// - AuthenticationManager uses your UserDetailsService and PasswordEncoder to check DB credentials.
// - If valid, the user is authenticated.
//
// ➡️ Used only during login, not for token-based authentication.

// ✅ When is UserDetailsService called?
// It is used by AuthenticationProvider during login.
//
// It:
// - Loads user data from the DB.
// - Returns a UserDetails object for Spring Security.
// - Password is compared using the PasswordEncoder.

// ✅ Why use addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)?
// - Ensures your jwtAuthFilter runs before the default UsernamePasswordAuthenticationFilter.
// - jwtAuthFilter checks for a valid JWT in the request.
// - If valid, it sets the Authentication object manually.
// - This prevents Spring from treating the request as unauthenticated and invoking the login filter.
//
// ➡️ This order is crucial: JWT-based authentication must occur before Spring tries to apply its own authentication logic.
//
// ✅ Why do we use SecurityContextHolder.getContext().setAuthentication(authToken)?
// - Spring Security uses SecurityContextHolder to store security information (like the logged-in user) per request thread.
// - Setting the Authentication object manually tells Spring: "This request is from an authenticated user."
// - Once set, Spring uses this information to authorize access to protected endpoints.
//
// ➡️ Without setting this, even a valid JWT will not make the request authenticated in Spring's eyes.
//
// ✅ Why is setting SecurityContextHolder necessary even with a valid JWT?
// - Spring Security relies entirely on the SecurityContext (via SecurityContextHolder) to determine authentication.
// - If you don't explicitly set the Authentication object using SecurityContextHolder, Spring has no idea who the user is.
// - A JWT might be valid, but unless you tell Spring "this is the authenticated user", it will still treat the request as anonymous.
// - That's why setting authToken in the context is essential for any protected endpoint to be accessible.