package com.financetracker.expense_tracker.config;

import com.financetracker.expense_tracker.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard",true)
                        .permitAll()
                )
                .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                );

        return http.build();
    }
/*
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("test")
                .password(passwordEncoder().encode("test"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
    */

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            com.financetracker.expense_tracker.entity.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };

    }
}
