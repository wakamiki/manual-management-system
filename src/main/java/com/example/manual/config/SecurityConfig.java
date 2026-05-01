package com.example.manual.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomLoginSuccessHandler successHandler)
                        throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/login",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/login/guest",
                                                                "/health")
                                                .permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler(successHandler)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin())
                                                .contentTypeOptions(c -> {
                                                })
                                                .referrerPolicy(r -> r
                                                                .policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                                .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                                                                "geolocation=(), camera=(), microphone=()"))
                                                .addHeaderWriter(new StaticHeadersWriter("Strict-Transport-Security",
                                                                "max-age=31536000"))
                                                .contentSecurityPolicy(csp -> csp.policyDirectives(
                                                                "default-src 'self'; "
                                                                                + "style-src 'self' https://cdnjs.cloudflare.com 'unsafe-inline'; "
                                                                                + "script-src 'self' https://cdn.jsdelivr.net 'unsafe-inline'; "
                                                                                + "img-src 'self' data:; font-src 'self' data:; object-src 'none'; frame-ancestors 'self'")));

                return http.build();
        }
}
