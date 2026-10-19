package com.deepconnect.ats.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.deepconnect.ats.auth.service.LoggedInUserDetailsService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
        "/api/auth/register", "/api/auth/login", "/api/auth/refresh", "/api/auth/activate", "/api/auth/logingoogle", "/error"};

    private final JwtAuthFilter jwtAuthFilter;
    private final LoggedInUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  //for stateless session with Jwt, CSRF is not needed
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            //TODO: for production uncomment the following code block and comment out the above authorizeHttpRequests block
            // .authorizeHttpRequests((requests) -> requests
            //     .requestMatchers(PUBLIC_URLS).permitAll()
            //     //.requestMatchers("/logout").authenticated() 
            //     .requestMatchers("/api/**").authenticated() 
            //     .anyRequest().authenticated()
            // )
            .headers(headers -> 
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                .xssProtection(
                        xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                )
                .contentSecurityPolicy(
                        cps -> cps.policyDirectives("script-src 'self'")
                )
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * CORS configuration to allow requests from specific origins.
     * Since we have specified the following in SecurityConfig.java:
     * .cors(Customizer.withDefaults())
     * In absense of a custom CorsConfigurationSource, spring boot uses the default CORS configuration provided by Spring Boot.
     * This default configuration allows all origins, all headers, and all methods. That is not what we want.
     * Therefore we define our own CORS configuration below to restrict it to specific origins.
     * Note that CORS only works for browsers, not for other clients like Postman, mobile apps or other backend apps.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        //config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.addAllowedOrigin("http://localhost:3001");     // local React dev server
        //config.addAllowedOrigin("https://another-frontend.com"); // add more if needed
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static boolean isPublicUrl(String requestURI) {
        return Arrays.asList(SecurityConfig.PUBLIC_URLS).stream().anyMatch(requestURI::startsWith);
    }
}
