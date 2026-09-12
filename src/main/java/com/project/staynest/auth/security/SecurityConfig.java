package com.project.staynest.auth.security;

import com.project.staynest.auth.security.filter.RefreshTokenFilter;
import com.project.staynest.auth.security.provider.RefreshTokenAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RefreshTokenFilter refreshTokenFilter
    )throws Exception{

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());

//        http.csrf(csrf->
//                csrf.csrfTokenRepository(
//                        new CookieCsrfTokenRepository()
//                )
//        );

        http.addFilterAfter(
                refreshTokenFilter,
                SecurityContextHolderFilter.class
        );

        http.authorizeHttpRequests(
                auth ->
                        auth.requestMatchers(
                                "/v1/auth/sign-up",
                                        "/v1/auth/sign-up/verify-otp",
                                        "/v1/auth/email-login",
                                        "/v1/auth/refresh",
                                        "/oauth/*"
                                ).permitAll()
                                .anyRequest().authenticated()
        );

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            RefreshTokenAuthenticationProvider refreshAuthenticationProvider
    ){
        return new ProviderManager(
                refreshAuthenticationProvider
        );
    }

}