package com.example.manual.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
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
        public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomLoginSuccessHandler successHandler,
                        @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled)
                        throws Exception {
                http
                                .authorizeHttpRequests(auth -> {
                                        auth.requestMatchers(
                                                                "/login",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/login/guest",
                                                                "/health")
                                                        .permitAll();
                                        if (h2ConsoleEnabled) {
                                                auth.requestMatchers("/h2-console/**").permitAll();
                                        }
                                        auth.anyRequest().authenticated();
                                })
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
                                                                                + "style-src 'self' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net 'unsafe-inline'; "
                                                                                + "script-src 'self' https://cdn.jsdelivr.net 'unsafe-inline'; "
                                                                                + "img-src 'self' data:; font-src 'self' data: https://cdn.jsdelivr.net; object-src 'none'; frame-ancestors 'self'")));
                if (h2ConsoleEnabled) {
                        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
                }

                return http.build();
        }
}
