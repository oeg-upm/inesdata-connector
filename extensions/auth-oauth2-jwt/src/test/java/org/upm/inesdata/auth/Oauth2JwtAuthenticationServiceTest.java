package org.upm.inesdata.auth;

import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.web.spi.exception.AuthenticationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.HttpHeaders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Oauth2JwtAuthenticationServiceTest {

    private static final String TEST_CONNECTOR_ID = "connector-id";
    private static final List<String> TEST_ALLOWED_ROLES = Arrays.asList("admin", "manager");
    private final IdentityService identityService = mock();
    private Oauth2JwtAuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new Oauth2JwtAuthenticationService(identityService, TEST_CONNECTOR_ID, TEST_ALLOWED_ROLES);
    }

    @Test
    void isAuthenticated_headerNotPresent_throwsException() {
        var map = Map.of("header1", List.of("val1, val2"),
                         "header2", List.of("anotherval1", "anotherval2"));
        assertThatThrownBy(() -> service.isAuthenticated(map)).isInstanceOf(AuthenticationFailedException.class).hasMessage(HttpHeaders.AUTHORIZATION + " header not found");
    }

    @Test
    void isAuthenticated_headersEmpty_throwsException() {
        Map<String, List<String>> map = Collections.emptyMap();
        assertThatThrownBy(() -> service.isAuthenticated(map)).isInstanceOf(AuthenticationFailedException.class).hasMessage(HttpHeaders.AUTHORIZATION + " header not found");
    }

    @Test
    void isAuthenticated_headersNull_throwsException() {
        assertThatThrownBy(() -> service.isAuthenticated(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void isAuthenticated_authorizationNoBearerToken() {
        var map = Map.of(HttpHeaders.AUTHORIZATION, List.of("no-bearer-token"));
        assertThatThrownBy(() -> service.isAuthenticated(map)).isInstanceOf(AuthenticationFailedException.class).hasMessage("Bearer token not found");
    }

    @Test
    void iisAuthenticated_correctAuthorized() {
        var map = Map.of(HttpHeaders.AUTHORIZATION, List.of("Bearer valid-token-key"));

        // Mock verification token result
        List<String> roles = Arrays.asList("user", "manager", "connector-id");
        Map<String, List<String>> rolesClaim = new HashMap<>();
        rolesClaim.put("roles", roles);
        var tokenBuilder = ClaimToken.Builder.newInstance();
        tokenBuilder.claim("realm_access", rolesClaim);
        var claimToken = tokenBuilder.build();
        
        when(identityService.verifyJwtToken(any(TokenRepresentation.class), isNull())).thenReturn(Result.success(claimToken));

        assertThat(service.isAuthenticated(map)).isTrue();
    }

    @Test
    void isAuthenticated_incorrectConnectorIdAuthorized() {
        var map = Map.of(HttpHeaders.AUTHORIZATION, List.of("Bearer invalid-token-key"));

        // Mock verification token result
        List<String> roles = Arrays.asList("user", "manager", "connector-id-2");
        Map<String, List<String>> rolesClaim = new HashMap<>();
        rolesClaim.put("roles", roles);
        var tokenBuilder = ClaimToken.Builder.newInstance();
        tokenBuilder.claim("realm_access", rolesClaim);
        var claimToken = tokenBuilder.build();
        
        when(identityService.verifyJwtToken(any(TokenRepresentation.class), isNull())).thenReturn(Result.success(claimToken));

        assertThat(service.isAuthenticated(map)).isFalse();
    }

    @Test
    void isAuthenticated_incorrectRolesAuthorized() {
        var map = Map.of(HttpHeaders.AUTHORIZATION, List.of("Bearer invalid-token-key"));

        // Mock verification token result
        List<String> roles = Arrays.asList("user", "connector-id");
        Map<String, List<String>> rolesClaim = new HashMap<>();
        rolesClaim.put("roles", roles);
        var tokenBuilder = ClaimToken.Builder.newInstance();
        tokenBuilder.claim("realm_access", rolesClaim);
        var claimToken = tokenBuilder.build();
        
        when(identityService.verifyJwtToken(any(TokenRepresentation.class), isNull())).thenReturn(Result.success(claimToken));

        assertThat(service.isAuthenticated(map)).isFalse();
    }

    @Test
    void isAuthenticated_badTokenValidation() {
        var map = Map.of(HttpHeaders.AUTHORIZATION, List.of("Bearer invalid-token-key"));

        when(identityService.verifyJwtToken(any(TokenRepresentation.class), isNull())).thenReturn(Result.failure("Expired token"));

        assertThat(service.isAuthenticated(map)).isFalse();
    }
}