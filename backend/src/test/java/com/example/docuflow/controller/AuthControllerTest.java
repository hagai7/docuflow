package com.example.docuflow.controller;

import com.example.docuflow.dto.AuthRequest;
import com.example.docuflow.dto.AuthResponse;
import com.example.docuflow.security.JwtUtil;
import com.example.docuflow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthenticationManager authManager;
    private JwtUtil jwtUtil;
    private UserService userService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        userService = mock(UserService.class);

        authController = new AuthController(authManager, jwtUtil, userService);
    }

    @Test
    void testRegister_success() {
        AuthRequest request = new AuthRequest("newuser", "password123");

        ResponseEntity<AuthResponse> response = authController.register(request);

        // Verify service call
        verify(userService, times(1)).registerUser("newuser", "password123");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody().message());

        // register, not login, so there shouldn't be token (in backend level)
        assertNull(response.getBody().token());
    }

    @Test
    void testLogin_success() {
        AuthRequest request = new AuthRequest("existinguser", "password456");

        // tells mock authManager:
        // "When authenticate(...) is called with any UsernamePasswordAuthenticationToken, do nothing, don't throw exceptions..."
        // I'm only testing my controller’s behavior, not whether authentication (spring internal) actually works
        doNothing().when(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(jwtUtil.generateToken("existinguser")).thenReturn("mock-jwt-token");

        ResponseEntity<AuthResponse> response = authController.login(request);

        // creates a "net" that will capture any argument of type UsernamePasswordAuthenticationToken passed to the mocked method
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        // check if authManager.authenticate() was called
        // and captures (into captor) the argument that was passed to it
        verify(authManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken authToken = captor.getValue();
        assertEquals("existinguser", authToken.getPrincipal());
        assertEquals("password456", authToken.getCredentials());

        // Verify JWT token generation
        verify(jwtUtil).generateToken("existinguser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody().message());
        assertEquals("mock-jwt-token", response.getBody().token());
    }
}
