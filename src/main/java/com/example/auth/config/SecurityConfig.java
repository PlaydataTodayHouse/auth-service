package com.example.auth.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain
        securityFilterChain(HttpSecurity security) throws Exception {
        security.csrf(AbstractHttpConfigurer::disable);
        security.sessionManagement(
                configurer -> configurer
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS));
        security.authenticationProvider(authenticationProvider);
        security.addFilterBefore(jwtAuthenticationFilter
                , UsernamePasswordAuthenticationFilter.class);
        security.authorizeHttpRequests(req ->
                req.requestMatchers(
                        AntPathRequestMatcher.antMatcher("/api/v1/auth/login")
                       ,AntPathRequestMatcher.antMatcher("/api/v1/auth/signup")
                       ,AntPathRequestMatcher.antMatcher("/api/v1/auth/refresh")
                       ,AntPathRequestMatcher.antMatcher("/api/v1/auth/findUser/**")
                )
                        .permitAll()
                        .anyRequest().authenticated()
        );
        return security.build();

    }
}
