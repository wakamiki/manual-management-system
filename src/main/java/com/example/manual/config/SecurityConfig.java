package com.example.manual.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // H2コンソールはログインなしで許可
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                // H2コンソールはCSRF対象外
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                // H2コンソール表示用に frame を許可
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                // デフォルトログイン画面を使う
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}