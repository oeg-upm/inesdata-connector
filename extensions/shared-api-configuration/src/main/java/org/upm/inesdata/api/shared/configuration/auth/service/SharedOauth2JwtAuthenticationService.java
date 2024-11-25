package org.upm.inesdata.api.shared.configuration.auth.service;

import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.edc.api.auth.spi.AuthenticationService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.web.spi.exception.AuthenticationFailedException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SharedAuthenticationService for access control based on JWT tokens
 */
public class SharedOauth2JwtAuthenticationService implements AuthenticationService {
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String AUTHORIZATION_HEADER_ERROR = HttpHeaders.AUTHORIZATION + " header not found";
    private static final String BEARER_PREFIX_ERROR = "Bearer token not found";


    private final IdentityService identityService;

    public SharedOauth2JwtAuthenticationService(IdentityService identityService) {
        this.identityService = identityService;
    }

    /**
     * Checks whether a particular request is authorized based on the "Authorization" header.
     *   The header must contains a valid JWT token
     *
     * @param headers the headers
     * @throws IllegalArgumentException The map of headers did not contain the "Authorization" header
     * @return whether it is a valid JWT token or not
     */
    @Override
    public boolean isAuthenticated(Map<String, List<String>> headers) {

        Objects.requireNonNull(headers, "headers");

        // Get the Authorization header
        var apiKey = headers.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                .map(headers::get)
                .findFirst();

        return apiKey.map(this::checkTokenValid).orElseThrow(() -> new AuthenticationFailedException(AUTHORIZATION_HEADER_ERROR));
    }

    private boolean checkTokenValid(List<String> tokenKeys) {
        return tokenKeys.size() == 1 && tokenKeys.stream().allMatch(this::isJwtTokenValid);
    }

    @SuppressWarnings("unchecked")
    private boolean isJwtTokenValid(String jwtToken) {
        boolean valid = false;

        if (!jwtToken.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationFailedException(BEARER_PREFIX_ERROR);
        }

        var tokenRepresentation = TokenRepresentation.Builder.newInstance().token(jwtToken.replace(BEARER_PREFIX, "")).build();
        var tokenValidation = identityService.verifyJwtToken(tokenRepresentation, null);
        valid = tokenValidation.succeeded();

        return valid;
    }
}
