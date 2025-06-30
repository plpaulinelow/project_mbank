package com.mbank.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Class to handle security configurations
 */
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
	
	private final SecurityUserProperties securityUserProperties;

	 public SecurityConfig(SecurityUserProperties securityUserProperties) {
	        this.securityUserProperties = securityUserProperties;
	    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	 http
         .csrf().disable()
         .authorizeHttpRequests((authz) -> authz
             .anyRequest().authenticated()
         )
         .httpBasic();
     return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername(securityUserProperties.getUsername() )
                .password(securityUserProperties.getPassword())
                .roles(securityUserProperties.getRoles())
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
