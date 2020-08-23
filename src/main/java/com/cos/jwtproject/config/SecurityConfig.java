package com.cos.jwtproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.jwtproject.config.jwt.JwtAuthenticationFilter;
import com.cos.jwtproject.config.jwt.JwtAuthorizationFilter;
import com.cos.jwtproject.repository.UserRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CorsConfig corsConfig;
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.addFilter(corsConfig.corsFilter())
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.formLogin().disable()
				.httpBasic().disable()
				
				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
				.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
				.authorizeRequests()
				.antMatchers("/user/**")
				.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/manager/**")
					.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/admin/**")
					.access("hasRole('ROLE_ADMIN')")
				.anyRequest().permitAll();
	}
}
