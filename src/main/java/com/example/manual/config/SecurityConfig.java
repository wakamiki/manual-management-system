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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // 部品系Bean
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        // 設定系Bean
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomLoginSuccessHandler successHandler)
                        throws Exception {
                http
                                // ログインなしで許可
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/h2-console/**",
                                                                "/login",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/login/guest")
                                                .permitAll()
                                                .anyRequest()
                                                .authenticated())
                                // ログイン遷移設定
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler(successHandler)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                // ログアウト設定
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                // H2コンソールはCSRF対象外
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**"))
                                // H2コンソール表示用に frame を許可
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }
}