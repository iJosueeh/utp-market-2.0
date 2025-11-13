package com.utpmarket.utp_market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // Rutas Públicas (para visitantes y usuarios autenticados)
                                .requestMatchers("/", "/about-us", "/sedes", "/help", "/ventas", "/categoria", "/producto/**").permitAll()
                                .requestMatchers("/auth/register", "/auth/forgot-password").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()

                                // Rutas que requieren autenticación
                                .requestMatchers("/perfil").authenticated()
                                .requestMatchers("/usuario/**").authenticated() // Para actualizar perfil
                                .requestMatchers("/reviews/**").authenticated() // Para crear, actualizar, eliminar reviews
                                .requestMatchers("/carrito/**").authenticated() // Todas las operaciones del carrito
                                .requestMatchers("/pedidos/**").authenticated() // Historial y detalle de pedidos
                                .requestMatchers("/cita/enviar").authenticated() // Enviar mensaje de contacto

                                // Rutas de Administrador
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().permitAll()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/auth/login")
                                .usernameParameter("email")
                                .loginProcessingUrl("/auth/login")
                                .defaultSuccessUrl("/", true)
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/auth/logout")
                                .logoutSuccessUrl("/auth/login?logout")
                                .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
