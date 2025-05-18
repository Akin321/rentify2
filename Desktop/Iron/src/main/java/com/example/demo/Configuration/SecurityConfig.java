package com.example.demo.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
	@Autowired
	JwtFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(csrf->csrf.disable())
		.authorizeHttpRequests(request->request.requestMatchers("admin/login","/css/**", "/v3/api-docs/**","/images/**",
                "/swagger-ui/**",
                "/swagger-ui.html","admin-rest/login").permitAll()
				.requestMatchers("/admin/view-user/**","/admin-rest/view-user/**","/admin/block-user/**","/admin-rest/block-user/**",
						"/admin-rest/add-user","/admin-rest/add-productTypes").hasAuthority("Admin")
				.requestMatchers("admin/view-productTypes/**","admin/add-productTypes","admin/view-productType/**").permitAll()
				.anyRequest().permitAll())
		.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 is the strength factor
    }
	
	@Bean
		public AuthenticationProvider authenticationProvidet(UserDetailsService userDetailsService) {
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
	
}
