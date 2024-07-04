package org.upm.inesdata.auditevent;

import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventEnvelope;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.monitor.Monitor;

public class AuditEventSubscriber implements EventSubscriber {

    private final Monitor monitor;

    public AuditEventSubscriber(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public <E extends Event> void on(EventEnvelope<E> event) {
        monitor.info("el evento"+event.getPayload().toString());
    }
}