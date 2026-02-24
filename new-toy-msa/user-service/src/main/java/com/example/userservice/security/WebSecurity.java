package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final Environment env;
    private final UserService userService;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception{
        http.csrf((csrf)->csrf.disable())
                .authorizeHttpRequests(auth->auth.requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .headers((headers)->headers
                        .frameOptions((frameOptions)->frameOptions.sameOrigin()));

        return http.build();
    }
}
