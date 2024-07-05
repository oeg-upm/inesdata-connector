package org.upm.inesdata.audit;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;

@Provides(HttpRequestInterceptor.class)
@Extension(value = AuditExtension.NAME)
public class AuditExtension implements ServiceExtension {

    public static final String NAME = "Audit configuration";


    @Inject
    private WebService webService;

    @Inject
    private IdentityService identityService;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        webService.registerResource(ApiContext.MANAGEMENT,new HttpRequestInterceptor(context.getMonitor(), identityService, context.getParticipantId()));
    }
}
