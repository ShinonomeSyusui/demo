package com.example.demo.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	SecurityFilterChain chain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/css/**").permitAll()
				.requestMatchers("/css2/**").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** はADMINロールが必要
				.requestMatchers("/user/**").hasRole("USER") // /user/** はUSERロールが必要
				.requestMatchers("/login", "/signup", "/signupValidation", "/update/password").permitAll() // ログインページを許可
				.anyRequest().authenticated() // 他は認証が必要
		)
				.formLogin(form -> form // フォームログインを有効化
						.loginPage("/login") // カスタムログインページ
						.loginProcessingUrl("/login")// ログイン処理のURL
						.defaultSuccessUrl("/mainPage", true) // ログイン成功時のリダイレクト先
						.permitAll() // ログインページは誰でもアクセス可能
				)
				.logout(logout -> logout // ログアウト設定
						.logoutUrl("/loguot")
						.logoutSuccessUrl("/logout") // ログアウト後のリダイレクト先
				);
		http
				.csrf(csrf -> csrf
						.disable());

		return http.build();
	}

	// メモリ内でユーザーを管理する
	// @Bean
	// public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder)
	// {
	// UserDetails user = User.builder()
	// .username("email") // ユーザー名
	// .password(passwordEncoder.encode("password")) // パスワード（暗号化）
	// .roles("USER") // 権限
	// .build();

	// UserDetails admin = User.builder()
	// .username("email")
	// .password(passwordEncoder.encode("adminofpassword"))
	// .roles("ADMIN")
	// .build();

	// return new InMemoryUserDetailsManager(user, admin);
	// }
}