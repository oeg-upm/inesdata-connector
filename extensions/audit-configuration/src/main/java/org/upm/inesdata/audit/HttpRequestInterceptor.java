package org.upm.inesdata.audit;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.edc.spi.monitor.Monitor;

import java.io.IOException;
import java.util.Enumeration;

@WebFilter("/*")
public class HttpRequestInterceptor implements Filter {

    private final Monitor monitor;

    public HttpRequestInterceptor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización si es necesaria
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Auditar la petición
        monitor.info("Intercepted Request: ");
        monitor.info("Method: " + httpRequest.getMethod());
        monitor.info("URL: " + httpRequest.getRequestURL().toString());

        // Imprimir cabeceras
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            monitor.info(headerName + ": " + httpRequest.getHeader(headerName));
        }

        // Continuar con el siguiente filtro o recurso
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}