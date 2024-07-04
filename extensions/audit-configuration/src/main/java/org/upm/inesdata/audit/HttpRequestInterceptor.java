package org.upm.inesdata.audit;

import org.eclipse.edc.spi.monitor.Monitor;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Enumeration;

@Provider
public class HttpRequestInterceptor implements ContainerRequestFilter {

    private final Monitor monitor;

    public HttpRequestInterceptor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Auditar la petición
        monitor.info("Intercepted Request: ");
        monitor.info("Method: " + requestContext.getMethod());
        monitor.info("URL: " + requestContext.getUriInfo().getRequestUri().toString());

        // Imprimir cabeceras
        for (String headerName : requestContext.getHeaders().keySet()) {
            monitor.info(headerName + ": " + String.join(", ", requestContext.getHeaders().get(headerName)));
        }
    }
}
