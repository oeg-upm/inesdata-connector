package org.upm.inesdata.api.shared.configuration;

import org.eclipse.edc.api.auth.spi.AuthenticationRequestFilter;
import org.eclipse.edc.api.auth.spi.registry.ApiAuthenticationRegistry;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.SettingContext;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.system.ExecutorInstrumentation;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.jersey.providers.jsonld.JerseyJsonLdInterceptor;
import org.eclipse.edc.web.jersey.providers.jsonld.ObjectMapperProvider;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.WebServiceConfiguration;
import org.eclipse.edc.web.spi.configuration.WebServiceConfigurer;
import org.eclipse.edc.web.spi.configuration.WebServiceSettings;

import java.net.URI;

import static java.lang.String.format;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_PREFIX;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_SCHEMA;
import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * This extension provides generic endpoints which are open to all connectors.
 */
@Provides(SharedApiUrl.class)
@Extension(value = SharedApiConfigurationExtension.NAME)
public class SharedApiConfigurationExtension implements ServiceExtension {
    public static final String NAME = "Shared Public API";

    private static final int DEFAULT_SHARED_PORT = 8186;
    private static final String SHARED_CONTEXT_PATH = "/api/v1/shared";

    @SettingContext("Shared API context setting key")
    private static final String SHARED_CONFIG_KEY = "web.http.shared";

    @Setting(value = "Base url of the shared API endpoint without the trailing slash. This should correspond to the values configured " +
            "in '" + DEFAULT_SHARED_PORT + "' and '" + SHARED_CONTEXT_PATH + "'.", defaultValue = "http://<HOST>:" + DEFAULT_SHARED_PORT + SHARED_CONTEXT_PATH)
    private static final String SHARED_ENDPOINT = "edc.shared.endpoint";

    private static final WebServiceSettings SHARED_SETTINGS = WebServiceSettings.Builder.newInstance()
            .apiConfigKey(SHARED_CONFIG_KEY)
            .contextAlias("shared")
            .defaultPath(SHARED_CONTEXT_PATH)
            .defaultPort(DEFAULT_SHARED_PORT)
            .name(NAME)
            .build();

    private static final String SHARED_SCOPE = "SHARED_API";

    @Inject
    private WebServer webServer;
    @Inject
    private ApiAuthenticationRegistry authenticationRegistry;
    @Inject
    private WebServiceConfigurer webServiceConfigurer;
    @Inject
    private WebService webService;
    @Inject
    private ExecutorInstrumentation executorInstrumentation;
    @Inject
    private Hostname hostname;
    @Inject
    private IdentityService identityService;
    @Inject
    private JsonLd jsonLd;
    @Inject
    private TypeManager typeManager;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var config = context.getConfig(SHARED_CONFIG_KEY);
        var configuration = webServiceConfigurer.configure(config, webServer, SHARED_SETTINGS);

        context.registerService(SharedApiUrl.class, sharedApiUrl(context, configuration));

        var authenticationFilter = new AuthenticationRequestFilter(authenticationRegistry, "shared-api");
        webService.registerResource("shared", authenticationFilter);

        jsonLd.registerNamespace(ODRL_PREFIX, ODRL_SCHEMA, SHARED_SCOPE);
        var jsonLdMapper = typeManager.getMapper(JSON_LD);
        webService.registerResource("shared", new ObjectMapperProvider(jsonLdMapper));
        webService.registerResource("shared", new JerseyJsonLdInterceptor(jsonLd, jsonLdMapper, SHARED_SCOPE));
    }

    private SharedApiUrl sharedApiUrl(ServiceExtensionContext context, WebServiceConfiguration config) {
        var callbackAddress = context.getSetting(SHARED_ENDPOINT, format("http://%s:%s%s", hostname.get(), config.getPort(), config.getPath()));
        try {
            var url = URI.create(callbackAddress);
            return () -> url;
        } catch (IllegalArgumentException e) {
            context.getMonitor().severe("Error creating shared endpoint url", e);
            throw new EdcException(e);
        }
    }
}
