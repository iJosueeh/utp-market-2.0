package com.utpmarket.utp_market.config;

import com.utpmarket.utp_market.filters.JwtAuthenticationFilter;
import com.utpmarket.utp_market.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Autowired
        private MyUserDetailsService myUserDetailsService;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder.userDetailsService(myUserDetailsService)
                                .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Deshabilitar CSRF ya que usamos JWT (stateless)
                                .csrf(csrf -> csrf.disable())

                                // Configurar política de sesiones como STATELESS (sin sesiones)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Configurar autorización de endpoints
                                .authorizeHttpRequests(auth -> auth
                                                // Rutas públicas que NO requieren autenticación
                                                .requestMatchers("/", "/about-us", "/sedes", "/help",
                                                                "/ventas", "/categoria", "/producto/**",
                                                                "/carrito", "/error/**")
                                                .permitAll()

                                                // Recursos estáticos públicos
                                                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico")
                                                .permitAll()

                                                // Endpoints de autenticación públicos
                                                .requestMatchers("/auth/login", "/auth/register", "/auth/refresh")
                                                .permitAll()

                                                // API pública
                                                .requestMatchers("/api/chatbot/**").permitAll()

                                                // Rutas de Administrador requieren JWT con rol ADMIN
                                                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                                                // Cualquier otra solicitud requiere autenticación con JWT
                                                .anyRequest().authenticated())

                                // Agregar filtro JWT antes del filtro de autenticación estándar
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                                // Manejo de excepciones: usar CustomAuthenticationEntryPoint
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(customAuthenticationEntryPoint));

                return http.build();
        }
}