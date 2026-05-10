package com.softdesign.tourney.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AppConfig {

    // ── Auth datasource (auth_db) — used only by CustomUserDetailsService ───────

    @Value("${auth.datasource.url}")
    private String authUrl;

    @Value("${auth.datasource.username}")
    private String authUsername;

    @Value("${auth.datasource.password}")
    private String authPassword;

    @Bean
    @Qualifier("authDataSource")
    public DataSource authDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(authUrl);
        ds.setUsername(authUsername);
        ds.setPassword(authPassword);
        return ds;
    }

    // ── Security ─────────────────────────────────────────────────────────────────

    @Bean
    @Primary
    public DataSource tournamentDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/tournaments").authenticated()
                        .requestMatchers("/tournaments/*/join", "/tournaments/*/leave").hasAuthority("MANAGER")
                        .requestMatchers("/tournaments/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/tournaments", true)
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}