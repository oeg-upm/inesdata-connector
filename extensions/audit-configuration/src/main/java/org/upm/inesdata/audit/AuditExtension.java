package org.upm.inesdata.audit;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;

/**
 * Service extension for configuring audit logging.
 * Registers the {@link HttpRequestInterceptor} as a resource with the web service.
 */
@Provides(HttpRequestInterceptor.class)
@Extension(value = AuditExtension.NAME)
public class AuditExtension implements ServiceExtension {

    public static final String NAME = "Audit configuration";

    @Inject
    private WebService webService;

    @Inject
    private IdentityService identityService;

    /**
     * Returns the name of the extension.
     *
     * @return the name of the extension
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * Initializes the service extension by registering the {@link HttpRequestInterceptor} with the web service.
     *
     * @param context the service extension context providing configuration and services
     */
    @Override
    public void initialize(ServiceExtensionContext context) {
        webService.registerResource(ApiContext.MANAGEMENT, new HttpRequestInterceptor(context.getMonitor(), identityService, context.getParticipantId()));
    }
}
