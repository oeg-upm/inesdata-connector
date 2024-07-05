package org.upm.inesdata.audit;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.monitor.Monitor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Provider
public class HttpRequestInterceptor implements ContainerRequestFilter {

    private static final String TEMPLATE_AUDIT_LOG = "[AUDIT][''{0}''][MANAGEMENT] User ''{1}'' calls ''{2}''";

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REALM_ACCESS_CLAIM_NAME = "realm_access";
    private static final String ROLES_NAME = "roles";
    public static final String TOKEN_PROPERTY_PREFERRED_USERNAME = "preferred_username";

    private final Monitor monitor;
    private final IdentityService identityService;

    private final String participantId;

    public HttpRequestInterceptor(Monitor monitor, IdentityService identityService, String participantId) {
        this.monitor = monitor;
        this.identityService = identityService;
        this.participantId = participantId;
    }

    @Override
    public void filter(ContainerRequestContext requestContext){
        String user = getUserFromRequest(requestContext);
        String auditLog = MessageFormat.format(TEMPLATE_AUDIT_LOG, participantId,
            user,
            requestContext.getUriInfo().getRequestUri().toString());
        monitor.info(auditLog);
    }

    private String getUserFromRequest(ContainerRequestContext requestContext) {
        List<String> authorizationHeaders = requestContext.getHeaders().get("Authorization");
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authHeader = authorizationHeaders.get(0);
            if (authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.replace(BEARER_PREFIX, "");
                return decodeJWT(token);
            }
        }
        return "NotAuthenticatedUser";
    }

    private String decodeJWT(String token) {
        var tokenRepresentation = TokenRepresentation.Builder.newInstance().token(token).build();
        var tokenValidation = identityService.verifyJwtToken(tokenRepresentation, null);
        return (String) tokenValidation.getContent().getClaims().get(TOKEN_PROPERTY_PREFERRED_USERNAME);
    }
}
