package org.upm.inesdata.api.shared.configuration.auth;

import org.eclipse.edc.api.auth.spi.AuthenticationService;
import org.eclipse.edc.api.auth.spi.registry.ApiAuthenticationRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.upm.inesdata.api.shared.configuration.auth.service.SharedOauth2JwtAuthenticationService;

/**
 * Extension that registers an SharedAuthenticationService that uses Oauth2 JWT Tokens
 */
@Provides(AuthenticationService.class)
@Extension(value = SharedOauth2JwtAuthenticationExtension.NAME)
public class SharedOauth2JwtAuthenticationExtension implements ServiceExtension {

    public static final String NAME = "Shared Oauth2 JWT Authentication";

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
        authenticationRegistry.register("shared-api",new SharedOauth2JwtAuthenticationService(identityService));
    }
}
