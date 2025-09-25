package com.ase.userservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.ase.userservice.config.TestSecurityConfig;

@WebMvcTest(DeleteUserController.class)
@Import(TestSecurityConfig.class)
@SuppressWarnings("unchecked")
class DeleteUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_USER_ID = "user-id-123";
    private static final String TEST_TOKEN = "test-bearer-token";
    private static final String DELETE_ENDPOINT = "/user/delete/" + TEST_USER_ID;

    private HttpResponse<String> createMockHttpResponse(int statusCode, String body) {
        HttpResponse<String> mockResponse = org.mockito.Mockito.mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn(body);
        return mockResponse;
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Mock HttpClient to handle both token retrieval and user deletion calls
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock successful deletion response
            HttpResponse<String> deleteResponse = createMockHttpResponse(204, "");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            
            // Mock the token retrieval call first, then the delete call
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        // Mock HttpClient to handle both token retrieval and user deletion calls
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock user not found response
            HttpResponse<String> deleteResponse = createMockHttpResponse(404, "User not found");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("User not found"));
        }
    }

    @Test
    void testDeleteUser_TokenRetrievalFailed() throws Exception {
        // Mock HttpClient to fail on token retrieval
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Token retrieval failed"));

            // When & Then - Since the controller doesn't handle exceptions, 
            // Spring will return a 500 error with error details
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Test
    void testDeleteUser_Unauthorized() throws Exception {
        // Mock HttpClient to handle both token retrieval and unauthorized deletion
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock unauthorized response
            HttpResponse<String> deleteResponse = createMockHttpResponse(401, "Unauthorized");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Unauthorized"));
        }
    }

    @Test
    void testDeleteUser_Forbidden() throws Exception {
        // Mock HttpClient to handle both token retrieval and forbidden deletion
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock forbidden response
            HttpResponse<String> deleteResponse = createMockHttpResponse(403, "Forbidden - insufficient permissions");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Forbidden - insufficient permissions"));
        }
    }

    @Test
    void testDeleteUser_BadRequest() throws Exception {
        // Mock HttpClient to handle both token retrieval and bad request on deletion
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock bad request response
            HttpResponse<String> deleteResponse = createMockHttpResponse(400, "Bad request - invalid user ID format");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Bad request - invalid user ID format"));
        }
    }

    @Test
    void testDeleteUser_ServerError() throws Exception {
        // Mock HttpClient to handle both token retrieval and server error on deletion
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock server error response
            HttpResponse<String> deleteResponse = createMockHttpResponse(500, "Internal server error");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete(DELETE_ENDPOINT))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Internal server error"));
        }
    }

    @Test
    void testDeleteUser_WithDifferentUserId() throws Exception {
        // Test with a different user ID to ensure path variable handling works correctly
        String differentUserId = "different-user-id-456";
        
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
            
            // Mock token response
            HttpResponse<String> tokenResponse = createMockHttpResponse(200, "{\"access_token\":\"" + TEST_TOKEN + "\"}");
            // Mock successful deletion response
            HttpResponse<String> deleteResponse = createMockHttpResponse(204, "");

            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(tokenResponse) // First call for token
                    .thenReturn(deleteResponse); // Second call for deletion

            // When & Then
            mockMvc.perform(delete("/user/delete/" + differentUserId))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }
    }
}