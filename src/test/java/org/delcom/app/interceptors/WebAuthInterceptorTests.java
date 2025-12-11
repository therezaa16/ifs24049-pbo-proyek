package org.delcom.app.interceptors;

import java.util.UUID;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class WebAuthInterceptorTest {

    @Mock
    private AuthContext authContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private WebAuthInterceptor webAuthInterceptor;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Setup SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
        
        // Setup mock user
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
    }

    @Test
    void preHandle_WhenUserIsAuthenticated_ShouldSetAuthUserInContext() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        // Act
        boolean result = webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(authContext, times(1)).setAuthUser(mockUser);
    }

    @Test
    void preHandle_WhenAuthenticationIsNull_ShouldNotSetAuthUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean result = webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(authContext, never()).setAuthUser(any());
    }

    @Test
    void preHandle_WhenUserIsNotAuthenticated_ShouldNotSetAuthUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        boolean result = webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(authContext, never()).setAuthUser(any());
    }

    @Test
    void preHandle_WhenPrincipalIsNotUserObject_ShouldNotSetAuthUser() {
        // Arrange
        String principalString = "anonymousUser";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(principalString);

        // Act
        boolean result = webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(authContext, never()).setAuthUser(any());
    }

    @Test
    void preHandle_WhenAuthenticationExistsButNotAuthenticated_ShouldNotSetAuthUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        boolean result = webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(authContext, never()).setAuthUser(any());
    }

    @Test
    void preHandle_ShouldAlwaysReturnTrue() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean result = webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result, "preHandle should always return true to allow request to continue");
    }

    @Test
    void preHandle_WithValidUserAuthentication_ShouldExtractUserCorrectly() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("Admin User");
        expectedUser.setEmail("admin@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(expectedUser);

        // Act
        webAuthInterceptor.preHandle(request, response, new Object());

        // Assert
        verify(authContext).setAuthUser(argThat(user -> 
            user.getId().equals(userId) && 
            user.getName().equals("Admin User") &&
            user.getEmail().equals("admin@example.com")
        ));
    }
}