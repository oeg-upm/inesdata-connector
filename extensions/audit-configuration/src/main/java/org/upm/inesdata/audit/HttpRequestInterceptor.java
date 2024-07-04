package org.upm.inesdata.audit;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.edc.spi.monitor.Monitor;

import java.text.MessageFormat;

@Provider
public class HttpRequestInterceptor implements ContainerRequestFilter {

    private final Monitor monitor;

    public HttpRequestInterceptor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void filter(ContainerRequestContext requestContext){
        // Formatear y registrar el mensaje de auditoría
        String auditLog = MessageFormat.format("[AUDIT][MANAGEMENT] User ''{0}'' calls ''{1}''",
            "USER",
            requestContext.getUriInfo().getRequestUri().toString());
        monitor.info(auditLog);
    }
}
