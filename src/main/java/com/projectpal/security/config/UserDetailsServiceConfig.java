package com.projectpal.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.projectpal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

	private final UserRepository userRepo;

	@Bean
	UserDetailsService userDetailsService() {

		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

				return (UserDetails) userRepo.findUserByEmail(email)
						.orElseThrow(() -> new UsernameNotFoundException("user not found"));
			}
		};
	}
}
