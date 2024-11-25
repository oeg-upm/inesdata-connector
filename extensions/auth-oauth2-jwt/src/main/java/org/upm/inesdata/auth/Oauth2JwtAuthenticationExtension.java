package org.upm.inesdata.auth;

import org.eclipse.edc.api.auth.spi.AuthenticationService;
import org.eclipse.edc.api.auth.spi.registry.ApiAuthenticationRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Extension that registers an AuthenticationService that uses Oauth2 JWT Tokens
 */
@Provides(AuthenticationService.class)
@Extension(value = Oauth2JwtAuthenticationExtension.NAME)
public class Oauth2JwtAuthenticationExtension implements ServiceExtension {

    public static final String NAME = "Oauth2 JWT Authentication";
    @Setting
    private static final String AUTH_SETTING_ALLOWEDROLES = "edc.api.auth.oauth2.allowedRoles";
    @Setting
    private static final String ROLE_PROPERTY_NAME = "role";
    
    @Inject
    private IdentityService identityService;
    @Inject
    private ApiAuthenticationRegistry authenticationRegistry;
    @Inject
    private Vault vault;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        // Get participant ID
        String participantId = context.getParticipantId();

        // Retrieve list of allowed roles
        var rolesConfig = context.getConfig(AUTH_SETTING_ALLOWEDROLES);
        List<String> allowedRoles = rolesConfig.partition().map(conf -> conf.getString(ROLE_PROPERTY_NAME)).collect(Collectors.toList());

        authenticationRegistry.register("management-api", new Oauth2JwtAuthenticationService(identityService, participantId, allowedRoles));
    }
}
