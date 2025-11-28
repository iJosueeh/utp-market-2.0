package com.utpmarket.utp_market.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utpmarket.utp_market.models.dto.auth.LoginRequest;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.AuthService;
import com.utpmarket.utp_market.services.MyUserDetailsService;
import com.utpmarket.utp_market.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for this test to focus on controller logic
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MyUserDetailsService userDetailsService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSetHttpOnlyCookieOnLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@utp.edu.pe");
        loginRequest.setPassword("password");

        UserDetails userDetails = new User("test@utp.edu.pe", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ESTUDIANTE")));
        Usuario usuario = new Usuario();
        usuario.setEmail("test@utp.edu.pe");
        usuario.setNombre("Test");
        usuario.setApellido("User");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
        when(userDetailsService.loadUserByUsername("test@utp.edu.pe")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("dummy-token");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("dummy-refresh-token");
        when(jwtUtil.getExpiration()).thenReturn(3600000L);
        when(usuarioRepository.findByEmail("test@utp.edu.pe")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().value("accessToken", "dummy-token"));
    }
}
