package com.ase.userservice.controllers;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ase.userservice.components.GetToken;
import com.ase.userservice.components.UserManagment;
import com.ase.userservice.config.TestSecurityConfig;
import com.ase.userservice.entities.CredentialRepresentation;
import com.ase.userservice.entities.NewUserRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CreateUserController.class)
@Import(TestSecurityConfig.class)
class CreateUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetToken getToken;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    private NewUserRepresentation createTestUser() {
        NewUserRepresentation user = new NewUserRepresentation();
        user.firstName = "Test";
        user.lastName = "User";
        user.email = "test@example.com";
        user.groups = new ArrayList<>(Arrays.asList("testgroup"));
        user.enabled = true;
        user.requiredActions = new ArrayList<>(Arrays.asList("UPDATE_PASSWORD"));
        user.credentials = new CredentialRepresentation[]{new CredentialRepresentation()};
        // Set a fixed password for testing
        user.credentials[0].value = "testpassword123";
        return user;
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> createMockHttpResponse(int statusCode, String body) {
        HttpResponse<String> mockResponse = org.mockito.Mockito.mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn(body);
        return mockResponse;
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        NewUserRepresentation testUser = createTestUser();
        String testToken = "test-token-123";
        String userDataResponse = "[{\"id\":\"user-id-123\",\"username\":\"testuser\"}]";
        
        // Mock GetToken
        when(getToken.getToken()).thenReturn(testToken);
        
        // Mock UserManagement static methods
        try (MockedStatic<UserManagment> mockedUserManagement = mockStatic(UserManagment.class)) {
            // Mock successful user creation (201 status)
            HttpResponse<String> createResponse = createMockHttpResponse(201, "");
            mockedUserManagement.when(() -> UserManagment.createUserfromJson(anyString(), anyString()))
                    .thenReturn(createResponse);
            
            // Mock successful user data retrieval
            HttpResponse<String> getUserResponse = createMockHttpResponse(200, userDataResponse);
            mockedUserManagement.when(() -> UserManagment.getUserDatafromUsername(anyString(), anyString()))
                    .thenReturn(getUserResponse);
            
            // When & Then
            mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(userDataResponse + "\n\"init-password\": \"testpassword123\""));
        }
    }

    @Test
    void testCreateUser_CreationFailed() throws Exception {
        // Given
        NewUserRepresentation testUser = createTestUser();
        String testToken = "test-token-123";
        String errorResponse = "User creation failed";
        
        // Mock GetToken
        when(getToken.getToken()).thenReturn(testToken);
        
        // Mock UserManagement static methods
        try (MockedStatic<UserManagment> mockedUserManagement = mockStatic(UserManagment.class)) {
            // Mock failed user creation (400 status)
            HttpResponse<String> createResponse = createMockHttpResponse(400, errorResponse);
            mockedUserManagement.when(() -> UserManagment.createUserfromJson(anyString(), anyString()))
                    .thenReturn(createResponse);
            
            // When & Then
            mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(errorResponse));
        }
    }

    @Test
    void testCreateUser_TokenRetrievalFailed() throws Exception {
        // Given
        NewUserRepresentation testUser = createTestUser();
        
        // Mock GetToken to throw exception
        when(getToken.getToken()).thenThrow(new RuntimeException("Token retrieval failed"));
        
        // When & Then
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateUser_UserDataRetrievalFailed() throws Exception {
        // Given
        NewUserRepresentation testUser = createTestUser();
        String testToken = "test-token-123";
        String errorResponse = "User not found";
        
        // Mock GetToken
        when(getToken.getToken()).thenReturn(testToken);
        
        // Mock UserManagement static methods
        try (MockedStatic<UserManagment> mockedUserManagement = mockStatic(UserManagment.class)) {
            // Mock successful user creation (201 status)
            HttpResponse<String> createResponse = createMockHttpResponse(201, "");
            mockedUserManagement.when(() -> UserManagment.createUserfromJson(anyString(), anyString()))
                    .thenReturn(createResponse);
            
            // Mock failed user data retrieval (404 status)
            HttpResponse<String> getUserResponse = createMockHttpResponse(404, errorResponse);
            mockedUserManagement.when(() -> UserManagment.getUserDatafromUsername(anyString(), anyString()))
                    .thenReturn(getUserResponse);
            
            // When & Then
            mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(errorResponse + "\n\"init-password\": \"testpassword123\""));
        }
    }

    @Test
    void testCreateUser_InvalidJsonInput() throws Exception {
        // When & Then
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}