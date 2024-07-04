package org.upm.inesdata.audit;

import jakarta.servlet.Filter;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

@Provides(HttpRequestInterceptor.class)
@Extension(value = AuditExtension.NAME)
public class AuditExtension implements ServiceExtension {

    public static final String NAME = "Audit configuration";


    @Inject
    private WebService webService;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        // Registrar el interceptor HTTP
        context.registerService(Filter.class, new HttpRequestInterceptor(context.getMonitor()));
        webService.registerResource(new HttpRequestInterceptor(context.getMonitor()));
        context.getMonitor().info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA - Registered Filter");
    }
}
