package org.upm.inesdata.auditevent;

import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

/**
 * Service extension for subscribing to audit events.
 * Registers the {@link AuditEventSubscriber} with the event router for both asynchronous and synchronous event handling.
 */
public class AuditEventSubscriptionExtension implements ServiceExtension {
    @Inject
    private EventRouter eventRouter;
    @Inject
    private Monitor monitor;

    /**
     * Initializes the service extension by registering the {@link AuditEventSubscriber} with the event router.
     *
     * @param context the service extension context providing configuration and services
     */
    @Override
    public void initialize(ServiceExtensionContext context) {
        eventRouter.register(Event.class, new AuditEventSubscriber(monitor, context.getParticipantId()));
        eventRouter.registerSync(Event.class, new AuditEventSubscriber(monitor, context.getParticipantId()));
    }
}
