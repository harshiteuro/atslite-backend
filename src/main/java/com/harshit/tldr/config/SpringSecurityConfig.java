package com.harshit.tldr.config;

import com.harshit.tldr.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // Constructor injection for required dependencies
    @Autowired
    public SpringSecurityConfig(JwtAuthFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        // Disable CSRF (not needed for stateless JWT)
        httpSecurity.cors(Customizer.withDefaults()) //enable cors
                .csrf(csrf -> csrf.disable())

                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/health-check", "/auth/signup", "/auth/login").permitAll()

                        // Role-based endpoints
//                      //✅ Also correct (Spring internally adds "ROLE_" prefix)
                        .requestMatchers("/auth/user/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Stateless session (required for JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before Spring Security's default filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");  // allows all origins (safer than setAllowedOrigins with "*")
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source; // this works because it implements CorsConfigurationSource
    }

    /*
     * Password encoder bean (uses BCrypt hashing)
     * Critical for secure password storage
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Authentication provider configuration
     * Links UserDetailsService and PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /*
     * Authentication manager bean
     * Required for programmatic authentication (e.g., in /generateToken)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
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


//🔹 1. The Request Lifecycle with Spring Security
//Incoming request → passes through the Spring Security filter chain.
//Your JWT filter (or any custom filter) may extract a token and put an Authentication object into the SecurityContextHolder.
//SecurityContextHolder is a static container (per thread via ThreadLocal) that stores the current user’s authentication info for the request.
//If your JWT is valid, you set:
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//Now Spring knows “this request belongs to user X with roles Y,Z”.
//Once filter chain finishes, the request moves into Spring MVC DispatcherServlet and eventually to your controller.
//Yes, Spring Security auto-checks if a URL (or controller method) requires authentication by looking at the SecurityContext
// that your filter populated. If no authentication is present → blocked before hitting the controller.

//➡️ Request comes in
//    |
//v
//[ JWT Filter runs ]
//        |
//        |-- Does request have "Authorization: Bearer <token>"?
//        |       |
//        |       ├── Yes → Validate token
//    |       |        |
//            |       |        ├── Valid → Extract username, roles →
//        |       |        |             Create Authentication → put in SecurityContext
//    |       |        |
//            |       |        └── Invalid/Expired → (optionally return 401)
//        |       |
//        |       └── No → Continue with anonymous user
//    |
//v
//[ Spring Security URL Matchers ]
//        |
//        ├── permitAll() → allowed (even if no auth)
//    |
//            ├── authenticated() → allowed only if SecurityContext has user
//    |
//            └── hasRole("X") → allowed only if SecurityContext has user with that role
//    |
//v
//➡️ Controller executes (or request blocked with 401/403)
