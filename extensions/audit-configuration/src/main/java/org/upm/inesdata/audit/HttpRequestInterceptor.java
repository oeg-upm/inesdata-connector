package org.upm.inesdata.audit;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;

import java.text.MessageFormat;
import java.util.List;

/**
 * Intercepts HTTP requests to audit user actions.
 * Logs details about the user and the request URI.
 */
@Provider
public class HttpRequestInterceptor implements ContainerRequestFilter {

    private static final String TEMPLATE_AUDIT_LOG = "[AUDIT][{0}][MANAGEMENT] User ''{1}'' calls ''{2}''";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_PROPERTY_PREFERRED_USERNAME = "preferred_username";
    private static final String TOKEN_USER_NOT_VALID_USER = "NotValidUser";
    private static final String TOKEN_USER_NOT_AUTHENTICATED_USER = "NotAuthenticatedUser";

    private final Monitor monitor;
    private final IdentityService identityService;
    private final String participantId;

    /**
     * Constructor for HttpRequestInterceptor.
     *
     * @param monitor        the monitor interface used for logging
     * @param identityService the identity service for token verification
     * @param participantId  the participant ID for audit log entries
     */
    public HttpRequestInterceptor(Monitor monitor, IdentityService identityService, String participantId) {
        this.monitor = monitor;
        this.identityService = identityService;
        this.participantId = participantId;
    }

    /**
     * Filters the HTTP request context to log audit details.
     *
     * @param requestContext the container request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String user = getUserFromRequest(requestContext);
        String auditLog = MessageFormat.format(TEMPLATE_AUDIT_LOG, participantId, user, requestContext.getUriInfo().getRequestUri().toString());
        monitor.info(auditLog);
    }

    /**
     * Extracts the user from the HTTP request context by decoding the JWT token.
     *
     * @param requestContext the container request context
     * @return the username extracted from the token, or a default value if not authenticated or token is invalid
     */
    private String getUserFromRequest(ContainerRequestContext requestContext) {
        List<String> authorizationHeaders = requestContext.getHeaders().get("Authorization");
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authHeader = authorizationHeaders.get(0);
            if (authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.replace(BEARER_PREFIX, "");
                return decodeJWT(token);
            }
        }
        return TOKEN_USER_NOT_AUTHENTICATED_USER;
    }

    /**
     * Decodes the JWT token to extract the username.
     *
     * @param token the JWT token
     * @return the username if token is valid, otherwise a default value indicating the token is not valid
     */
    private String decodeJWT(String token) {
        var tokenRepresentation = TokenRepresentation.Builder.newInstance().token(token).build();
        var tokenValidation = identityService.verifyJwtToken(tokenRepresentation, null);
        if (tokenValidation.failed()) {
            return TOKEN_USER_NOT_VALID_USER;
        }
        return (String) tokenValidation.getContent().getClaims().get(TOKEN_PROPERTY_PREFERRED_USERNAME);
    }
}
