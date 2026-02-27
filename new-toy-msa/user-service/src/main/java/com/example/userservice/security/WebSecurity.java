package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public static final String ALLOWD_IP_ADDRESS="127.0.0.1";
    public static final String SUBNET="/32";
    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_IP_MATCHER=new IpAddressMatcher(ALLOWD_IP_ADDRESS+SUBNET);

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager manager = builder.build();


        http.csrf((csrf)->csrf.disable())
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/**")
                .access(new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('::1') " +
                        "or hasIpAddress('192.168.219.104')")).anyRequest().authenticated()
            )
            .authenticationManager(manager)
            .addFilter(getAuthenticationFilter(manager))
            .httpBasic(Customizer.withDefaults())
            .headers((headers)->headers.frameOptions((frameOptions)->frameOptions.sameOrigin()));
        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager manager) throws Exception{
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setAuthenticationManager(manager);
        return filter;
    }

}
