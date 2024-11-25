package org.upm.inesdata.auth;

import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(DependencyInjectionExtension.class)
public class Oauth2JwtAuthenticationExtensionTest {

    private static final String AUTH_SETTING_ALLOWEDROLES = "edc.api.auth.oauth2.allowedRoles";

    private final IdentityService identityService = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(IdentityService.class, identityService);
    }

    @Test
    public void testPrimaryMethod_loadKeyFromVault(ServiceExtensionContext context, Oauth2JwtAuthenticationExtension extension) {
        Map<String, String> configuration = Map.ofEntries(
            entry("1.role", "manager"),
            entry("2.role", "admin"));

        when(context.getConfig(AUTH_SETTING_ALLOWEDROLES)).thenReturn(ConfigFactory.fromMap(configuration));

        extension.initialize(context);

        verify(context)
                .getConfig(AUTH_SETTING_ALLOWEDROLES);
    }



}
