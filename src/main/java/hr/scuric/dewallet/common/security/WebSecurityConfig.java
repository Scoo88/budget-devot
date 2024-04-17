package hr.scuric.dewallet.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final String[] SWAGGER_LIST = {"/docs", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/swagger-resources", "/v3/api-docs/**", "/proxy/**", "/api/v1/budget/client/registration"};
    private final String[] PERMIT_ALL_LIST = {"/api/v1/budget/registration"};
    private final String[] AUTHENTICATE_FIRST_LIST = {"/api/v1/budget/client*", "/api/v1/budget/category*/**", "/api/v1/budget/expense*/**"};

    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService());
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(this.authenticationProvider());

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(this.PERMIT_ALL_LIST).permitAll()
                        .requestMatchers(this.SWAGGER_LIST).permitAll()
                        .requestMatchers(this.AUTHENTICATE_FIRST_LIST).authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/budget/client").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(login ->
                        login.usernameParameter("email")
                                .defaultSuccessUrl("/api/v1/budget/client")
                                .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll()
                );

        return http.build();
    }
}
