package com.devconnector.config;

import com.devconnector.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Fixed "SessionPolicy" error
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Fixed "JwtFilter" position error

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1. Enable CORS and Disable CSRF
				.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())

				// 2. Stateless Session (for JWT)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 3. Request Authorizations
				.authorizeHttpRequests(auth -> auth
						// PUBLIC: Auth & User Registration
						
						.requestMatchers("/uploads/**", "/avatars/**", "/static/**").permitAll()
					    .requestMatchers(HttpMethod.GET, "/*.png", "/*.jpg", "/*.jpeg", "/*.gif").permitAll()
					    .requestMatchers(HttpMethod.POST, "/api/profile/upload").authenticated()
					    .requestMatchers(HttpMethod.POST, "/api/profile/avatar").authenticated()

					    .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/api/auth/**")
						.permitAll().requestMatchers(HttpMethod.POST, "/api/users").permitAll()

						// Unrestricted viewing
						.requestMatchers(HttpMethod.GET, "/api/profile").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/profile/user/**").permitAll()

						// Locked down for logged-in users
						.requestMatchers("/api/profile/me").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/profile").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/profile/**").authenticated() // For experience/education
						.requestMatchers(HttpMethod.DELETE, "/api/profile/**").authenticated()

						// PUBLIC: Posts (Viewing feed)
						.requestMatchers(HttpMethod.GET, "/api/posts").permitAll()

						// PRIVATE: Everything else requires a token
						.anyRequest().authenticated())

				// 4. JWT Filter position
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// This creates the "Bean" that UserService is looking for
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}